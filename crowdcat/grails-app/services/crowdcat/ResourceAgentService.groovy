package crowdcat

import grails.transaction.Transactional

@Transactional
class ResourceAgentService {

  // 
  def checkForNewResources() {
    // Use the different kinds of source to collect records.
    // Initially this is a mock implementation that just adds some specific resources
    Project.list().each { project ->
      checkProject(project)
    }
  }

  def checkProject(project) {
    
  }

  def upsertResource(project, resource_uri) {
  }
}
