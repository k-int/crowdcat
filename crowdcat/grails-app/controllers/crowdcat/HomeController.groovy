package crowdcat

import grails.plugin.springsecurity.annotation.Secured

class HomeController {

  def springSecurityService

  def index() { 
    def result = [:]
    def user = springSecurityService.currentUser
    if ( user ) {
      result.sclist = SourceCollection.list()
      result.plist = Project.list()
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
  def createSourceCollection() {

    log.debug("home::createSourceCollection");

    if ( request.method=='POST' ) {
      if ( params.newSourceCollectionName ) {
        log.debug("Create new source collection with name ${params.newSourceCollectionName}");
        def new_source_collection = new SourceCollection(name:params.newSourceCollectionName).save(flush:true, failOnError:true)
        log.debug("Result: ${new_source_collection}");
        redirect action:'index'
      }
    }
    else {
      // show create source collection form
    }
    
  }
}
