package crowdcat

import grails.plugin.springsecurity.annotation.Secured

class HomeController {

  def springSecurityService

  def index() { 
    def user = springSecurityService.currentUser
    if ( user ) {
      render view:'loggedInIndex'
    }
    else {
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def login() {
    redirect action:'index'
  }

  @Secured(['ROLE_INST_ADM', 'IS_AUTHENTICATED_FULLY'])
  def createUniverse() {

    if ( request.method=='POST' ) {
    }
    
    redirect action:'index'
  }
}
