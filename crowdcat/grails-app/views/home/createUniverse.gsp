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
        <h1>Create Universe</h1>
        <p>A Universe is a context in CrowdCat collecting together records from many different sources 
           into a coherent set with some particular purpose. Universes help manage the process
           of organising volunteers describing and enriching content by defining the schemas and record sets
           to be used in that process.
           </p>
        <g:form action="createUniverse" method="POST">
          <div class="form-group">
             <label for="newUniverseName">Universe Name</label>
             <input class="form-control" id="newUniverseName" placeholder="Name of this universe" name="newUniverseName" type="text">
          </div>
          <button type="submit" class="btn btn-default">Submit</button>
        </g:form>
      </div>
    </div>
  </div>
  

</body>
</html>
