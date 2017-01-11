package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class EstadoController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AutorizacaoService autorizacaoService
    EstadoService estadoService
    MessageSource messageSource

    def delete() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Estado estado = estadoService.findById(params.id as Long)

            if (!estado) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null))
                return
            }

            if (!estadoService.verifyDeletion(estado)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Estado.has.cidades', null, null), status: HttpStatus.CONFLICT
                return
            }

            estado.delete(flush: true)
            render status: HttpStatus.NO_CONTENT
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def index() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            respond estadoService.findAllOrderByNome()
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }    
    }

    def save() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            JsonSlurper jsonSlurper = new JsonSlurper()
            Estado estado = new Estado(jsonSlurper.parseText(request.JSON.toString()))

            estado.save(flush: true)
            respond estado, [status: HttpStatus.CREATED, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def show() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Estado estado = estadoService.findById(params.id as Long)

            if (estado) {
                respond estado
            } else {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null))
            }
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def update() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Estado estado = estadoService.findById(params.id as Long)

            if (!estado) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null))
                return
            }

            JsonSlurper jsonSlurper = new JsonSlurper()
            estado.properties = jsonSlurper.parseText(request.JSON.toString())

            estado.save(flush: true)
            respond estado, [status: HttpStatus.OK, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }
}