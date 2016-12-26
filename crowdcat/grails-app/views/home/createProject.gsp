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
        <h1>Create Project</h1>
        <p>A project is a combination of record sources, selection rules and completion goals. Projects help administrators
           target sets of records, set rules for which items should be included (EG Only items that don't currently have a description) and set an aim for the project (EG Make sure every record has a description)</p>
        <g:form action="createProject" method="POST">
          <div class="form-group">
             <label for="newProject">Project Name</label>
             <input class="form-control" id="newProjectName" placeholder="Name of new project" name="newProjectName" type="text">
          </div>
          <button type="submit" class="btn btn-default">Submit</button>
        </g:form>
      </div>
    </div>
  </div>
  

</body>
</html>
