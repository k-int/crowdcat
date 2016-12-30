#!/usr/bin/groovy

// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@GrabResolver(name='mvnRepository', root='http://central.maven.org/maven2/')
@GrabResolver(name='kint', root='http://nexus.k-int.com/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.3'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.0'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='com.k-int', module='goai', version='1.0.2'),
  @Grab(group='org.slf4j', module='slf4j-api', version='1.7.6'),
  @Grab(group='org.slf4j', module='jcl-over-slf4j', version='1.7.6'),
  @Grab(group='net.sourceforge.nekohtml', module='nekohtml', version='1.9.14'),
  @Grab(group='xerces', module='xercesImpl', version='2.9.1'),
  @Grab(group='org.apache.jena', module='jena-tdb', version='1.0.2'),
  @Grab(group='org.apache.jena', module='jena-core', version='2.11.2'),
  @Grab(group='org.apache.jena', module='jena-arq', version='2.11.2'),
  @Grab(group='org.apache.jena', module='jena-iri', version='1.0.2'),
  @Grab(group='org.apache.jena', module='jena-spatial', version='1.0.1'),
  @Grab(group='org.apache.jena', module='jena-security', version='2.11.2'),
  @Grab(group='org.apache.jena', module='jena-text', version='1.0.1'),
  @Grab(group='virtuoso', module='virtjena', version='2'),
  @Grab(group='virtuoso', module='virtjdbc', version='4.1')
])

import groovyx.net.http.*
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import org.apache.http.*
import org.apache.http.protocol.*
import java.nio.charset.Charset
import static groovy.json.JsonOutput.*
import virtuoso.jena.driver.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.graph.*;
import java.text.SimpleDateFormat
import groovy.util.slurpersupport.GPathResult
import org.apache.log4j.*
import com.k_int.goai.*;

def config_file = new File('crowdcat-config.groovy')

def config = new ConfigSlurper().parse(config_file.toURL())
if ( ! config.maxtimestamp ) {
  println("Intialise timestamp");
  config.maxtimestamp = 0
}

println("Starting...");


try {
  
  graph = new VirtGraph('uri://gokb.openlibraryfoundation.org/', config.store_uri, "dba", "dba");

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

  addToGraph(orgUri, skos_pref_label_pred, record.metadata.gokb.org.name.text(),false);
  addUriToGraph(orgUri, foaf_homepage_pred, record.metadata.gokb.org.homepage?.text(),false);
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

def addToGraph(subj, pred, obj, isNode){
  if(isNode){
    graph.add(new Triple(subj, pred, obj));
  }else{
    if(obj?.trim()){
      graph.add(new Triple(subj, pred, NodeFactory.createLiteral(obj)));
    }
  }
}


def addUriToGraph(subj, pred, uri, isNode){
  if(isNode){
    graph.add(new Triple(subj, pred, uri));
  }else{
    if(uri?.trim()){
      graph.add(new Triple(subj, pred, NodeFactory.createURI(uri)));
    }
  }
}

