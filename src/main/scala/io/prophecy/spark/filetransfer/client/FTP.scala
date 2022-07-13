package io.prophecy.spark.filetransfer.client

import io.prophecy.spark.filetransfer.client.util.FileTransferOptions
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SaveMode

class FTP(options: FileTransferOptions) extends BaseClient with Logging {
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
    // TODO:  @anshuman - Implement FTP upload
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
    // TODO:  @anshuman - Implement FTP download
  }
}