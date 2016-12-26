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
}
