#!/usr/bin/groovy

@GrabResolver(name='mvnRepository', root='http://central.maven.org/maven2/')
@GrabResolver(name='kint', root='http://nexus.k-int.com/content/repositories/releases')
@Grapes([
  @Grab(group='org.apache.jena', module='jena-core', version='3.0.1'),
  @Grab(group='org.apache.jena', module='jena-arq', version='3.0.1'),
  @Grab(group='org.apache.jena', module='jena-iri', version='3.0.1'),
  @Grab(group='org.apache.jena', module='jena-spatial', version='3.0.1'),
  @Grab(group='org.apache.jena', module='jena-text', version='3.0.1'),
  @Grab(group='virtuoso', module='virtjena', version='3'),
  @Grab(group='virtuoso', module='virtjdbc', version='4.1')
])

// Dependency info can be found here:: https://virtuoso.openlinksw.com/dataspace/doc/dav/wiki/Main/VirtJenaProvider

import virtuoso.jena.driver.*;
// import com.hp.hpl.jena.query.*;
// import com.hp.hpl.jena.rdf.model.* ;
// import com.hp.hpl.jena.graph.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.graph.*;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.vocabulary.FOAF;

def config_file = new File('crowdcat-config.groovy')

def config = new ConfigSlurper().parse(config_file.toURL())
if ( ! config.maxtimestamp ) {
  println("Intialise timestamp");
  config.maxtimestamp = 0
}

println("Starting...");


try {
  
  graph = new VirtGraph('uri://NLWVirtTest1/graph1/', config.store_uri, "dba", "dba");

  // Model model = ModelFactory.createDefaultModel();
  Model model = ModelFactory.createModelForGraph(graph)


  Node dc_service_type = NodeFactory.createURI('http://purl.org/dc/dcmitype/Service');
  Node dc_collection_type = NodeFactory.createURI('http://purl.org/dc/dcmitype/Collection');
  Node dc_text_type = NodeFactory.createURI('http://purl.org/dc/dcmitype/Text')
  Node dc_type_type = NodeFactory.createURI('http://purl.org/dc/terms/type ')
  Node type_pred = NodeFactory.createURI('http://www.w3.org/1999/02/22-rdf-syntax-ns#type');
  Node rdfs_resource_pred = NodeFactory.createURI('http://www.w3.org/2000/01/rdf-schema#Resource');

  Node skos_pref_label_pred = NodeFactory.createURI('http://www.w3.org/2004/02/skos/core#prefLabel');
  Node skos_alt_label_pred = NodeFactory.createURI('http://www.w3.org/2004/02/skos/core#altLabel');
  Node owl_same_as_pred = NodeFactory.createURI('http://www.w3.org/2002/07/owl#sameAs');

  Node dc_publisher_pred = NodeFactory.createURI('http://purl.org/dc/terms/publisher');
  Node dc_format_pred = NodeFactory.createURI('http://purl.org/dc/terms/format');
  Node dc_medium_pred = NodeFactory.createURI('http://purl.org/dc/terms/medium')
  Node bibo_status_pred = NodeFactory.createURI('http://purl.org/ontology/bibo/status');

  // addToGraph(orgUri, skos_pref_label_pred, record.metadata.gokb.org.name.text(),false);
  // addUriToGraph(orgUri, foaf_homepage_pred, record.metadata.gokb.org.homepage?.text(),false);

  model.begin();

  Resource r = model.createResource();              
  Resource person_1 = model.createResource("http://ianibbo.me/#", FOAF.Person)
  person_1.addProperty(RDFS.label, model.createLiteral("Ian", "en"))
  person_1.addProperty(RDFS.label, model.createLiteral("Ian Ibbotson", "en"))

  model.commit();

  // model.write( System.out, "N-TRIPLE");
}
catch ( Exception e ) {
  e.printStackTrace();
}
finally {
}

println("Done.");

config_file.withWriter { writer ->
  config.writeTo(writer)
}
