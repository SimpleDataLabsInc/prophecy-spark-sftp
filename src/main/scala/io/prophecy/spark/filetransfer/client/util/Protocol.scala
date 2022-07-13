package io.prophecy.spark.filetransfer.client.util

/**
  * Supported file transfer protocols to upload/download content.
  *
  *  1. [[Protocol#ftp ftp]]
  *  2. [[Protocol#sftp sftp]]
  *
  * @since 0.1.0
  */
object Protocol extends Enumeration {
  type Protocol = Value
  val ftp, sftp = Value
}
