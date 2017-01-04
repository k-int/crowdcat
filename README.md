Crowdcat

# An exploratory platform providing Virtuoso backed OA Annotations server and mirador front end

Some extra info here https://youtu.be/FbakFZ3nEZI

![Image of annotations displaying in mirador](/images/AnnotationsFromVirt.png)

# Architecture

* Front end :: Mirador with a custom endpoint for talking OA Protocol -- 
* Web API :: Provided by the grails app in crowdcat dir -- currently this app mixes a number of adminstrative tools and the OA Implementaiton. This is for conveinence only, in production the annotation controller will be moved into a stand-alone app separate to the admin functions.
* Storage :: Provided by Virtuoso

# Oauth Identity Provider Setup

Google OAuth creds page::
https://console.developers.google.com/apis/credentials

# Configuring OAuth service provider

Google API keys are passed to the app via environment variables set before starting the app

export CROWDCAT_GOOGLE_API_KEY="xxxxx"
export CROWDCAT_GOOGLE_SECRET="xxxxx"

Usually these are set in a script that wraps the start command


Some handy bits of SPARQL for seeing what the app does

# Setup

This GIST is handy:: https://gist.github.com/ianibo/0670970fccc7f424924f

# WARNING

The Jena / virtuoso libraries seem to do some clever bytecode manipulation and caching -- and does not seem to 
survive the groovy dynamic reloading process. Be wary of exceptions like

    org.apache.jena.shared.JenaException: java.lang.IllegalStateException: The type registry TypeRegistry(id=778613057,loader=sun.misc.Launcher$AppClassLoader) does not know about type id 3883

that indicate reloading is out of sync with the library. Full app restart is needed in this case.


# Some handy sparql

Virtuoso should be ruunning a web SPARQL endpont at http://localhost:8890/sparql. Here are some queries to try out
after creating some annotations...

## Annotations on a given canvas

This is the root of the query that drives the mirador <-> OA annotation server GET request for annotations present on
a given canvas. The app actually only needs the graph as each graph is serialised and added to the response, but the
query itself is a useful tool for devs to copy into http://localhost:8890/sparql to see how their annotations look in the DB

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
	

## SPARQL to list what we know about http://dams.llgc.org.uk/iiif/2.0/image/4004625 (It's a target)
  
    select ?s ?p ?o 
    where {
    ?s ?p ?o .
    ?s <http://purl.org/dc/terms/isPartOf> <http://dams.llgc.org.uk/iiif/2.0/image/4004625>
    } LIMIT 100
  
  
## Find me all things that have image 4004625 as a target

    select ?res ?p ?o
    where {
    ?res ?p ?o .
    ?res <http://www.w3.org/ns/oa#hasTarget> ?target .
    ?target <http://purl.org/dc/terms/isPartOf> <http://dams.llgc.org.uk/iiif/2.0/image/4004625> .
    }

## Find me all annotations that have image 4004625 as a target

    select ?res ?p ?o
    where {
    ?res ?p ?o .
    ?res a <http://www.w3.org/ns/oa#Annotation> .
    ?res <http://www.w3.org/ns/oa#hasTarget> ?target .
    ?target <http://purl.org/dc/terms/isPartOf> <http://dams.llgc.org.uk/iiif/2.0/image/4004625> .
    }

## Bit more volumous -- props we will use when assembling an annotation to send back to mirador

    select ?res ?p ?o ?target ?tp ?to
    where {
    ?res ?p ?o .
    ?target ?tp ?to .
    ?res a <http://www.w3.org/ns/oa#Annotation> .
    ?res <http://www.w3.org/ns/oa#hasTarget> ?target .
    ?target <http://purl.org/dc/terms/isPartOf> <http://dams.llgc.org.uk/iiif/2.0/image/4004625> .
    }

## Include graph (current impl has 1:1 graph with annotation)

    select ?g ?res ?p ?o ?target ?tp ?to
    where {
    GRAPH ?g { 
        ?res ?p ?o .
        ?target ?tp ?to .
        ?res a <http://www.w3.org/ns/oa#Annotation> .
        ?res <http://www.w3.org/ns/oa#hasTarget> ?target .
        ?target <http://purl.org/dc/terms/isPartOf> <http://dams.llgc.org.uk/iiif/2.0/image/4004625> .
      }
    }

## List all graphs (Annotations in current impl) for identified resource

Done this way initially to make it easy to respond to mirador requests for all annotations against current canvas -- essentially creating 
a union of these graphs will be the annotation list the server needs to respond with.

    select ?g
    where {
    GRAPH ?g { 
        ?res a <http://www.w3.org/ns/oa#Annotation> .
        ?res <http://www.w3.org/ns/oa#hasTarget> ?target .
        ?target <http://purl.org/dc/terms/isPartOf> <http://dams.llgc.org.uk/iiif/2.0/image/4004625> .
      }
    }


## List annotation ID, Content of annotation, Source (effectively canvas) and Selector for any annotations part of image 4004625


    select ?res ?content ?source ?selectorValue
    where {
    ?body <http://www.w3.org/2011/content#chars> ?content .
    ?res <http://www.w3.org/ns/oa#hasBody> ?body .
    ?res a <http://www.w3.org/ns/oa#Annotation> .
    ?res <http://www.w3.org/ns/oa#hasTarget> ?target .
    ?target <http://purl.org/dc/terms/isPartOf> <http://dams.llgc.org.uk/iiif/2.0/image/4004625> .
    ?target <http://www.w3.org/ns/oa#hasSelector> ?selector .
    ?target <http://www.w3.org/ns/oa#hasSource> ?source .
    ?selector <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?selectorValue .
    }
