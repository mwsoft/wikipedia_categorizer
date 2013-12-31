package jp.scaleout.dw.category.models

import java.sql.Timestamp

import scala.slick.driver.H2Driver.simple.Table

case class CategoryLink(
    clFrom: Int, clTo: String, clSortkey: String, clTimestamp: Timestamp,
    clSortkeyPrefix: String, clCollation: String, clType: String)

object CategoryLinks extends Table[CategoryLink]("categorylinks") {

  def clFrom = column[Int]("cl_from")
  def clTo = column[String]("cl_to")
  def clSortkey = column[String]("cl_sortkey")
  def clTimestamp = column[Timestamp]("cl_timestamp")
  def clSortkeyPrefix = column[String]("cl_sortkey_prefix")
  def clCollation = column[String]("cl_collation")
  def clType = column[String]("cl_type")

  def * = clFrom ~ clTo ~ clSortkey ~ clTimestamp ~ clSortkeyPrefix ~  
  	clCollation ~ clType <> (CategoryLink, CategoryLink.unapply _)
}