package jp.mwsoft.wikipedia.categorizer

import java.io.FileReader

import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.analysis.ja.JapaneseTokenizer
import org.apache.lucene.analysis.ja.dict.UserDictionary
import org.apache.lucene.util.Version

object Globals {

  val analyzer = new JapaneseAnalyzer(Version.LUCENE_46,
    new UserDictionary(new FileReader("data/userdic.txt")),
    JapaneseTokenizer.Mode.NORMAL,
    JapaneseAnalyzer.getDefaultStopSet,
    JapaneseAnalyzer.getDefaultStopTags)

  val indexPath = "index"

  val pageArticleXml = "data/jawiki-latest-pages-articles.xml.bz2"

}