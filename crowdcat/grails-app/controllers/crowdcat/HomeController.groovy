package crowdcat

import grails.plugin.springsecurity.annotation.Secured

class HomeController {

  def index() { 
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def login() {
    redirect action:'index'
  }
}
