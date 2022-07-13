package io.prophecy.spark.filetransfer.client.util

/**
  * Supported file formats to read/write data via Spark '''DataFrame API'''.
  *
  *  1. [[FileFormat#csv csv]]
  *  2. [[FileFormat#text text]]
  *
  * @since 0.1.0
  */
object FileFormat extends Enumeration {
  type FileFormat = Value
  val csv, text = Value
}
