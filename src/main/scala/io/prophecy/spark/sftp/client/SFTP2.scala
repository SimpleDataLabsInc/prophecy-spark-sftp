package io.prophecy.spark.sftp.client

import io.prophecy.spark.sftp.client.util.FileTransferOptions
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.{FileMode, SFTPClient}
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SaveMode

import java.io.File
import java.nio.file.Paths
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

class SFTP2(options: FileTransferOptions) extends BaseClient with Logging {
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
  override def upload(src: String, dest: String, mode: SaveMode): Unit = {

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
      if (client.stat(src).getType == FileMode.Type.DIRECTORY) {
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
}

