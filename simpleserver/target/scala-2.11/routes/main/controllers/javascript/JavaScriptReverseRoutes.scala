
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/proj/simpleserver/conf/routes
// @DATE:Tue Nov 29 19:22:31 UTC 2016

import play.api.routing.JavaScriptReverseRoute
import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:1
package controllers.javascript {
  import ReverseRouteContext.empty

  // @LINE:1
  class ReverseApplication(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:1
    def greet: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.greet",
      """
        function(name0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "hello/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("name", encodeURIComponent(name0))})
        }
      """
    )
  
    // @LINE:5
    def add: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.add",
      """
        function(id0,name1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "add" + _qS([(id0 == null ? null : (""" + implicitly[QueryStringBindable[Integer]].javascriptUnbind + """)("id", id0)), (name1 == null ? null : (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("name", name1))])})
        }
      """
    )
  
    // @LINE:4
    def connect: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.connect",
      """
        function(ID0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "connect/" + (""" + implicitly[PathBindable[Integer]].javascriptUnbind + """)("ID", ID0)})
        }
      """
    )
  
    // @LINE:3
    def listSessions: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.listSessions",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "list"})
        }
      """
    )
  
    // @LINE:2
    def newSession: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.newSession",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "new"})
        }
      """
    )
  
  }


}
