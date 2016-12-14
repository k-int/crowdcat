package crowdcat

import grails.plugin.springsecurity.annotation.Secured

class HomeController {

  def springSecurityService

  def index() { 
    def result = [:]
    def user = springSecurityService.currentUser
    if ( user ) {
      result.unilist = Universe.list()
      render(view:'loggedInIndex', model:result)
    }
    else {
    }
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def login() {
    redirect action:'index'
  }

  @Secured(['ROLE_INST_ADM', 'IS_AUTHENTICATED_FULLY'])
  def createUniverse() {

    log.debug("home::createUniverse");

    if ( request.method=='POST' ) {
      if ( params.newUniverseName ) {
        log.debug("Create new universe with name ${params.newUniverseName}");
        def new_universe = new Universe(name:params.newUniverseName).save(flush:true, failOnError:true)
        log.debug("Result: ${new_universe}");
        redirect action:'index'
      }
    }
    else {
      // show create universe form
    }
    
  }
}
