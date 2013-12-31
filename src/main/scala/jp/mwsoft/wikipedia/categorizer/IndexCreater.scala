package jp.mwsoft.wikipedia.categorizer

import java.io.File

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version

import jp.mwsoft.wikipedia.categorizer.common.Controls
import jp.mwsoft.wikipedia.parser.PageArticleParser

object IndexCreater extends App {

  val indexPath = new File(Globals.indexPath)
  if (indexPath.exists) {
    println(s"${Globals.indexPath} directory is already exist.")
    exit(0)
  }

  if (!new File(Globals.pageArticleXml).exists) {
    println(s"${Globals.pageArticleXml} is not exist.")
  }

  var counter = 0

  val writerConfig = new IndexWriterConfig(Version.LUCENE_46, Globals.analyzer)
  val writer = new IndexWriter(FSDirectory.open(indexPath), writerConfig)
  try Controls.managed(new PageArticleParser(Globals.pageArticleXml)) { parser =>
    for (
      page <- parser;
      if page.title != null;
      if !page.title.trim.isEmpty;
      if !page.title.startsWith("Wikipedia:");
      if !page.title.startsWith("Help:");
      if !page.title.startsWith("ファイル:");
      if !page.title.contains("曖昧さ回避");
      if page.categories.length > 0
    ) {
      val doc = new Document()
      doc.add(new StringField("id", page.id.toString, Field.Store.YES))
      val titleField = new TextField("title", page.title, Field.Store.YES)
      titleField.setBoost(5.0f)
      doc.add(titleField)
      doc.add(new TextField("text", page.text, Field.Store.NO))
      doc.add(new StringField("categories", page.categories.mkString("\t"), Field.Store.YES))
      writer.addDocument(doc)

      if ({ counter += 1; counter } % 10000 == 0) println(counter)
    }
  } finally {
    writer.commit()
    writer.close()
  }
}