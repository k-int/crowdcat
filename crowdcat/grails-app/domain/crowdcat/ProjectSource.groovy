package crowdcat

class ProjectSource {

  Project proj
  SourceCollection rs

  static constraints = {
    proj blank: false, nullable:false
    rs blank: false, nullable:false
  }

  static mapping = {
    table 'cc_project_source'
    proj column:'ps_proj'
    rs column:'ps_rs'
  }
}
