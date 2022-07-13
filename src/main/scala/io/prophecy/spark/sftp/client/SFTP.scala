package io.prophecy.spark.sftp.client

import io.prophecy.spark.sftp.client.util.WriteMode.WriteMode
import io.prophecy.spark.sftp.client.util.{FileTransferOptions, FileUtils, WriteMode}
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.{FileMode, SFTPClient, SFTPException}
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SaveMode

import java.io.File
import java.nio.file.{FileSystems, Paths}
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.util.Try

class SFTP(options: FileTransferOptions) extends BaseClient with Logging {
  private val UNIX_PATH_SEPARATOR: String = "/"

  /**
   * Uploads local files to remote host.
   *
   * @param src  Local file/directory path.
   * @param dest Remote directory path.
   * @param mode Spark DataFrame Write Mode.
   * @return Returns unit of successful upload.
   * @since 0.1.0
   */
  def upload(
              src: String,
              dest: String,
              mode: SaveMode = SaveMode.Overwrite
            ): Unit = {
    val srcPath: File = new File(src)
    val files: List[File] = {
      Try(srcPath.listFiles().toList).getOrElse(List(srcPath))
    }
    val filesSize = files.size
    val destExt: String = {
      Try(FileUtils.getFileExt(new File(dest))).getOrElse("")
    }
    if (destExt.nonEmpty && filesSize > 1) {
      sys.error(
        s"""
           |Too many source files '$filesSize' found to write to target file path '$dest'!!
           |Please provide directory path on remote machine or combine the source files to
           |a single file.
          """.stripMargin.trim
      )
    }
    if (destExt.nonEmpty && filesSize == 1) {
      val srcExt: String = FileUtils.getFileExt(files.head)
      if (srcExt != destExt)
        sys.error(
          s"Source '$srcExt' and Target '$destExt' file extensions mismatch!!"
        )
    }

    val client: SFTPClient = connect
    try {
      var path: String = ""
      var destDirs: List[String] = dest.split(UNIX_PATH_SEPARATOR).toList
      if (destExt.nonEmpty) destDirs = destDirs.dropRight(1)
      for (dir <- destDirs) {
        path = path + UNIX_PATH_SEPARATOR + dir
        Try(client.mkdir(path))
      }

      files.foreach(x => {
        val source: String = x.getCanonicalPath
        val target: String = if (filesSize == 1 && destExt.nonEmpty) {
          dest
        } else {
          dest + UNIX_PATH_SEPARATOR + x.getName
        }
        log.info("Uploading file from " + source + " to " + target)
        val uploadMode: WriteMode = getPutMode(client, target, mode)
        log.info("Using upload mode: " + uploadMode)

        if (!uploadMode.equals(WriteMode.IGNORE)) {
          if (uploadMode.equals(WriteMode.OVERWRITE) && client.statExistence(target) != null)
            client.rm(target)
          client.put(source, target)
        }
      })

    } finally {
      disconnect(client)
    }
  }

  /**
   * Downloads files from remote host.
   *
   * @param src  Remote file/directory path.
   * @param dest Local directory path.
   * @return Returns unit of successful download.
   * @since 0.1.0
   */
  override def download(src: String, dest: String): Unit = {
    val client = connect
    var files: List[String] = List(src)
    try {
      if (FileUtils.isFilePathGlob(src)) {
        val pathComponents = client.getSFTPEngine.getPathHelper.getComponents(src)
        val parent = pathComponents.getParent
        val glob = FileSystems.getDefault.getPathMatcher("glob:" + src)
        files = client.ls(parent)
          .filter(x => glob.matches(Paths.get(x.getPath)))
          .map(x => parent + UNIX_PATH_SEPARATOR + x.getName)
          .toList
      }
      else if (client.stat(src).getType == FileMode.Type.DIRECTORY) {
        files = client
          .ls(src)
          .filterNot(x => {
            Set(".", "..").contains(x.getName) || x.isDirectory
          })
          .map(x => src + UNIX_PATH_SEPARATOR + x.getName)
          .toList
      }
      for (source <- files) {
        val target: String = dest + File.separator + Paths
          .get(source)
          .getFileName
        log.info("Downloading file from " + source + " to " + target)
        client.get(source, target)
      }
    } finally {
      disconnect(client)
    }
  }

  private def connect: SFTPClient = {
    val client = new SSHClient
    client.addHostKeyVerifier(new PromiscuousVerifier)
    client.connect(options.host)
    client.authPassword(options.username, options.password)
    val sftpClient = client.newSFTPClient()
    sftpClient
  }

  private def disconnect(channel: SFTPClient): Unit = {
    channel.close()
    channel.getSFTPEngine.close()
  }

  /**
   * Gets the SFTP upload mode based on the provided Spark DataFrameWriter save mode.
   *
   * @param client SFTP client instance.
   * @param target Target file path to upload on the remote machine.
   * @param mode   Spark DataFrame save mode to determine the file upload mode.
   * @return SFTP upload mode.
   * @since 0.3.0
   */
  def getPutMode(
                  client: SFTPClient,
                  target: String,
                  mode: SaveMode
                ): WriteMode = mode match {
    case x@(SaveMode.ErrorIfExists | SaveMode.Ignore) =>
      var fileExists: Boolean = false
      try {
        fileExists = !client.ls(target).isEmpty
      } catch {
        case ex: SFTPException =>
          fileExists = false
      }
      if (fileExists) {
        if (x == SaveMode.ErrorIfExists) {
          sys.error(
            s"Target file '$target' already exists on remote host '${options.host}'!!"
          )
        } else {
          log.info(
            s"Ignoring target file '$target' write as it already exists on remote host '${options.host}'!!"
          )
          WriteMode.IGNORE
        }
      } else {
        WriteMode.OVERWRITE
      }
    case SaveMode.Append => WriteMode.APPEND
    case SaveMode.Overwrite => WriteMode.OVERWRITE
  }
}

