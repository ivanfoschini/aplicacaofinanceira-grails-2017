package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class ContaPoupancaController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AgenciaService agenciaService
    AutorizacaoService autorizacaoService
    ContaPoupancaService contaPoupancaService
    MessageSource messageSource

    def delete() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            ContaPoupanca contaPoupanca = contaPoupancaService.findById(params.id as Long)

            if (!contaPoupanca) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ContaPoupanca.not.found', null, null))
                return
            }

            if (!contaPoupancaService.verifyDeletion(contaPoupanca)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Conta.has.correntistas', null, null), status: HttpStatus.CONFLICT
                return
            }

            contaPoupanca.delete(flush: true)
            render status: HttpStatus.NO_CONTENT
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }
    }

    def index() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            respond contaPoupancaService.findAllOrderByNumero()
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def save() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            JSONObject jsonObject = request.JSON

            if (!DateUtil.instance.validateDateFromJSON(jsonObject, 'dataDeAbertura')) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.ContaPoupanca.dataDeAbertura.nullable', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            if (!DateUtil.instance.validateDateFromJSON(jsonObject, 'dataDeAniversario')) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.ContaPoupanca.dataDeAniversario.nullable', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            if (!agenciaService.validateAgencia(jsonObject)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            JsonSlurper jsonSlurper = new JsonSlurper()
            ContaPoupanca contaPoupanca = jsonSlurper.parseText(jsonObject.toString())

            contaPoupanca.save(flush: true)
            respond contaPoupanca, [status: HttpStatus.CREATED, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def show() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            ContaPoupanca contaPoupanca = contaPoupancaService.findById(params.id as Long)

            if (contaPoupanca) {
                respond contaPoupanca
            } else {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ContaPoupanca.not.found', null, null))
            }
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    def update() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            ContaPoupanca contaPoupanca = contaPoupancaService.findById(params.id as Long)

            if (!contaPoupanca) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ContaPoupanca.not.found', null, null))
                return
            }

            JSONObject jsonObject = request.JSON

            if (!DateUtil.instance.validateDateFromJSON(jsonObject, 'dataDeAbertura')) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.ContaPoupanca.dataDeAbertura.nullable', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            if (!DateUtil.instance.validateDateFromJSON(jsonObject, 'dataDeAniversario')) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.ContaPoupanca.dataDeAniversario.nullable', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            if (!agenciaService.validateAgencia(jsonObject)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
                return
            }

            JsonSlurper jsonSlurper = new JsonSlurper()
            contaPoupanca.properties = jsonSlurper.parseText(request.JSON.toString())

            contaPoupanca.save(flush: true)
            respond contaPoupanca, [status: HttpStatus.OK, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }
}