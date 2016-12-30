package crowdcat

import grails.plugin.springsecurity.annotation.Secured
import grails.converters.*

import virtuoso.jena.driver.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.graph.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.riot.Lang


/**
 *
 *
 *
 *
 * SPARQL to list what we know about http://dams.llgc.org.uk/iiif/2.0/image/4004625
 *
 * select ?s ?p ?o 
 * where {
 * ?s ?p ?o .
 * ?s <http://purl.org/dc/terms/isPartOf> <http://dams.llgc.org.uk/iiif/2.0/image/4004625>
 * } LIMIT 100
 * 
 *
 * Find me all annotations for target image 4004625
 * select ?res ?p ?o 
 * where {
 * ?res ?p ?o .
 * ?res <http://www.w3.org/ns/oa#hasTarget> ?target .
 * ?target <http://purl.org/dc/terms/isPartOf> <http://dams.llgc.org.uk/iiif/2.0/image/4004625> .
 * }
 *
 */
class AnnotationController {

  def springSecurityService
  def resourceAgentService

  /**
   *
   */
  def index() {
    log.debug("annotation::index");

    def graph=null;
    try {
      if ( request.method=='POST' ) {
        log.debug("Create Annotation ${params}");
  
        String annotation_json_str = request.JSON?.toString()
  
        def annot_id =java.util.UUID.randomUUID().toString();
  
        def config = [
          store_uri:'jdbc:virtuoso://localhost:1111'
        ]

        graph = new VirtGraph('uri://crowdcat/annotation/'+annot_id, config.store_uri, "dba", "dba");

        // Model model = ModelFactory.createDefaultModel();
        Model model = ModelFactory.createModelForGraph(graph)

        model.begin();
        println("Model from virt");
        model.write( System.out, "N-TRIPLE");

        // Resource annotation = model.createResource("http://crowdcat/annotation/"+annot_id, FOAF.Person)
        // person_1.addProperty(RDFS.label, model.createLiteral("Ian", "en"))
        // person_1.addProperty(RDFS.label, model.createLiteral("Ian Ibbotson", "en"))

        org.apache.jena.riot.RDFDataMgr.read(model, new StringReader(annotation_json_str), 'eng', Lang.JSONLD);

        println("Model after processing");
        model.write( System.out, "N-TRIPLE");
        model.commit();
      }
      else {
        log.debug("Get ${params}");
      }
    }
    catch ( Exception e ) {
      log.error("Problem",e);
      e.printStackTrace()
    }
    finally {
      if ( graph ) {
        graph.close()
      }
    }

    def result = [:]
    render result as JSON
  }

}
