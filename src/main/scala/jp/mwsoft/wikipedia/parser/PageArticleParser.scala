package jp.mwsoft.wikipedia.parser

import java.io.BufferedInputStream
import java.io.Closeable
import java.io.FileInputStream

import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.joda.time.format.DateTimeFormat

import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.events.XMLEvent

case class PageArticle(
  var id: Long = -1,
  var title: String = null,
  var lastUpdate: Long = -1,
  var text: String = null,
  var categories: List[String] = null) {

  val reCategory = new Regex("""\[\[(?i)Category\:([^\|]+)\|?.*?]\]""", "category")

  def setText(text: String) {
    this.text = text
    val categories = ArrayBuffer[String]()
    for (line <- text.split("\n"); m <- reCategory.findFirstMatchIn(line)) {
      categories += m.group("category")
    }
    this.categories = categories.toList
  }
}

/**
 * parse jawiki-latest-pages-articles.xml.bz2
 */
class PageArticleParser(path: String) extends Iterator[PageArticle] with Closeable {

  val factory = XMLInputFactory.newInstance()
  var reader: XMLEventReader = null
  var nextElem: PageArticle = null

  def hasNext: Boolean = this.synchronized { reader == null || nextElem != null }

  def next: PageArticle = this.synchronized {
    if (reader == null) {
      reader = factory.createXMLEventReader(
        new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(path))));
      readElem()
    }
    val elem = nextElem
    readElem()
    elem
  }

  val dtFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
  var parentElem = ""

  private def readElem(): Unit = if (reader.hasNext) {
    val event = reader.nextEvent()

    if (isStartElem(event, "page")) {
      parentElem = "page"
      nextElem = PageArticle()
    } else if (isStartElem(event, "id") && parentElem == "page") {
      nextElem.id = getText("id").toLong
    } else if (isStartElem(event, "title")) {
      nextElem.title = getText("title")
    } else if (isStartElem(event, "timestamp")) {
      nextElem.lastUpdate = dtFormat.parseMillis(getText("timestamp"))
    } else if (isStartElem(event, "text")) {
      nextElem.setText(getText("text"))
    } else if (isStartElem(event, "revision")) {
      parentElem = "revision"
    } else if (isEndElem(event, "revision")) {
      parentElem = "page"
    } else if (isStartElem(event, "contributor")) {
      parentElem = "contributor"
    } else if (isEndElem(event, "contributor")) {
      parentElem = "revision"
    }

    if (!isEndElem(event, "page")) readElem
  } else {
    nextElem = null
  }

  private def getText(name: String): String = {
    val builder = new StringBuilder();
    def getTextLoop: Unit = if (reader.hasNext) {
      val event = reader.nextEvent()
      if (event.getEventType() == XMLStreamConstants.CHARACTERS)
        builder.append(event.asCharacters.getData.trim)
      if (!isEndElem(event, name))
        getTextLoop
    }
    getTextLoop
    builder.toString
  }

  private def isStartElem(event: XMLEvent, name: String): Boolean = {
    event.getEventType() == XMLStreamConstants.START_ELEMENT &&
      name.equals(event.asStartElement.getName.getLocalPart)
  }

  private def isEndElem(event: XMLEvent, name: String): Boolean = {
    event.getEventType() == XMLStreamConstants.END_ELEMENT &&
      name.equals(event.asEndElement.getName.getLocalPart)
  }

  def close() {
    if (reader != null) reader.close()
  }
}