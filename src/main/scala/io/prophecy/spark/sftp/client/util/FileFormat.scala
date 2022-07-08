package io.prophecy.spark.sftp.client.util

/**
  * Supported file formats to read/write data via Spark '''DataFrame API'''.
  *
  *  1. [[FileFormat#avro avro]]
  *  1. [[FileFormat#csv csv]]
  *  1. [[FileFormat#json json]]
  *  1. [[FileFormat#orc orc]]
  *  1. [[FileFormat#parquet parquet]]
  *  1. [[FileFormat#text text]]
  *  1. [[FileFormat#xml xml]]
  *
  * @since 0.1.0
  */
object FileFormat extends Enumeration {
  type FileFormat = Value
  val avro, csv, json, orc, parquet, text, xml = Value
}
