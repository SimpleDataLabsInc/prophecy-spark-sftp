package io.prophecy.spark

import io.prophecy.spark.filetransfer.client.util.{FileTransferOptions, Protocol}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter}

package object filetransfer {

  /**
   * Adds methods for each supported [[Protocol Protocol]],
   * to DataFrameReader that allows you to read remote files via the selected protocol.
   *
   * @constructor Provides implicits to `DataFrameReader` for supported protocols.
   * @param reader a DataFrameReader
   * @since 0.2.0
   */
  implicit class ProtocolDataFrameReader(reader: DataFrameReader) {
    def sftp: String => DataFrame = {
      reader
        .format("io.prophecy.spark.filetransfer")
        .option(FileTransferOptions.PROTOCOL, Protocol.sftp.toString)
        .load
    }
  }

  /**
   * Adds methods for each supported [[Protocol Protocol]],
   * to DataFrameWriter that allows you to write remote files via the selected protocol.
   *
   * @constructor Provides implicits to `DataFrameWriter` for supported protocols.
   * @param writer a DataFrameWriter
   * @since 0.2.0
   */
  implicit class ProtocolDataFrameWriter[T](writer: DataFrameWriter[T]) {
    def sftp: String => Unit = {
      writer
        .format("io.prophecy.spark.filetransfer")
        .option(FileTransferOptions.PROTOCOL, Protocol.sftp.toString)
        .save
    }
  }

}
