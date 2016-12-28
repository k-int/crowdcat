package crowdcat

class Project {

  String name

  static constraints = {
    name blank: false, unique: true
  }

  static mapping = {
    table 'cc_project'
    name column:'prj_name'
  }

  public ensureResourcePresent(resource_uri, description) {
    log.debug("ensureResourcePresent(${resource_uri},${description}");
  }
}
