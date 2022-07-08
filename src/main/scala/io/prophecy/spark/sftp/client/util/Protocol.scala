package io.prophecy.spark.sftp.client.util

/**
  * Supported file transfer protocols to upload/download content.
  *
  *  1. [[Protocol#ftp ftp]]
  *  1. [[Protocol#scp scp]]
  *  1. [[Protocol#sftp sftp]]
  *
  * @since 0.1.0
  */
object Protocol extends Enumeration {
  type Protocol = Value
  val ftp, scp, sftp = Value
}
