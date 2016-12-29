package crowdcat

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller:'home', action:'index')
        "/annotation"(controller:'annotation', action:'index')
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
