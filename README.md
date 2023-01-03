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
  * show the [RFC](https://www.rfc-editor.org/rfc/rfc7807)
* observation
 * new unified `Observation` api 
 * show tracing and metrics in actuator
* buildpacks
* aot + graalvm
  * java is already amazing. did u know its ranked the 4th most energy effivient language after the likes of C and Go? Python's 70x less efficient!
  * it owes its incredible speed to the garbage collector and JIT
  * did you see this? the _original_ Java garbage collector  https://twitter.com/jtannady/status/981547257479778307?lang=en 
  * the JIT is amazing! it works by creating a closed world assumption of your apps. it needs to remove all _fun_. If it can do that, it can produce applications that fly
  * but what happens when it has issues? somebody needs to provide that configuration! enter spring boot 3 aot engine
  * kick off a build
* aot native image builds takes a long time
  * we asked them to play elevator music https://github.com/joshlong/bootiful-spring-boot-3/edit/main/README.md
  * then look at the replies
 * client
 * reactive web, gateway, graphql
 * declarative http clients

