// Viewer.js for mirador version

//= require mirador/mirador.min.js
//= require MiradorOAProtocol.js

$(function() {
  myMiradorInstance = Mirador({
    id: "embeddedViewer",
    layout: "1x1",
    buildPath: "assets/mirador/",
    data: [
      { manifestUri: "http://dams.llgc.org.uk//iiif/2.0/image/4004625/info.json", location: "iWibble"}
    ],
    windowObjects: [],
    // annotationEndpoint: {
    //   name:"Local Storage",
    //   module: "LocalStorageEndpoint" }
    annotationEndpoint: {
      name:"Mirador OA Protocol Endpoint Storage",
      module: "MiradorOAProtocolEndpoint" 
    }
  });
});

