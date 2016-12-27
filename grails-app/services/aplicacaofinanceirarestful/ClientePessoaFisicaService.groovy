package aplicacaofinanceirarestful

import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.validation.Errors
import org.springframework.validation.ObjectError

@Transactional
class ClientePessoaFisicaService {

    def clientePessoaFisicaComEnderecoResponse(ClientePessoaFisica clientePessoaFisica) {
        def enderecosList = []

        clientePessoaFisica.enderecos.each { endereco ->
            def enderecoMap = [
                    enderecoId: endereco.id,
                    enderecoLogradouro: endereco.logradouro,
                    enderecoNumero: endereco.numero,
                    enderecoComplemento: endereco.complemento,
                    enderecoBairro: endereco.bairro,
                    enderecoCep: endereco.cep,
                    enderecoCidade: endereco.cidade.nome,
                    enderecoEstado: endereco.cidade.estado.nome
            ]

            enderecosList.add(enderecoMap)
        }

        def enderecosMap = enderecosList

        def clientePessoaFisicaMap = [
                id: clientePessoaFisica.id,
                nome: clientePessoaFisica.nome,
                status: clientePessoaFisica.status,
                rg: clientePessoaFisica.rg,
                cpf: clientePessoaFisica.cpf,
                enderecos: enderecosMap
        ]

        return clientePessoaFisicaMap
    }

    def clientePessoaFisicaCompactResponse(List<ClientePessoaFisica> clientesPessoasFisicas) {
        def clientesPessoasFisicasList = []

        clientesPessoasFisicas.each { ClientePessoaFisica clientePessoaFisica ->
            def clientePessoaFisicaMap = [
                id: clientePessoaFisica.id,
                nome: clientePessoaFisica.nome,
                status: clientePessoaFisica.status,
                rg: clientePessoaFisica.rg,
                cpf: clientePessoaFisica.cpf
            ]

            clientesPessoasFisicasList.add(clientePessoaFisicaMap)
        }

        return clientesPessoasFisicasList
    }

    def findAllOrderByNome() {
        return ClientePessoaFisica.findAll("from ClientePessoaFisica as clientePessoaFisica order by clientePessoaFisica.nome")
    }

    def findById(Long id) {
        return ClientePessoaFisica.get(id)
    }

    def validate(clientePessoaFisica, messageSource, request, response) {
        JSONArray errorsJSONArray = new JSONArray()

        if (!clientePessoaFisica.validate()) {
            Errors errorsObject = (Errors) clientePessoaFisica.errors
            def allErrors = errorsObject.allErrors

            allErrors.each { ObjectError error ->
                JSONObject errorJSONObject = new JSONObject()
                errorJSONObject.put('message', messageSource.getMessage(error, null))
                errorsJSONArray.put(errorJSONObject)
            }
        }

        clientePessoaFisica.enderecos.each { Endereco endereco ->
            if (!endereco.validate()) {
                Errors errorsObject = (Errors) endereco.errors
                def allErrors = errorsObject.allErrors

                allErrors.each { ObjectError error ->
                    if (!error.field.toString().startsWith('cliente')) {
                        JSONObject errorJSONObject = new JSONObject()
                        errorJSONObject.put('message', messageSource.getMessage(error, null))
                        errorsJSONArray.put(errorJSONObject)
                    }
                }
            }
        }

        def responseBody = [:]

        if (!errorsJSONArray.isEmpty()) {
            int status = HttpStatus.UNPROCESSABLE_ENTITY.value()

            responseBody.timestamp = new Date().getTime()
            responseBody.status = status
            responseBody.error = 'Unprocessable Entity'
            responseBody.errors = errorsJSONArray
            responseBody.path = request.requestURI

            response.setStatus(status)
        }

        return responseBody
    }
}