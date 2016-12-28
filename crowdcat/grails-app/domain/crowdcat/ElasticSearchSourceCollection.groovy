package crowdcat

class ElasticSearchSourceCollection extends SourceCollection {

  String esUrl

  static constraints = {
    esUrl  blank: false, nullable: false
  }

  static mapping = {
    esUrl column:'sc_es_url'
  }

  public String toString() {
    id+' '+name+':'+esUrl
  }

  public String getNiceName() {
    'ElasticSearch'
  }

  public void update(project) {
    log.debug("ElasticSearchSourceCollection::update(${project})");
  }

}
