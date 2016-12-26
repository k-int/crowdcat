package crowdcat

abstract class SourceCollection {

  String name

  static constraints = {
    name blank: false, unique: true
  }

  static mapping = {
    table 'cc_source_collection'
    name column:'sc_name'
  }

  public abstract String getNiceName();

}
