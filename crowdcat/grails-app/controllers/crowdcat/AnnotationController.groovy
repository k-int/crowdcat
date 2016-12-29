package crowdcat

import grails.plugin.springsecurity.annotation.Secured
import grails.converters.*

class AnnotationController {

  def springSecurityService
  def resourceAgentService

  def index() {
    log.debug("annotation::index");

    if ( request.method=='POST' ) {
      log.debug("Create Annotation ${params}");
      log.debug(request.JSON?.toString())
    }
    else {
      log.debug("Get ${params}");
    }

    def result = [:]
    render result as JSON
  }

}
