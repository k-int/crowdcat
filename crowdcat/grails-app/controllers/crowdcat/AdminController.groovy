package crowdcat

import grails.plugin.springsecurity.annotation.Secured

class AdminController {

  def springSecurityService
  def resourceAgentService

  @Secured(['ROLE_INST_ADM', 'IS_AUTHENTICATED_FULLY'])
  def triggerResourceAgent() {
    log.debug("admin::triggerResourceAgent");

    resourceAgentService.checkForNewResources()

    redirect controller:'home', action:'index'
  }

}
