package aplicacaofinanceirarestful

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

class AgenciaController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    def agenciaService
    def bancoService
    def cidadeService
    def messageSource

    def delete() {
        Agencia agencia = agenciaService.findById(params.id as Long)

        if (!agencia) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null))
        }

        agencia.endereco.delete()
        agencia.delete(flush: true)
        render status: HttpStatus.NO_CONTENT
    }

    def index() {
        respond agenciaService.findAllOrderByNome()
    }

    def save() {
        JSONObject jsonObject = request.JSON

        if (!bancoService.validateBanco(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        if (!cidadeService.validateCidade(jsonObject)) {
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
    }

    def show() {
        Agencia agencia = agenciaService.findById(params.id as Long)

        if (agencia) {
            respond agencia
        } else {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null))
        }
    }

    def update() {
        Agencia agencia = Agencia.get(params.id)

        if (!agencia) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Agencia.not.found', null, null))
        }

        JSONObject jsonObject = request.JSON

        if (!bancoService.validateBanco(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        if (!cidadeService.validateCidade(jsonObject)) {
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
    }
}