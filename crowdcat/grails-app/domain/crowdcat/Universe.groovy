package crowdcat

class Universe {

  String name

  static constraints = {
    name blank: false, unique: true
  }

  static mapping = {
    table 'cc_universe'
    name column:'uni_name'
  }
}
