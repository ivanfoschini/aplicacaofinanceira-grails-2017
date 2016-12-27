package aplicacaofinanceirarestful

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class ClientePessoaFisicaController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    CidadeService cidadeService
    ClientePessoaFisicaService clientePessoaFisicaService
    EnderecoService enderecoService
    MessageSource messageSource

//    def delete() {
//        ClientePessoaFisica clientePessoaFisica = ClientePessoaFisica.get(params.id)
//
//        if (!clientePessoaFisica) {
//            respondNotFound()
//            return
//        }
//
//        clientePessoaFisica.enderecos.each { endereco ->
//            endereco.delete()
//        }
//
//        clientePessoaFisica.delete(flush: true)
//        respondDeleted()
//    }
//
//    def index() {
//        def pessoasFisicas = ClientePessoaFisica.findAll("from ClientePessoaFisica as cli order by cli.nome")
//
//        JSON.use('compactClientePessoaFisica')
//
//        render pessoasFisicas as JSON
//    }

    def save() {
        JSONObject jsonObject = request.JSON

        if (!enderecoService.validateEnderecos(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.ClientePessoaFisica.enderecos.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        if (!cidadeService.validateCidadeForCliente(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        ClientePessoaFisica clientePessoaFisica = jsonSlurper.parseText(jsonObject.toString())

        def responseBody = clientePessoaFisicaService.validate(clientePessoaFisica, messageSource, request, response)

        if (responseBody.isEmpty()) {
            clientePessoaFisica.enderecos.each { endereco ->
                clientePessoaFisica.addToEnderecos(endereco)
            }

            clientePessoaFisica.save(flush: true)
            respond clientePessoaFisicaService.createClientePessoaFisicaComEnderecoResponse(clientePessoaFisica), status: HttpStatus.CREATED
        } else {
            render responseBody as JSON
        }
    }

//    def show() {
//        ClientePessoaFisica clientePessoaFisica = ClientePessoaFisica.get(params.id)
//
//        if (clientePessoaFisica) {
//            respondFound(clientePessoaFisica)
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
//        def clientePessoaFisica = ClientePessoaFisica.get(params.id)
//
//        if (!clientePessoaFisica) {
//            respondNotFound()
//            return
//        }
//
//        if (params.version != null) {
//            if (clientePessoaFisica.version > params.long('version')) {
//                respondConflict(clientePessoaFisica)
//                return
//            }
//        }
//
//        JSONObject jsonObject = request.JSON
//
//        if (!validateEnderecos(jsonObject)) {
//            respondEnderecosNotFound()
//            return
//        }
//
//        if (!validateCidadeForAgencia(jsonObject)) {
//            respondCidadeNotFound()
//            return
//        }
//
//        clientePessoaFisica.enderecos.each { endereco ->
//            endereco.delete()
//        }
//
//        JsonSlurper jsonSlurper = new JsonSlurper()
//        clientePessoaFisica.properties = jsonSlurper.parseText(request.JSON.toString())
//
//        def responseBody = validate(clientePessoaFisica)
//
//        if (responseBody.errors.isEmpty()) {
//            clientePessoaFisica.save(flush: true)
//            respondUpdated(clientePessoaFisica)
//        } else {
//            response.status = ConstantUtil.instance.SC_UNPROCESSABLE_ENTITY
//            render responseBody as JSON
//        }
//    }
}