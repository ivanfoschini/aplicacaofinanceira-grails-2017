package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class ContaPoupancaController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AgenciaService agenciaService
    ContaPoupancaService contaPoupancaService
    MessageSource messageSource

    def delete() {
        ContaPoupanca contaPoupanca = contaPoupancaService.findById(params.id as Long)

        if (!contaPoupanca) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ContaPoupanca.not.found', null, null))
            return
        }

        contaPoupanca.delete(flush: true)
        render status: HttpStatus.NO_CONTENT
    }

    def index() {
        respond contaPoupancaService.findAllOrderByNumero()
    }

    def save() {
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
    }

    def show() {
        ContaPoupanca contaPoupanca = contaPoupancaService.findById(params.id as Long)

        if (contaPoupanca) {
            respond contaPoupanca
        } else {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ContaPoupanca.not.found', null, null))
        }
    }

    def update() {
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
    }
}