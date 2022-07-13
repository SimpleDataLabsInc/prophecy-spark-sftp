package io.prophecy.spark.sftp.client.util

/**
 * Supported write modes of data data via Spark '''DataFrame API'''.
 *
 *  1. [[WriteMode#OVERWRITE OVERWRITE]]
 *     2. [[WriteMode#APPEND APPEND]]
 *
 * @since 0.1.0
 */
object WriteMode extends Enumeration {
  type WriteMode = Value
  val OVERWRITE, APPEND, IGNORE = Value

}
