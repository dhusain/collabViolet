
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/proj/simpleserver/conf/routes
// @DATE:Tue Nov 29 19:22:31 UTC 2016

import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:1
package controllers {

  // @LINE:1
  class ReverseApplication(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:1
    def greet(name:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "hello/" + implicitly[PathBindable[String]].unbind("name", dynamicString(name)))
    }
  
    // @LINE:5
    def add(id:Integer = 0, name:String = ""): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "add" + queryString(List(if(id == 0) None else Some(implicitly[QueryStringBindable[Integer]].unbind("id", id)), if(name == "") None else Some(implicitly[QueryStringBindable[String]].unbind("name", name)))))
    }
  
    // @LINE:4
    def connect(ID:Integer): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "connect/" + implicitly[PathBindable[Integer]].unbind("ID", ID))
    }
  
    // @LINE:3
    def listSessions(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "list")
    }
  
    // @LINE:2
    def newSession(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "new")
    }
  
  }


}
