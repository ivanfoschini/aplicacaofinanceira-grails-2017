package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class ContaCorrenteController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AgenciaService agenciaService
    ContaCorrenteService contaCorrenteService
    MessageSource messageSource

    def delete() {
        ContaCorrente contaCorrente = contaCorrenteService.findById(params.id as Long)

        if (!contaCorrente) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ContaCorrente.not.found', null, null))
        }

        contaCorrente.delete(flush: true)
        render status: HttpStatus.NO_CONTENT
    }

    def index() {
        respond contaCorrenteService.findAllOrderByNumero()
    }

    def save() {
        JSONObject jsonObject = request.JSON

        if (!DateUtil.instance.validateDataDeAbertura(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.ContaCorrente.dataDeAbertura.nullable', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        if (!agenciaService.validateAgencia(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        ContaCorrente contaCorrente = jsonSlurper.parseText(jsonObject.toString())

        contaCorrente.save(flush: true)
        respond contaCorrente, [status: HttpStatus.CREATED, view: 'show']
    }

    def show() {
        ContaCorrente contaCorrente = contaCorrenteService.findById(params.id as Long)

        if (contaCorrente) {
            respond contaCorrente
        } else {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ContaCorrente.not.found', null, null))
        }
    }

    def update() {
        ContaCorrente contaCorrente = contaCorrenteService.findById(params.id as Long)

        if (!contaCorrente) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ContaCorrente.not.found', null, null))
            return
        }

        JSONObject jsonObject = request.JSON

        if (!DateUtil.instance.validateDataDeAbertura(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.ContaCorrente.dataDeAbertura.nullable', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        if (!agenciaService.validateAgencia(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        contaCorrente.properties = jsonSlurper.parseText(request.JSON.toString())

        contaCorrente.save(flush: true)
        respond contaCorrente, [status: HttpStatus.OK, view: 'show']
    }
}