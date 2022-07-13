package io.prophecy.spark.filetransfer.client.util

/**
 * Supported write modes of data data via Spark '''DataFrame API'''.
 *
 *     1. [[WriteMode#OVERWRITE overwrite existing file]]
 *     2. [[WriteMode#APPEND append to existing file]]
 *     2. [[WriteMode#IGNORE ignore write if file exists]]
 *
 * @since 0.1.0
 */
object WriteMode extends Enumeration {
  type WriteMode = Value
  val OVERWRITE, APPEND, IGNORE = Value

}
