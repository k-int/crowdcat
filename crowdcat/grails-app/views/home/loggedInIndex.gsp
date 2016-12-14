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
          <li role="navigation"><a data-toggle="tab" href="#universes">Universes</a></li>
          <li role="navigation"><a data-toggle="tab" href="#projects">Projects</a></li>
        </ul>

        <div class="tab-content">
          <div id="overview" class="tab-pane active">
            <h3>Overview</h3>
            <p>Some content.</p>
          </div>

          <div id="universes" class="tab-pane">
            <h3>Universes</h3>
            <g:link action="createUniverse" class="btn btn-success pull-right">Create Universe</g:link>
            <p>Some content.</p>
            <table class="table table-striped">
              <tbody>
                <g:each in="${unilist}" var="u">
                  <tr>
                    <td>${u}</td>
                  </tr>
                </g:each>
              </tbody>
            </table>
          </div>

          <div id="projects" class="tab-pane">
            <h3>Projects</h3>
            <p>Some content.</p>
          </div>
        </div>

      </div>
    </div>
  </div>

</body>
</html>
