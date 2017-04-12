
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/proj/simpleserver/conf/routes
// @DATE:Tue Nov 29 19:22:31 UTC 2016

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._

import _root_.controllers.Assets.Asset
import _root_.play.libs.F

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:1
  Application_0: controllers.Application,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:1
    Application_0: controllers.Application
  ) = this(errorHandler, Application_0, "/")

  import ReverseRouteContext.empty

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, Application_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """hello/""" + "$" + """name<[^/]+>""", """controllers.Application.greet(name:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """new""", """controllers.Application.newSession()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """list""", """controllers.Application.listSessions()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """connect/""" + "$" + """ID<[^/]+>""", """controllers.Application.connect(ID:Integer)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """add""", """controllers.Application.add(id:Integer ?= 0, name:String ?= "")"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:1
  private[this] lazy val controllers_Application_greet0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("hello/"), DynamicPart("name", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Application_greet0_invoker = createInvoker(
    Application_0.greet(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "greet",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """hello/""" + "$" + """name<[^/]+>"""
    )
  )

  // @LINE:2
  private[this] lazy val controllers_Application_newSession1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("new")))
  )
  private[this] lazy val controllers_Application_newSession1_invoker = createInvoker(
    Application_0.newSession(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "newSession",
      Nil,
      "GET",
      """""",
      this.prefix + """new"""
    )
  )

  // @LINE:3
  private[this] lazy val controllers_Application_listSessions2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("list")))
  )
  private[this] lazy val controllers_Application_listSessions2_invoker = createInvoker(
    Application_0.listSessions(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "listSessions",
      Nil,
      "GET",
      """""",
      this.prefix + """list"""
    )
  )

  // @LINE:4
  private[this] lazy val controllers_Application_connect3_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("connect/"), DynamicPart("ID", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Application_connect3_invoker = createInvoker(
    Application_0.connect(fakeValue[Integer]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "connect",
      Seq(classOf[Integer]),
      "GET",
      """""",
      this.prefix + """connect/""" + "$" + """ID<[^/]+>"""
    )
  )

  // @LINE:5
  private[this] lazy val controllers_Application_add4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("add")))
  )
  private[this] lazy val controllers_Application_add4_invoker = createInvoker(
    Application_0.add(fakeValue[Integer], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "add",
      Seq(classOf[Integer], classOf[String]),
      "GET",
      """""",
      this.prefix + """add"""
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:1
    case controllers_Application_greet0_route(params) =>
      call(params.fromPath[String]("name", None)) { (name) =>
        controllers_Application_greet0_invoker.call(Application_0.greet(name))
      }
  
    // @LINE:2
    case controllers_Application_newSession1_route(params) =>
      call { 
        controllers_Application_newSession1_invoker.call(Application_0.newSession())
      }
  
    // @LINE:3
    case controllers_Application_listSessions2_route(params) =>
      call { 
        controllers_Application_listSessions2_invoker.call(Application_0.listSessions())
      }
  
    // @LINE:4
    case controllers_Application_connect3_route(params) =>
      call(params.fromPath[Integer]("ID", None)) { (ID) =>
        controllers_Application_connect3_invoker.call(Application_0.connect(ID))
      }
  
    // @LINE:5
    case controllers_Application_add4_route(params) =>
      call(params.fromQuery[Integer]("id", Some(0)), params.fromQuery[String]("name", Some(""))) { (id, name) =>
        controllers_Application_add4_invoker.call(Application_0.add(id, name))
      }
  }
}
