package aplicacaofinanceirarestful


import grails.rest.*
import grails.converters.*
import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class ContaCorrenteController {

    static allowedMethods = [delete: "DELETE", list: "GET", save: "POST", show: "GET", update: "PUT"]

    AgenciaService agenciaService
    MessageSource messageSource

//    def delete() {
//        ContaCorrente contaCorrente = ContaCorrente.get(params.id)
//
//        if (!contaCorrente) {
//            respondNotFound()
//            return
//        }
//
//        contaCorrente.delete(flush: true)
//        respondDeleted()
//    }
//
//    def list() {
//        def contasCorrente = ContaCorrente.findAll("from ContaCorrente as con order by con.numero")
//
//        JSON.use('compactContaCorrente')
//
//        render contasCorrente as JSON
//    }

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

//    def show() {
//        ContaCorrente contaCorrente = ContaCorrente.get(params.id)
//
//        if (contaCorrente) {
//            respondFound(contaCorrente)
//        } else {
//            respondNotFound()
//        }
//    }
//
//    def update() {
//        if (!JSONUtil.instance.requestIsJson(request)) {
//            JSONUtil.instance.respondNotAcceptable(response)
//            return
//        }
//
//        def contaCorrente = ContaCorrente.get(params.id)
//
//        if (!contaCorrente) {
//            respondNotFound()
//            return
//        }
//
//        if (params.version != null) {
//            if (contaCorrente.version > params.long('version')) {
//                respondConflict(contaCorrente)
//                return
//            }
//        }
//
//        JSONObject jsonObject = request.JSON
//
//        if (!validateAgencia(jsonObject)) {
//            respondAgenciaNotFound()
//            return
//        }
//
//        String dataDeAberturaString = jsonObject.get("dataDeAbertura")
//        jsonObject.remove("dataDeAbertura")
//
//        JsonSlurper jsonSlurper = new JsonSlurper()
//        contaCorrente.properties = jsonSlurper.parseText(request.JSON.toString())
//        contaCorrente.dataDeAbertura = DateUtil.instance.stringToDate(dataDeAberturaString)
//
//        def responseBody = validate(contaCorrente)
//
//        if (responseBody.errors.isEmpty()) {
//            contaCorrente.save(flush: true)
//            respondUpdated(contaCorrente)
//        } else {
//            response.status = ConstantUtil.instance.SC_UNPROCESSABLE_ENTITY
//            render responseBody as JSON
//        }
//    }
}