
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/proj/simpleserver/conf/routes
// @DATE:Tue Nov 29 19:22:31 UTC 2016


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
