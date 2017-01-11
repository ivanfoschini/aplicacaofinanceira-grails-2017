package aplicacaofinanceirarestful

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class AgenciaController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AgenciaService agenciaService
    AutorizacaoService autorizacaoService
    BancoService bancoService
    CidadeService cidadeService
    MessageSource messageSource

    def delete() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Agencia agencia = agenciaService.findById(params.id as Long)

            if (!agencia) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null))
                return
            }

            if (!agenciaService.verifyDeletion(agencia)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Agencia.has.contas', null, null), status: HttpStatus.CONFLICT
                return
            }

            agencia.endereco.delete()
            agencia.delete(flush: true)
            render status: HttpStatus.NO_CONTENT
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }      
    }

    def index() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            respond agenciaService.findAllOrderByNome()
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }    
    }

    def save() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            JSONObject jsonObject = request.JSON

            if (!bancoService.validateBanco(jsonObject)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            if (!cidadeService.validateCidadeForAgencia(jsonObject)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            JsonSlurper jsonSlurper = new JsonSlurper()
            Agencia agencia = new Agencia()
            agencia.endereco = new Endereco()
            agencia = jsonSlurper.parseText(jsonObject.toString())

            def responseBody = agenciaService.validate(agencia, messageSource, request, response)

            if (responseBody.isEmpty()) {
                agencia.endereco.save()
                agencia.save(flush: true)

                respond agencia, [status: HttpStatus.CREATED, view:'show']
            } else {
                render responseBody as JSON
            }
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def show() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Agencia agencia = agenciaService.findById(params.id as Long)

            if (agencia) {
                respond agencia
            } else {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null))
            }
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def update() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Agencia agencia = agenciaService.findById(params.id as Long)

            if (!agencia) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null))
                return
            }

            JSONObject jsonObject = request.JSON

            if (!bancoService.validateBanco(jsonObject)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            if (!cidadeService.validateCidadeForAgencia(jsonObject)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            JsonSlurper jsonSlurper = new JsonSlurper()
            agencia.properties = jsonSlurper.parseText(request.JSON.toString())

            def responseBody = agenciaService.validate(agencia, messageSource, request, response)

            if (responseBody.isEmpty()) {
                agencia.save(flush: true)

                respond agencia, [status: HttpStatus.OK, view:'show']
            } else {
                render responseBody as JSON
            }
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }        
    }
}