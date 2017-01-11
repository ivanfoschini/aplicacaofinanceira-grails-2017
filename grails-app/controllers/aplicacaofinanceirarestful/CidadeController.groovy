package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class CidadeController {
    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AutorizacaoService autorizacaoService
    CidadeService cidadeService
    EstadoService estadoService
    MessageSource messageSource

    def delete() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Cidade cidade = cidadeService.findById(params.id as Long)

            if (!cidade) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null))
                return
            }

            cidade.delete(flush: true)
            render status: HttpStatus.NO_CONTENT
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def index() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            respond cidadeService.findAllOrderByNome()
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }    
    }

    def save() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            JSONObject jsonObject = request.JSON

            if (!estadoService.validateEstado(jsonObject)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            if (!estadoService.verifyCidadeIsUnique(jsonObject)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.unique.for.Estado', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            JsonSlurper jsonSlurper = new JsonSlurper()
            Cidade cidade = new Cidade(jsonSlurper.parseText(jsonObject.toString()))

            cidade.save(flush: true)
            respond cidade, [status: HttpStatus.CREATED, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def show() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Cidade cidade = cidadeService.findById(params.id as Long)

            if (cidade) {
                respond cidade
            } else {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null))
            }
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def update() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Cidade cidade = cidadeService.findById(params.id as Long)

            if (!cidade) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null))
                return
            }

            JSONObject jsonObject = request.JSON

            if (!estadoService.validateEstado(jsonObject)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            if (!estadoService.verifyCidadeIsUnique(jsonObject, cidade.id)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.unique.for.Estado', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            JsonSlurper jsonSlurper = new JsonSlurper()
            cidade.properties = jsonSlurper.parseText(request.JSON.toString())

            cidade.save(flush: true)
            respond cidade, [status: HttpStatus.OK, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }
}