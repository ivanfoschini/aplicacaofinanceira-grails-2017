package aplicacaofinanceirarestful

import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.validation.Errors
import org.springframework.validation.ObjectError

@Transactional
class ClientePessoaJuridicaService {

    def clientePessoaJuridicaComEnderecoResponse(ClientePessoaJuridica clientePessoaJuridica) {
        def enderecosList = []

        clientePessoaJuridica.enderecos.each { endereco ->
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

        def clientePessoaJuridicaMap = [
                id: clientePessoaJuridica.id,
                nome: clientePessoaJuridica.nome,
                status: clientePessoaJuridica.status,
                cnpj: clientePessoaJuridica.cnpj,
                enderecos: enderecosMap
        ]

        return clientePessoaJuridicaMap
    }

    def clientePessoaJuridicaCompactResponse(List<ClientePessoaJuridica> clientesPessoasJuridicas) {
        def clientesPessoasJuridicasList = []

        clientesPessoasJuridicas.each { ClientePessoaJuridica clientePessoaJuridica ->
            def clientePessoaJuridicaMap = [
                    id: clientePessoaJuridica.id,
                    nome: clientePessoaJuridica.nome,
                    status: clientePessoaJuridica.status,
                    cnpj: clientePessoaJuridica.cnpj
            ]

            clientesPessoasJuridicasList.add(clientePessoaJuridicaMap)
        }

        return clientesPessoasJuridicasList
    }

    def findAllOrderByNome() {
        return ClientePessoaJuridica.findAll("from ClientePessoaJuridica as clientePessoaJuridica order by clientePessoaJuridica.nome")
    }

    def findById(Long id) {
        return ClientePessoaJuridica.get(id)
    }

    def validate(clientePessoaJuridica, messageSource, request, response) {
        JSONArray errorsJSONArray = new JSONArray()

        if (!clientePessoaJuridica.validate()) {
            Errors errorsObject = (Errors) clientePessoaJuridica.errors
            def allErrors = errorsObject.allErrors

            allErrors.each { ObjectError error ->
                JSONObject errorJSONObject = new JSONObject()
                errorJSONObject.put('message', messageSource.getMessage(error, null))
                errorsJSONArray.put(errorJSONObject)
            }
        }

        clientePessoaJuridica.enderecos.each { Endereco endereco ->
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