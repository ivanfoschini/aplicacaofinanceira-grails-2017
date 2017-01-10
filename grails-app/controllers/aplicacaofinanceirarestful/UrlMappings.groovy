package aplicacaofinanceirarestful

class UrlMappings {

    static mappings = {
        delete "/$controller/$id(.$format)?"(action:"delete")
        get "/$controller(.$format)?"(action:"index")
        get "/$controller/$id(.$format)?"(action:"show")
        post "/$controller(.$format)?"(action:"save")
        put "/$controller/$id(.$format)?"(action:"update")
        patch "/$controller/$id(.$format)?"(action:"patch")

        "/"(controller: 'application', action:'index')
        "500"(view: '/error')
        "404"(view: '/notFound')

        "/correntista/associate"(controller: 'correntista', action: 'associate')
        "/correntista/showByCliente/$id"(controller: 'correntista', action: 'showByCliente')
        "/correntista/showByConta/$id"(controller: 'correntista', action: 'showByConta')

        "/login/login"(controller: 'login', action: 'login')

        "/logout/logout"(controller: 'logout', action: 'logout')
    }
}
