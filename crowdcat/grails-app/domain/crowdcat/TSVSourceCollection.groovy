package crowdcat

class TSVSourceCollection extends SourceCollection {

  String filepath
  def TSVSourceAgentService

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

  public void update(project) {
    log.debug("TSVSourceCollection::update(${project})");
    java.io.File f = new java.io.File(filepath)
    if ( f.exists() ) {
      log.debug("File ${filepath} exists - process");
      TSVSourceAgentService.update(project, f)
    }
    else {
      log.debug("File ${filepath} does not exist - create");
      f.createNewFile();
    }
  }

}
