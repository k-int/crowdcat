package crowdcat

import grails.plugin.springsecurity.annotation.Secured
import grails.converters.*

// import virtuoso.jena.driver.*;
// import com.hp.hpl.jena.query.*;
// import com.hp.hpl.jena.rdf.model.* ;
// import com.hp.hpl.jena.graph.*;
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Model

class AnnotationController {

  def springSecurityService
  def resourceAgentService

  def index() {
    log.debug("annotation::index");

    if ( request.method=='POST' ) {
      log.debug("Create Annotation ${params}");

      String annotation_json_str = request.JSON?.toString()

      // org.apache.jena.riot.RDFDataMgr mgr 
      // Model model = RDFDataMgr.loadModel("data.ttl") ;
      // Model model = ModelFactory.createDefaultModel().read(IOUtils.toInputStream(annotation_json_str, "UTF-8"), null, "JSON-LD");
    }
    else {
      log.debug("Get ${params}");
    }

    def result = [:]
    render result as JSON
  }

}
