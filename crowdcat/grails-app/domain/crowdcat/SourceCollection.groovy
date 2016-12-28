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

  /**
   *  Update the specifid project adding any resources from the source that are new or have changed
   *  since last check
   */
  public abstract void update(project);

}
