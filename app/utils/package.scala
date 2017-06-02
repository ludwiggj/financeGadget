package object utils {
  val dataHome = "data"
  val holdingsHome = "holdings"

  def time[R](name: String, block: => R): R = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    println(s"Elapsed time ($name): " + (t1 - t0) / Math.pow(10, 9) + "s")
    result
  }
}