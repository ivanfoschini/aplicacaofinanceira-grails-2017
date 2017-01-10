package aplicacaofinanceirarestful

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class AgenciaController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AgenciaService agenciaService
    BancoService bancoService
    CidadeService cidadeService
    MessageSource messageSource

    def delete() {
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
    }
}