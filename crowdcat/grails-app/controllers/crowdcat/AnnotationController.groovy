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
import org.apache.jena.riot.*;
import org.apache.jena.query.*;

// import com.github.jsonldjava.jena.JenaJSONLD;

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

  // Yes - this is fugly!!! -- http://iiif.io/api/presentation/2/context.json
  public static annotations_context = '''"@context": [ { "sc": "http://iiif.io/api/presentation/2#", "iiif": "http://iiif.io/api/image/2#", "exif": "http://www.w3.org/2003/12/exif/ns#", "oa": "http://www.w3.org/ns/oa#", "cnt": "http://www.w3.org/2011/content#", "dc": "http://purl.org/dc/elements/1.1/", "dcterms": "http://purl.org/dc/terms/", "dctypes": "http://purl.org/dc/dcmitype/", "doap": "http://usefulinc.com/ns/doap#", "foaf": "http://xmlns.com/foaf/0.1/", "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdfs": "http://www.w3.org/2000/01/rdf-schema#", "xsd": "http://www.w3.org/2001/XMLSchema#", "svcs": "http://rdfs.org/sioc/services#", "as": "http://www.w3.org/ns/activitystreams#", "license": { "@type": "@id", "@id": "dcterms:rights" }, "service": { "@type": "@id", "@id": "svcs:has_service" }, "seeAlso": { "@type": "@id", "@id": "rdfs:seeAlso" }, "within": { "@type": "@id", "@id": "dcterms:isPartOf" }, "profile": { "@type": "@id", "@id": "doap:implements" }, "related": { "@type": "@id", "@id": "dcterms:relation" }, "logo": { "@type": "@id", "@id": "foaf:logo" }, "thumbnail": { "@type": "@id", "@id": "foaf:thumbnail" }, "startCanvas": { "@type": "@id", "@id": "sc:hasStartCanvas" }, "contentLayer": { "@type": "@id", "@id": "sc:hasContentLayer" }, "members": { "@type": "@id", "@id": "sc:hasParts", "@container": "@list" }, "collections": { "@type": "@id", "@id": "sc:hasCollections", "@container": "@list" }, "manifests": { "@type": "@id", "@id": "sc:hasManifests", "@container": "@list" }, "sequences": { "@type": "@id", "@id": "sc:hasSequences", "@container": "@list" }, "canvases": { "@type": "@id", "@id": "sc:hasCanvases", "@container": "@list" }, "resources": { "@type": "@id", "@id": "sc:hasAnnotations", "@container": "@list" }, "images": { "@type": "@id", "@id": "sc:hasImageAnnotations", "@container": "@list" }, "otherContent": { "@type": "@id", "@id": "sc:hasLists", "@container": "@list" }, "structures": { "@type": "@id", "@id": "sc:hasRanges", "@container": "@list" }, "ranges": { "@type": "@id", "@id": "sc:hasRanges", "@container": "@list" }, "metadata": { "@type": "@id", "@id": "sc:metadataLabels", "@container": "@list" }, "description": { "@id": "dc:description" }, "navDate": { "@id": "sc:presentationDate" }, "rendering": { "@id": "dcterms:hasFormat", "@type": "@id" }, "height": { "@type": "xsd:integer", "@id": "exif:height" }, "width": { "@type": "xsd:integer", "@id": "exif:width" }, "attribution": { "@id": "sc:attributionLabel" }, "viewingDirection": { "@id": "sc:viewingDirection", "@type": "@vocab" }, "viewingHint": { "@id": "sc:viewingHint", "@type": "@vocab" }, "left-to-right": { "@id": "sc:leftToRightDirection", "@type": "sc:ViewingDirection" }, "right-to-left": { "@id": "sc:rightToLeftDirection", "@type": "sc:ViewingDirection" }, "top-to-bottom": { "@id": "sc:topToBottomDirection", "@type": "sc:ViewingDirection" }, "bottom-to-top": { "@id": "sc:bottomToTopDirection", "@type": "sc:ViewingDirection" }, "paged": { "@id": "sc:pagedHint", "@type": "sc:ViewingHint" }, "non-paged": { "@id": "sc:nonPagedHint", "@type": "sc:ViewingHint" }, "continuous": { "@id": "sc:continuousHint", "@type": "sc:ViewingHint" }, "individuals": { "@id": "sc:individualsHint", "@type": "sc:ViewingHint" }, "top": { "@id": "sc:topHint", "@type": "sc:ViewingHint" }, "multi-part": { "@id": "sc:multiPartHint", "@type": "sc:ViewingHint" }, "facing-pages": { "@id": "sc:facingPagesHint", "@type": "sc:ViewingHint" }, "motivation": { "@type": "@id", "@id": "oa:motivatedBy" }, "resource": { "@type": "@id", "@id": "oa:hasBody" }, "on": { "@type": "@id", "@id": "oa:hasTarget" }, "full": { "@type": "@id", "@id": "oa:hasSource" }, "selector": { "@type": "@id", "@id": "oa:hasSelector" }, "stylesheet": { "@type": "@id", "@id": "oa:styledBy" }, "style": { "@id": "oa:styleClass" }, "default": { "@type": "@id", "@id": "oa:default" }, "item": { "@type": "@id", "@id": "oa:item" }, "chars": { "@id": "cnt:chars" }, "encoding": { "@id": "cnt:characterEncoding" }, "bytes": { "@id": "cnt:bytes" }, "format": { "@id": "dc:format" }, "language": { "@id": "dc:language" }, "value": { "@id": "rdf:value" }, "label": { "@id": "rdfs:label" }, "first": { "@type": "@id", "@id": "as:first" }, "last": { "@type": "@id", "@id": "as:last" }, "next": { "@type": "@id", "@id": "as:next" }, "prev": { "@type": "@id", "@id": "as:prev" }, "total": { "@id": "as:totalItems" }, "startIndex": { "@id": "as:startIndex" } } ]'''

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

  
            
            log.debug("As JSONLD");
            StringWriter ld_sw = new StringWriter()

            // http://www.programcreek.com/java-api-examples/index.php?api=com.github.jsonldjava.core.JsonLdOptions
            // https://jena.apache.org/documentation/javadoc/arq/org/apache/jena/riot/WriterDatasetRIOT.html
            WriterDatasetRIOT w = RDFDataMgr.createDatasetWriter(Lang.JSONLD)
            // https://mavenbrowse.pauldoo.com/central/com/github/jsonld-java/jsonld-java-jena/0.4.1/jsonld-java-jena-0.4.1-test-sources.jar/-/com/github/jsonldjava/jena/ExampleTest.java
            // WriterDatasetRIOT w = RDFDataMgr.createDatasetWriter(JenaJSONLD.JSONLD)
            org.apache.jena.sparql.util.Context c = new org.apache.jena.sparql.util.Context()

            // https://www.mail-archive.com/commits@jena.apache.org/msg12064.html
            // https://www.mail-archive.com/commits@jena.apache.org/msg12087.html
            // file:///home/ibbo/Downloads/apache-jena-3.0.1/javadoc-arq/index.html
            // http://hcklab.blogspot.co.uk/2014/01/json-ld-jena-and-virtuoso-and-named.html
            c.set(org.apache.jena.sparql.util.Symbol.create('@context'),annotations_context)

            org.apache.jena.graph.Graph the_graph = stand_alone_model.getGraph()
            org.apache.jena.riot.system.PrefixMap pm = new org.apache.jena.riot.system.PrefixMapStd()
            org.apache.jena.sparql.core.DatasetGraph dg = org.apache.jena.sparql.core.DatasetGraphFactory.createOneGraph(the_graph);

            w.write(ld_sw, 
                    dg,
                    pm,
                    'http://base', 
                    c);

            def json_ld = ld_sw.toString();

            log.debug("Json LD Version: ${json_ld}");
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
