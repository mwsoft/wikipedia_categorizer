package jp.mwsoft.wikipedia.categorizer.common

import java.io.Closeable

object Controls {

  def managed[T, U <: Closeable](resource: U)(f: U => T): T = {
    try f(resource)
    finally resource.close()
  }

  def managed[T, U <: Closeable, V <: Closeable](resource1: U, resource2: V)(f: (U, V) => T): T = {
    try f(resource1, resource2)
    finally {
      resource1.close()
      resource2.close()
    }
  }

  def tryOpt[T](f: => T): Option[T] = {
    try Some(f) catch { case e: Throwable => None }
  }

  def retry[T](retryCount: Int)(f: => T): T = {
    try f
    catch {
      case e: Throwable => {
        if (retryCount == 0) throw e
        else retry(retryCount - 1)(f)
      }
    }
  }

  def retryOpt[T](retryCount: Int)(f: => T): Option[T] = {
    tryOpt(retry(retryCount)(f))
  }

}