import com.jcraft.jsch.{ChannelSftp, JSch}
import org.apache.spark.sql.SparkSession

import java.io.File
import scala.jdk.CollectionConverters.CollectionHasAsScala

object Main {
  def main(args: Array[String]): Unit = {
    sealed trait FileMode
    object FileMode {
      case object Text extends FileMode
      case object Binary extends FileMode
    }

    val temporaryPath = "/Users/maciej/Developer/prophecy/prophecy-spark-sftp/out"
    new File(temporaryPath).mkdirs()

    val hostname = "prophecy.files.com"
    val username = "maciej@prophecy.io"
    val password = "tpd*qeb3fgj0XWY-nyw"

    val fileMode: FileMode = FileMode.Text

    val path = "/folder"

    val client = new JSch()
    val session = client.getSession(username, hostname)
    session.setConfig("StrictHostKeyChecking", "no")
    session.setPassword(password)
    session.connect()

    val channel = session.openChannel("sftp").asInstanceOf[ChannelSftp]
    channel.connect()

    // Shallow files list
    // Super old library, without generic types :(
    val files = channel.ls(path).asScala.toList.asInstanceOf[List[ChannelSftp#LsEntry]]
    println(s"Found ${files.length} sftp files under $path")

    files
      .filter { file =>
        val attributes = file.getAttrs
        !attributes.isDir && attributes.isReg
      }
      .foreach { file =>
        val sourcePath = new File(path, file.getFilename)
        val targetPath = new File(temporaryPath, file.getFilename)

        println(s"Downloading ${sourcePath.getAbsolutePath} to a temporary location ${targetPath.getAbsolutePath}")
        channel.get(sourcePath.getAbsolutePath, targetPath.getAbsolutePath)
      }

    channel.disconnect()
    session.disconnect()

    val spark = SparkSession.builder.master("local").getOrCreate()
    val df = fileMode match {
      case FileMode.Text   => spark.read.text(temporaryPath)
      case FileMode.Binary => spark.read.format("binaryFile").load(temporaryPath)
    }

    df.show()
  }
}
