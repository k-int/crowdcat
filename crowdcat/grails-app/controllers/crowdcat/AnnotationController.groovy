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
import org.apache.jena.riot.Lang;
import org.apache.jena.query.*;


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

  public static ANNOTATIONS_QRY_1 = '''
select ?graph ?annotation ?target ?body ?source ?content
where {
  GRAPH ?graph { 
    ?annotation <http://www.w3.org/ns/oa#hasTarget> ?target .
    ?target <http://www.w3.org/ns/oa#hasSource> <http://dams.llgc.org.uk/iiif/2.0/image/4004625/sequence/1/canvas/1> .
    ?annotation <http://www.w3.org/ns/oa#hasBody> ?body .
    ?target <http://www.w3.org/ns/oa#hasSource> ?source .
    ?body <http://www.w3.org/2011/content#chars> ?content .
  }
}
'''

  /**
   *
   */
  def index() {
    log.debug("annotation::index");
    def result = null;

    def graph=null;

    def config = [
      store_uri:'jdbc:virtuoso://localhost:1111'
    ]

    try {
      if ( request.method=='POST' ) {
        log.debug("Create Annotation ${params}");
  
        String annotation_json_str = request.JSON?.toString()
  
        def annot_id =java.util.UUID.randomUUID().toString();
  
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

        result=[status:'OK'];
      }
      else {
        log.debug("Get ${params}");
        // If we have been passed a uri
        if ( params.uri && ( params.uri.trim().length() > 0 ) ) {
          // Lets find all annotations -- create a new graph with no default graph == Everything
          graph = new VirtGraph(null, config.store_uri, "dba", "dba");
          Query sparql = QueryFactory.create(ANNOTATIONS_QRY_1);
          VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);
          ResultSet results = vqe.execSelect();
          while (results.hasNext()) {
            QuerySolution q_result = results.nextSolution();
            // select ?graph ?annotation ?target ?body ?source ?content
            RDFNode res_graph = q_result.get("graph");
            RDFNode res_annotation = q_result.get("annotation");
            RDFNode res_target = q_result.get("target");
            RDFNode res_body = q_result.get("body");
            RDFNode res_source = q_result.get("source");
            RDFNode res_content = q_result.get("content");
            log.debug(res_graph.toString() + ' ' + 
                      res_annotation.toString() + ' ' + 
                      res_target.toString() + ' ' + 
                      res_body.toString() + ' ' + 
                      res_source.toString() + ' ' + 
                      res_content.toString())

            def annotation_graph = new VirtGraph(res_graph.toString(), config.store_uri, "dba", "dba");
            log.debug("Got res graph");
            def annotation_model = ModelFactory.createModelForGraph(annotation_graph);
            // org.apache.jena.riot.RDFDataMgr.write(System.out, annotation_model, Lang.JSONLD);
            // org.apache.jena.riot.RDFDataMgr.write(System.out, annotation_model, org.apache.jena.riot.RDFFormat.JSONLD_PRETTY );
            log.debug("As N3");

            // We handle this as a 2 step process. Something in vert is adding an "sql" IRI to the model and this is causing a cycle
            // in the JSON-LD output. We go trhough n3 as a neutral format to clear out any unwanted storage model shennanagins.

            // Convert the model we got back from virt into n3
            StringWriter n3_model_sw = new StringWriter()
            org.apache.jena.riot.RDFDataMgr.write(n3_model_sw, annotation_model, org.apache.jena.riot.RDFFormat.NTRIPLES_UTF8 );
            def annotation_as_n3 = n3_model_sw.toString();
            annotation_graph.close();
            
            log.debug("Model as n3 ${annotation_as_n3}");
          
            def stand_alone_model = ModelFactory.createDefaultModel()

            org.apache.jena.riot.RDFDataMgr.read(stand_alone_model, new StringReader(annotation_as_n3), 'eng', Lang.N3);

  
            // log.debug("As JSONLD");
            org.apache.jena.riot.RDFDataMgr.write(System.out, stand_alone_model, Lang.JSONLD);

          }
        }

        // Mirador expects a JS array of annotation graphs as a result object
        result = []

  
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

    render result as JSON
  }

}
