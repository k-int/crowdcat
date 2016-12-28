package crowdcat

class TSVSourceCollection extends SourceCollection {

  String filepath

  static constraints = {
    filepath  blank: false, nullable: false
  }

  static mapping = {
    filepath column:'sc_es_url'
  }

  public String toString() {
    id+' '+name+':'+filepath
  }

  public String getNiceName() {
    'Static File'
  }
}
