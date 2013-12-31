package jp.mwsoft.wikipedia.categorizer.common

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

import scala.collection.Iterator

class FileLineIterable(file: File) extends Iterable[String] with Closeable {
  def this(path: String) = this(new File(path))
  val lineIterator = new FileLineIterator(file)
  def iterator = lineIterator
  def close() = lineIterator.close()
}

class GzipFileLineIterable(file: File) extends Iterable[String] with Closeable {
  def this(path: String) = this(new File(path))
  val lineIterator = new GzipFileLineIterator(file)
  def iterator = lineIterator
  def close() = lineIterator.close()
}

class FileLineIterator(infile: File) extends FileLineIteratorLike {
  def this(path: String) = this(new File(path))

  val file = infile
  def getReader() = new BufferedReader(new FileReader(file))
}

class GzipFileLineIterator(infile: File) extends FileLineIteratorLike {
  def this(path: String) = this(new File(path))

  val file = infile
  def getReader() = new BufferedReader(new InputStreamReader(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)))))
}

trait FileLineIteratorLike extends Iterator[String] {

  val file: File
  var reader: BufferedReader = null
  var nextLine: String = ""

  def getReader(): BufferedReader

  override def hasNext: Boolean = this.synchronized {
    if (nextLine == null && reader != null) reader.close()
    nextLine != null
  }

  override def next: String = this.synchronized {
    try {
      if (reader == null) {
        reader = getReader()
        nextLine = reader.readLine()
      }
      val line = nextLine
      nextLine = reader.readLine()
      if (nextLine == null) reader.close()
      line
    } catch {
      case e: IOException => {
        if (reader != null) reader.close()
        throw e
      }
    }
  }

  def close(): Unit = this.synchronized {
    if (reader != null) reader.close
  }
}
