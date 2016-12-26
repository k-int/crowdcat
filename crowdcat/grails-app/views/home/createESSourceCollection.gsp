<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title></title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>

  <div class="container">
    <div class="row">
      <div class="col-md-12">
        <h1>Create Source Collection</h1>
        <p>A Source Collection is a source context in CrowdCat grouping records from a source together.</p>
        <g:form action="createUniverse" method="POST">
          <div class="form-group">
             <label for="newUniverseName">Source Collection Name</label>
             <input class="form-control" id="newSourceCollectionName" placeholder="Name of new source collection" name="newSourceCollectionName" type="text">
          </div>
          <button type="submit" class="btn btn-default">Submit</button>
        </g:form>
      </div>
    </div>
  </div>
  

</body>
</html>
