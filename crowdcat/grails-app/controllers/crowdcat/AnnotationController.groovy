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

import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.riot.system.RiotLib;
import com.github.jsonldjava.core.JsonLdOptions;

import org.apache.jena.riot.JsonLDWriteContext;

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


  public static String annotations_context = '''
{
    "oa":      "http://www.w3.org/ns/oa#",
    "dc":      "http://purl.org/dc/elements/1.1/",
    "dcterms": "http://purl.org/dc/terms/",
    "dctypes": "http://purl.org/dc/dcmitype/",
    "foaf":    "http://xmlns.com/foaf/0.1/",
    "rdf":     "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "rdfs":    "http://www.w3.org/2000/01/rdf-schema#",
    "skos":    "http://www.w3.org/2004/02/skos/core#",
    "xsd":     "http://www.w3.org/2001/XMLSchema#",
    "iana":    "http://www.iana.org/assignments/relation/",
    "owl":     "http://www.w3.org/2002/07/owl#",
    "as":      "http://www.w3.org/ns/activitystreams#",
    "schema":  "http://schema.org/",

    "id":      {"@type": "@id", "@id": "@id"},
    "type":    {"@type": "@id", "@id": "@type"},

    "Annotation":           "oa:Annotation",
    "Dataset":              "dctypes:Dataset",
    "Image":                "dctypes:StillImage",
    "Video":                "dctypes:MovingImage",
    "Audio":                "dctypes:Sound",
    "Text":                 "dctypes:Text",
    "TextualBody":          "oa:TextualBody",
    "ResourceSelection":    "oa:ResourceSelection",
    "SpecificResource":     "oa:SpecificResource",
    "FragmentSelector":     "oa:FragmentSelector",
    "CssSelector":          "oa:CssSelector",
    "XPathSelector":        "oa:XPathSelector",
    "TextQuoteSelector":    "oa:TextQuoteSelector",
    "TextPositionSelector": "oa:TextPositionSelector",
    "DataPositionSelector": "oa:DataPositionSelector",
    "SvgSelector":          "oa:SvgSelector",
    "RangeSelector":        "oa:RangeSelector",
    "TimeState":            "oa:TimeState",
    "HttpRequestState":     "oa:HttpRequestState",
    "CssStylesheet":        "oa:CssStyle",
    "Choice":               "oa:Choice",
    "Person":               "foaf:Person",
    "Software":             "as:Application",
    "Organization":         "foaf:Organization",
    "AnnotationCollection": "as:OrderedCollection",
    "AnnotationPage":       "as:OrderedCollectionPage",
    "Audience":             "schema:Audience", 

    "Motivation":    "oa:Motivation",
    "bookmarking":   "oa:bookmarking",
    "classifying":   "oa:classifying",
    "commenting":    "oa:commenting",
    "describing":    "oa:describing",
    "editing":       "oa:editing",
    "highlighting":  "oa:highlighting",
    "identifying":   "oa:identifying",
    "linking":       "oa:linking",
    "moderating":    "oa:moderating",
    "questioning":   "oa:questioning",
    "replying":      "oa:replying",
    "reviewing":     "oa:reviewing",
    "tagging":       "oa:tagging",

    "auto":          "oa:autoDirection",
    "ltr":           "oa:ltrDirection",
    "rtl":           "oa:rtlDirection",

    "body":          {"@type": "@id", "@id": "oa:hasBody"},
    "target":        {"@type": "@id", "@id": "oa:hasTarget"},
    "source":        {"@type": "@id", "@id": "oa:hasSource"},
    "selector":      {"@type": "@id", "@id": "oa:hasSelector"},
    "state":         {"@type": "@id", "@id": "oa:hasState"},
    "scope":         {"@type": "@id", "@id": "oa:hasScope"},
    "refinedBy":     {"@type": "@id", "@id": "oa:refinedBy"},
    "startSelector": {"@type": "@id", "@id": "oa:hasStartSelector"},
    "endSelector":   {"@type": "@id", "@id": "oa:hasEndSelector"},
    "renderedVia":   {"@type": "@id", "@id": "oa:renderedVia"},
    "creator":       {"@type": "@id", "@id": "dcterms:creator"},
    "generator":     {"@type": "@id", "@id": "as:generator"},
    "rights":        {"@type": "@id", "@id": "dcterms:rights"},
    "homepage":      {"@type": "@id", "@id": "foaf:homepage"},
    "via":           {"@type": "@id", "@id": "oa:via"},
    "canonical":     {"@type": "@id", "@id": "oa:canonical"},
    "stylesheet":    {"@type": "@id", "@id": "oa:styledBy"},
    "cached":        {"@type": "@id", "@id": "oa:cachedSource"},
    "conformsTo":    {"@type": "@id", "@id": "dcterms:conformsTo"},
    "items":         {"@type": "@id", "@id": "as:items", "@container": "@list"},
    "partOf":        {"@type": "@id", "@id": "as:partOf"},
    "first":         {"@type": "@id", "@id": "as:first"},
    "last":          {"@type": "@id", "@id": "as:last"},
    "next":          {"@type": "@id", "@id": "as:next"},
    "prev":          {"@type": "@id", "@id": "as:prev"},
    "audience":      {"@type": "@id", "@id": "schema:audience"},
    "motivation":    {"@type": "@vocab", "@id": "oa:motivatedBy"},
    "purpose":       {"@type": "@vocab", "@id": "oa:hasPurpose"},
    "textDirection": {"@type": "@vocab", "@id": "oa:textDirection"},

    "accessibility": "schema:accessibilityFeature",
    "bodyValue":     "oa:bodyValue",
    "format":        "dc:format",
    "language":      "dc:language",
    "processingLanguage": "oa:processingLanguage",
    "value":         "rdf:value",
    "exact":         "oa:exact",
    "prefix":        "oa:prefix",
    "suffix":        "oa:suffix",
    "styleClass":    "oa:styleClass",
    "name":          "foaf:name",
    "email":         "foaf:mbox",
    "email_sha1":    "foaf:mbox_sha1sum",
    "nickname":      "foaf:nick",
    "label":         "rdfs:label",

    "created":       {"@id": "dcterms:created", "@type": "xsd:dateTime"},
    "modified":      {"@id": "dcterms:modified", "@type": "xsd:dateTime"},
    "generated":     {"@id": "dcterms:issued", "@type": "xsd:dateTime"},
    "sourceDate":    {"@id": "oa:sourceDate", "@type": "xsd:dateTime"},
    "sourceDateStart": {"@id": "oa:sourceDateStart", "@type": "xsd:dateTime"},
    "sourceDateEnd": {"@id": "oa:sourceDateEnd", "@type": "xsd:dateTime"},

    "start":         {"@id": "oa:start", "@type": "xsd:nonNegativeInteger"},
    "end":           {"@id": "oa:end", "@type": "xsd:nonNegativeInteger"},
    "total":         {"@id": "as:totalItems", "@type": "xsd:nonNegativeInteger"},
    "startIndex":    {"@id": "as:startIndex", "@type": "xsd:nonNegativeInteger"}
  }
'''

  public Map annotations_context_map = [
                  'oa' : 'http://www.w3.org/ns/oa#',
                  'id' : [ '@type':'@id', '@id':'@id' ],
                'type' : [ '@type':'@id', '@id':'@type' ],
          'Annotation' : [ '@type':'@id', '@id':'oa:Annotation'],
    'SpecificResource' : [ '@type':'@id', '@id':'oa:SpecificResource'],
                  'on' : [ '@type':'@id', '@id':'oa:hasTarget' ],
          'motivation' : [ '@type':'@id', '@id':'oa:hasMotivation' ],
                 'full': [ '@type':'@id', '@id':'oa:hasSource' ]
  ]

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
            
            log.debug("Model as n3\n${annotation_as_n3}");
          
            def stand_alone_model = ModelFactory.createDefaultModel()
            stand_alone_model.setNsPrefix('ex', 'http://www.ex.com/');
            stand_alone_model.setNsPrefix('sh', 'http://schema.org/');
            stand_alone_model.setNsPrefix('oa', 'http://www.w3.org/ns/oa#');

            org.apache.jena.riot.RDFDataMgr.read(stand_alone_model, new StringReader(annotation_as_n3), 'eng', Lang.N3);

  
            
            log.debug("As JSONLD");
            StringWriter ld_sw = new StringWriter()

            // See :: https://github.com/apache/jena/blob/master/jena-arq/src-examples/arq/examples/riot/ExJsonLD.java
            // WriterDatasetRIOT w = RDFDataMgr.createDatasetWriter(RDFFormat.JSONLD_FRAME_PRETTY)
            WriterDatasetRIOT w = RDFDataMgr.createDatasetWriter(RDFFormat.JSONLD_COMPACT_PRETTY)
            // WriterDatasetRIOT w = RDFDataMgr.createDatasetWriter(RDFFormat.JSONLD_EXPAND_PRETTY) // Not this one
            JsonLDWriteContext ctx = new JsonLDWriteContext();
            ctx.setJsonLDContext(annotations_context);
            JsonLdOptions opts = new JsonLdOptions();
            ctx.setOptions(opts);
            opts.setCompactArrays(false);

            DatasetGraph dg = DatasetFactory.create(stand_alone_model).asDatasetGraph();
            PrefixMap pm = RiotLib.prefixMap(dg);

            log.debug("prefix map ${pm}");

            String frame = '{"@type" : "http://www.w3.org/ns/oa#Annotation"}';
            ctx.setFrame(frame);

            w.write(ld_sw, 
                    dg,
                    pm,
                    null, // 'http://base', 
                    ctx);

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
