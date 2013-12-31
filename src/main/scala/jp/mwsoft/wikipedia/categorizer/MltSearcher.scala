package jp.mwsoft.wikipedia.categorizer

import java.io.Closeable
import java.io.File
import java.io.FileReader
import java.io.Reader
import java.io.StringReader

import scala.Array.canBuildFrom

import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.queries.mlt.MoreLikeThis
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.FSDirectory
import org.jsoup.Jsoup

import jp.mwsoft.wikipedia.categorizer.common.Controls

case class MltResult(id: Int, title: String, categories: Array[String], score: Float)

class MltSearcher extends Closeable {

  var previousQuery: String = null

  val indexReader = DirectoryReader.open(FSDirectory.open(new File(Globals.indexPath)))

  def close() {
    indexReader.close()
  }

  def urlSearch(url: String): List[MltResult] = {
    val content = Jsoup.connect(url).get.text()
    val contentReader = new StringReader(content)
    search(contentReader)
  }

  def fileSearch(file: File): List[MltResult] = {
    Controls.managed(new FileReader(file)) { reader => search(reader) }
  }

  def search(contentReader: Reader): List[MltResult] = {
    val searcher = new IndexSearcher(indexReader)
    val query = createQuery(indexReader, contentReader)
    previousQuery = query.toString

    val collector = TopScoreDocCollector.create(1000, false)
    searcher.search(query, collector)

    (for (hit <- collector.topDocs(0, 50).scoreDocs) yield {
      val doc = searcher.doc(hit.doc)
      MltResult(
        doc.get("id").toString.toInt,
        doc.get("title").toString,
        doc.get("categories").toString.split("\t"),
        hit.score)
    }).toList
  }

  def createQuery(indexReader: IndexReader, contentReader: Reader): Query = {
    val mlt = new MoreLikeThis(indexReader)
    mlt.setMinTermFreq(1)
    mlt.setMinDocFreq(1)
    mlt.setMaxQueryTerms(50)
    mlt.setMaxDocFreq(100000)
    mlt.setFieldNames(Array("title", "text"))
    mlt.setAnalyzer(Globals.analyzer)
    mlt.setBoost(true)
    mlt.like(contentReader, "text")
  }
}