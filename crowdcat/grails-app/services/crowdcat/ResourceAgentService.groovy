package crowdcat

import grails.transaction.Transactional

@Transactional
class ResourceAgentService {

  // 
  def checkForNewResources() {

    log.debug("ResourceAgentService::checkForNewResources");

    // Use the different kinds of source to collect records.
    // Initially this is a mock implementation that just adds some specific resources
    Project.list().each { project ->
      checkProject(project)
    }

    log.debug("ResourceAgentService::checkForNewResources done");
  }

  def checkProject(project) {
    log.debug("ResourceAgentService::checkProject(${project})");
    def sources = ProjectSource.findAllByProj(project)
    sources.each { src ->
      checkProjectSource(src)
    }
  }

  def checkProjectSource(ps) {
    log.debug("ResourceAgentService::checkProjectSource(${ps})");
    ps.rs.update(ps.proj)
  }

}
