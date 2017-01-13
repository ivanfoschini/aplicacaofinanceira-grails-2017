package aplicacaofinanceirarestful

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class ClientePessoaJuridicaController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AutorizacaoService autorizacaoService
    CidadeService cidadeService
    ClientePessoaJuridicaService clientePessoaJuridicaService
    EnderecoService enderecoService
    MessageSource messageSource

    def delete() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
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
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }     
    }

    def index() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            List<ClientePessoaJuridica> clientesPessoasJuridicas = clientePessoaJuridicaService.findAllOrderByNome()
            respond clientePessoaJuridicaService.clientePessoaJuridicaCompactResponse(clientesPessoasJuridicas)
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }    
    }

    def save() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
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
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }   
    }

    def show() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            ClientePessoaJuridica clientePessoaJuridica = clientePessoaJuridicaService.findById(params.id as Long)

            if (clientePessoaJuridica) {
                respond clientePessoaJuridicaService.clientePessoaJuridicaComEnderecoResponse(clientePessoaJuridica)
            } else {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.ClientePessoaJuridica.not.found', null, null))
            }
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }    
    }

    def update() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
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
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }     
    }
}