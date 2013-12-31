package jp.mwsoft.wikipedia.categorizer

import java.io.File
import java.net.URL

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.asScalaBuffer

import org.apache.commons.io.FileUtils

import jp.mwsoft.wikipedia.categorizer.common.Controls

object Main extends App {

  val usage = "Usage : ./activator [url | file path]"

  if (args.length == 0) {
    println(usage)
    exit(0)
  }

  def isUrl(str: String) =
    str.startsWith("http") &&
      (try { Some(new URL(str)) } catch { case e: Throwable => None }).isDefined

  Controls.managed(new MltSearcher()) { searcher =>

    val searchResultOpt =
      if (isUrl(args(0))) Some(searcher.urlSearch(args(0)))
      else if (new File(args(0)).exists) Some(searcher.fileSearch(new File(args(0))))
      else {
        println(usage)
        None
      }

    println("━━━━━━━【query】━━━━━━━━")
    println(searcher.previousQuery)

    for (searchResult <- searchResultOpt) {
      val categoryMap = FileUtils.readLines(new File("data/category_map.txt"))
        .map(_.split("\t")).filter(_.size > 1)
        .map(x => x(0) -> x(1).split(","))
        .toMap

      val categories =
        (for (page <- searchResult)
          yield page.categories.map(_ -> page.score))
          .flatMap(x => x)
          .groupBy(x => x._1)
          .map(x => x._1 -> x._2.map(_._2).sum)
          .toList.sortBy(_._2).reverse

      println("━━━━━━━【wikipedia categories】━━━━━━━━")
      categories.slice(0, 10) foreach println

      val convertCategories =
        categories.flatMap(x => categoryMap.getOrElse(x._1, Array()).map(_ -> x._2))
          .groupBy(x => x._1)
          .map(x => x._1 -> x._2.map(_._2).sum)
          .toList.sortBy(_._2).reverse

      println("━━━━━━━【my categories】━━━━━━━━")
      convertCategories.slice(0, 10) foreach println
    }
  }
}