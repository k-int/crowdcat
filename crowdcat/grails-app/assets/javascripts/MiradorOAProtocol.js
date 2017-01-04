/*
 * All Endpoints need to have at least the following:
 * annotationsList - current list of OA Annotations
 * dfd - Deferred Object
 * init()
 * search(options, successCallback, errorCallback)
 * create(oaAnnotation, successCallback, errorCallback)
 * update(oaAnnotation, successCallback, errorCallback)
 * deleteAnnotation(annotationID, successCallback, errorCallback) (delete is a reserved word)
 * TODO:
 * read() //not currently used
 *
 * Optional, if endpoint is not OA compliant:
 * getAnnotationInOA(endpointAnnotation)
 * getAnnotationInEndpoint(oaAnnotation)
 */
(function($){

  $.MiradorOAProtocolEndpoint = function(options) {

    jQuery.extend(this, {
      dfd:             null,
      annotationsList: [],        //OA list for Mirador use
      windowID:        null,
      eventEmitter:    null,
      oaProtocolEndpoint: null
    }, options);

    this.init();
  };

  $.MiradorOAProtocolEndpoint.prototype = {
    init: function() {
      //whatever initialization your endpoint needs       
    },

    //Search endpoint for all annotations with a given URI in options
    search: function(options, successCallback, errorCallback) {

      console.log("search(%o,%o,%o)",options,successCallback,errorCallback);

      var _this = this;

      //use options.uri
      jQuery.ajax({
        url: this.oaProtocolEndpoint,
        type: 'GET',
        dataType: 'json',
        headers: { },
        data: options,
        contentType: "application/json; charset=utf-8",
        success: function(data) {
          //check if a function has been passed in, otherwise, treat it as a normal search
          if (typeof successCallback === "function") {
            successCallback(data);
          } else {
            jQuery.each(data, function(index, value) {
              _this.annotationsList.push(_this.getAnnotationInOA(value));
            });
            _this.dfd.resolve(true);
          }
        },
        error: function() {
          if (typeof errorCallback === "function") {
            errorCallback();
          }
        }
      });
    },
    
    //Delete an annotation by endpoint identifier
    deleteAnnotation: function(annotationID, successCallback, errorCallback) {

      console.log("deleteAnnotation(%o,%o,%o)",annotationID,successCallback,errorCallback);

      var _this = this;        
      jQuery.ajax({
        url: '',
        type: 'DELETE',
        dataType: 'json',
        headers: { },
        contentType: "application/json; charset=utf-8",
        success: function(data) {
          if (typeof successCallback === "function") {
            successCallback();
          }
        },
        error: function() {
          if (typeof errorCallback === "function") {
            errorCallback();
          }
        }
      });
    },
    
    //Update an annotation given the OA version
    update: function(oaAnnotation, successCallback, errorCallback) {

      console.log("update(%o,%o,%o)",oaAnnotation,successCallback,errorCallback);

      var annotation = this.getAnnotationInEndpoint(oaAnnotation),
      _this = this;
      
      jQuery.ajax({
        url: '',
        type: 'POST',
        dataType: 'json',
        headers: { },
        data: '',
        contentType: "application/json; charset=utf-8",
        success: function(data) {
          if (typeof successCallback === "function") {
            successCallback();
          }
        },
        error: function() {
          if (typeof errorCallback === "function") {
            errorCallback();
          }
        }
      });
    },

    //takes OA Annotation, gets Endpoint Annotation, and saves
    //if successful, MUST return the OA rendering of the annotation
    create: function(oaAnnotation, successCallback, errorCallback) {

      console.log("create(%o,%o,%o)",oaAnnotation,successCallback,errorCallback);

      var _this = this;
      
      console.log("Posting %o",oaAnnotation);

      jQuery.ajax({
        url: this.oaProtocolEndpoint,
        type: 'POST',
        dataType: 'json',
        data: JSON.stringify(oaAnnotation),
        contentType: "application/json; charset=utf-8",
        success: function(data) {
          if (typeof successCallback === "function") {
            console.log("Success %o",data);
            successCallback(_this.getAnnotationInOA(data));
          }
        },
        error: function() {
          if (typeof errorCallback === "function") {
            errorCallback();
          }
        }
      });
    },

    set: function(prop, value, options) {
      if (options) {
        this[options.parent][prop] = value;
      } else {
        this[prop] = value;
      }
    },

    //Convert Endpoint annotation to OA
    getAnnotationInOA: function(annotation) {
      console.log("getAnnotationInOA(%o)",annotation);
      var result = {
        "@context": "http://iiif.io/api/presentation/2/context.json",
        "@id": "xyz",
        "@type":"oa:Annotation",
        "annotatedBy" : { 
          "@id" : '',
          "name" : ''},
        "annotatedAt" : "",
        "serializedAt" : "",
        "permissions" : "",
        "endpoint" : this
      };

      result.motivation=['oa:commenting'];
      result.resource=[{
        "@type":"dctypes:"+annotation.body.type,
        "format":annotation.body.format,
        "chars":annotation.body["http://www.w3.org/2011/content#chars"]
      }];
      result.on={
        "@type":"oa:"+annotation.target["type"],
        "full":annotation.target.source,
        "selector":{
          "@type":"oa:"+annotation.target.selector["type"],
          "value":annotation.target.selector.value
          // "@type" : "oa:FragmentSelector",
          // "value" : "xywh=100,100,400,400"
        },
        "within":{
          "@id":annotation.target["dcterms:isPartOf"]["id"],
          "@type":"sc:Manifest"
          // "@type":annotation.target["dcterms:isPartOf"]["type"]
        }
      };

      // Stuff gleaned from https://github.com/ProjectMirador/mirador/blob/a4a71087833d01c81eca8ebfdbac682b821c8100/js/src/annotations/catchEndpoint.js

      console.log("converted to %o",result);

      return result;
    },

    // Converts OA Annotation to endpoint format
    getAnnotationInEndpoint: function(oaAnnotation) {
      console.log("getAnnotationInEndpoint(%o)",oaAnnotation);
      return oaAnnotation;
    }
  };

}(Mirador));
