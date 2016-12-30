Crowdcat

# Oauth Identity Provider Setup

Google OAuth creds page::
https://console.developers.google.com/apis/credentials

# Configuring OAuth service provider

Google API keys are passed to the app via environment variables set before starting the app

export CROWDCAT_GOOGLE_API_KEY="xxxxx"
export CROWDCAT_GOOGLE_SECRET="xxxxx"

Usually these are set in a script that wraps the start command


Some handy bits of SPARQL for seeing what the app does

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
