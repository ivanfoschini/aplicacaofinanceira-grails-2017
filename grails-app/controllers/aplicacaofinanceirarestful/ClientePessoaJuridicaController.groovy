package aplicacaofinanceirarestful

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class ClientePessoaJuridicaController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    CidadeService cidadeService
    ClientePessoaJuridicaService clientePessoaJuridicaService
    EnderecoService enderecoService
    MessageSource messageSource

    def delete() {
        ClientePessoaJuridica clientePessoaJuridica = clientePessoaJuridicaService.findById(params.id as Long)

        if (!clientePessoaJuridica) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ClientePessoaJuridica.not.found', null, null))
            return
        }

        if (!clientePessoaJuridicaService.verifyDeletion(clientePessoaJuridica)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Cliente.has.correntistas', null, null), status: HttpStatus.CONFLICT
            return
        }

        clientePessoaJuridica.enderecos.each { endereco ->
            endereco.delete()
        }

        clientePessoaJuridica.delete(flush: true)
        render status: HttpStatus.NO_CONTENT
    }

    def index() {
        List<ClientePessoaJuridica> clientesPessoasJuridicas = clientePessoaJuridicaService.findAllOrderByNome()
        respond clientePessoaJuridicaService.clientePessoaJuridicaCompactResponse(clientesPessoasJuridicas)
    }

    def save() {
        JSONObject jsonObject = request.JSON

        if (!enderecoService.validateEnderecos(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.ClientePessoaJuridica.enderecos.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        if (!cidadeService.validateCidadeForCliente(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        ClientePessoaJuridica clientePessoaJuridica = jsonSlurper.parseText(jsonObject.toString())

        def responseBody = clientePessoaJuridicaService.validate(clientePessoaJuridica, messageSource, request, response)

        if (responseBody.isEmpty()) {
            clientePessoaJuridica.enderecos.each { endereco ->
                clientePessoaJuridica.addToEnderecos(endereco)
            }

            clientePessoaJuridica.save(flush: true)
            respond clientePessoaJuridicaService.clientePessoaJuridicaComEnderecoResponse(clientePessoaJuridica), status: HttpStatus.CREATED
        } else {
            render responseBody as JSON
        }
    }

    def show() {
        ClientePessoaJuridica clientePessoaJuridica = clientePessoaJuridicaService.findById(params.id as Long)

        if (clientePessoaJuridica) {
            respond clientePessoaJuridicaService.clientePessoaJuridicaComEnderecoResponse(clientePessoaJuridica)
        } else {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ClientePessoaJuridica.not.found', null, null))
        }
    }

    def update() {
        ClientePessoaJuridica clientePessoaJuridica = ClientePessoaJuridica.findById(params.id as Long)

        if (!clientePessoaJuridica) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ClientePessoaJuridica.not.found', null, null))
            return
        }

        JSONObject jsonObject = request.JSON

        if (!enderecoService.validateEnderecos(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.ClientePessoaJuridica.enderecos.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        if (!cidadeService.validateCidadeForCliente(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        clientePessoaJuridica.enderecos.each { endereco ->
            endereco.delete()
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        clientePessoaJuridica.properties = jsonSlurper.parseText(request.JSON.toString())

        def responseBody = clientePessoaJuridicaService.validate(clientePessoaJuridica, messageSource, request, response)

        if (responseBody.isEmpty()) {
            clientePessoaJuridica.save(flush: true)

            respond clientePessoaJuridicaService.clientePessoaJuridicaComEnderecoResponse(clientePessoaJuridica), status: HttpStatus.CREATED
        } else {
            render responseBody as JSON
        }
    }
}