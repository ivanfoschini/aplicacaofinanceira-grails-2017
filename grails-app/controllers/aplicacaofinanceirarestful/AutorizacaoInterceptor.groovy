package aplicacaofinanceirarestful

import grails.converters.JSON
import org.springframework.context.MessageSource

class AutorizacaoInterceptor {

    AutorizacaoService autorizacaoService
    MessageSource messageSource

    AutorizacaoInterceptor() {
        matchAll().excludes(controller: 'login')
    }

    boolean before() {
        println request.requestURI
        println request.method

        //Colocar quartz
        //Alterar bootstrap e classe SErvico para colocar a uri certa e o metodo. Colocar tambem o logout como um servico restrito
        //Fazer o else abaixo retornar uma excecao como as outras (o metodo deve ficar em autorizacaoService
        //Revisar texto da aula para ver se nao falta nada


        boolean autorizado = autorizacaoService.autorizar(request, params)

        if (autorizado) {
            return true
        } else {
            def responseBody = [:]
            responseBody.message = messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null)
            render responseBody as JSON

            return false
        }
    }

    boolean after() { return true }

    void afterView() {
        // no-op
    }
}
