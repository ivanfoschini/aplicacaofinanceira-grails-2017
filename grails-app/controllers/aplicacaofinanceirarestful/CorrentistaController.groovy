package aplicacaofinanceirarestful

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class CorrentistaController {

    static allowedMethods = [associate: "POST", showByCliente: "GET", showByConta: "GET"]

    CorrentistaService correntistaService
    MessageSource messageSource

    def associate() {
        JSONObject jsonObject = request.JSON
        JSONArray jsonCorrentistasArray = jsonObject.get("correntistas")
        JsonSlurper jsonSlurper = new JsonSlurper()
        def correntistas = []
        def responseBody
        Conta conta

        jsonCorrentistasArray.each { jsonCorrentistaObject ->
            Correntista correntista = new Correntista(jsonSlurper.parseText(jsonCorrentistaObject.toString()))

            responseBody = correntistaService.validate(correntista, messageSource, request, response)

            if (!responseBody.isEmpty()) {
                render responseBody as JSON
                return
            }

            correntistas.add(correntista)
        }

        def validateTitularidadeResult = correntistaService.validateTitularidade(jsonCorrentistasArray)

        if (validateTitularidadeResult != null) {
            render message: messageSource.getMessage(validateTitularidadeResult, null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        def validateContaResult = correntistaService.validateConta(jsonCorrentistasArray)

        if (validateContaResult instanceof String) {
            render message: messageSource.getMessage(validateContaResult, null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        } else if (validateContaResult instanceof Conta) {
            conta = validateContaResult
        }

        def validateClienteResult = correntistaService.validateCliente(jsonCorrentistasArray)

        if (validateClienteResult != null) {
            render message: messageSource.getMessage(validateClienteResult, null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        Correntista.executeUpdate("delete Correntista cor where cor.conta.id = " + conta.id)

        correntistas.each { correntista ->
            correntista.save(flush: true)
        }

        respond correntistaService.associateResponse(jsonObject), status: HttpStatus.OK
    }

    def showByCliente() {
        Cliente cliente = Cliente.get(params.id)

        if (!cliente) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Cliente.not.found', null, null))
        } else {
            respond correntistaService.showByClienteResponse(cliente), status: HttpStatus.OK
        }
    }

    def showByConta() {
        Conta conta = Conta.get(params.id)

        if (!conta) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Conta.not.found', null, null))
        } else {
            respond correntistaService.showByContaResponse(conta), status: HttpStatus.OK
        }
    }
}
