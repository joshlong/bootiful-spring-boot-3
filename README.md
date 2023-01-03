# Bootiful Spring Boot 3

## servicve
* start.spring.io (h2, jdbc, web, actuator)
* java 17 
  * 'technically superior' / 'morally superior' 
* records 
  * 'look ma! no lombok!'
* service 
* controller 
  * make sure to do a `@Controller` + `@ResponseBody`, _not_ `RouterFunction` for consistency with the rest of the talk
* error handling
  * show that the new code uses `HttpServletRequest` from `jakarta.\*`, not `javax.\*`
  * this is because Oracle ghosted the community
  * change is hard! now's a good time to clear the air on another major, breaking change in Sprng Boot 3: [no more animated ASCII art](https://raw.githubusercontent.com/snicoll-demos/demo-animated-banner/master/src/main/resources/banner.gif )! show the 2.7 feature turning this `.gif` into a banner 
  * now, granted, that feature isn't great for serverless...
* controller advice
* problem details
  * show the RFC
* observation
* buildpacks
* aot + graalvm
  * og java garbage collector  https://twitter.com/jtannady/status/981547257479778307?lang=en
* takes a long time
* 	elevator music
* client
* reactive web, gateway, graphql
* make sure to use snapshot for s-c
* declarative http clients

