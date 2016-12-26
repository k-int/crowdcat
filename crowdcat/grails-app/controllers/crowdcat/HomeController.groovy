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
  def createESSourceCollection() {

    log.debug("home::createESSourceCollection");

    if ( request.method=='POST' ) {
      if ( params.newSourceCollectionName && params.esUrl ) {
        log.debug("Create new source collection with name ${params.newSourceCollectionName},${params.esUrl}");
        def new_source_collection = new ElasticSearchSourceCollection(name:params.newSourceCollectionName, esUrl: params.esUrl).save(flush:true, failOnError:true)
        log.debug("Result: ${new_source_collection}");
        redirect action:'index', fragment:'sourceCollections'
      }
    }
    else {
      // show create source collection form
    }
  }

  @Secured(['ROLE_INST_ADM', 'IS_AUTHENTICATED_FULLY'])
  def createProject() {

    log.debug("home::createProject");

    if ( request.method=='POST' ) {
      if ( params.newProjectName ) {
        log.debug("Create new project with name ${params.newProjectName}");
        def new_project = new Project(name:params.newProjectName).save(flush:true, failOnError:true)
        log.debug("Result: ${new_project}");
        redirect action:'index', fragment: 'projects'
      }
    }
    else {
      // show create project form
    }
  }

}
