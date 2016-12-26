<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title></title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>
  <div class="container-fluid">
    <div class="row">
      <div class="col-md-2">
        Logged in user
        User Profile Info.
        <br/>
        <p>Bio</p>
      </div>
      <div class="col-md-10">

        <ul class="nav nav-tabs">
          <li role="navigation" class="active"><a data-toggle="tab" href="#overview">Overview</a></li>
          <li role="navigation"><a data-toggle="tab" href="#sourceCollections">Source Collections</a></li>
          <li role="navigation"><a data-toggle="tab" href="#projects">Projects</a></li>
        </ul>

        <div class="tab-content">
          <div id="overview" class="tab-pane active">
            <h3>Overview</h3>
            <p>Some content.</p>
          </div>

          <div id="sourceCollections" class="tab-pane">
            <h3>Source Collections</h3>
            <g:link action="createSourceCollection" class="btn btn-success pull-right">New Source Collection</g:link>
            <table class="table table-striped">
              <tbody>
                <g:each in="${sclist}" var="sc">
                  <tr>
                    <td>${sc}</td>
                  </tr>
                </g:each>
              </tbody>
            </table>
          </div>

          <div id="projects" class="tab-pane">
            <h3>Projects</h3>
            <g:link action="createProject" class="btn btn-success pull-right">New Project</g:link>
            <table class="table table-striped">
              <tbody>
                <g:each in="${projList}" var="p">
                  <tr>
                    <td>${p}</td>
                  </tr>
                </g:each>
              </tbody>
            </table>
          </div>

          </div>
        </div>

      </div>
    </div>
  </div>

</body>
</html>
