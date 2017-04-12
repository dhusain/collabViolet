
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/proj/simpleserver/conf/routes
// @DATE:Tue Nov 29 19:22:31 UTC 2016

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseApplication Application = new controllers.ReverseApplication(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseApplication Application = new controllers.javascript.ReverseApplication(RoutesPrefix.byNamePrefix());
  }

}
