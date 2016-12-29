package aplicacaofinanceirarestful

import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.validation.Errors
import org.springframework.validation.ObjectError

@Transactional
class CorrentistaService {

    def associateResponse(jsonObject) {
        JSONArray jsonCorrentistasArray = jsonObject.get("correntistas")
        JSONObject jsonCorrentistaObject = jsonCorrentistasArray[0]

        Conta conta = Conta.get(jsonCorrentistaObject.get("conta").get("id"))

        def correntistas = Correntista.findAllByConta(conta)

        def clientesList = []

        correntistas.each { correntista ->
            final clienteMap = [
                    clienteId: correntista.cliente.id,
                    clienteNome: correntista.cliente.nome,
                    titularidade: correntista.titularidade
            ]

            clientesList.add(clienteMap)
        }

        def clientesMap = clientesList

        def correntistaMap = [
            contaId: conta.id,
            contaNumero: conta.numero,
            clientes: clientesMap
        ]

        return correntistaMap
    }

    def showByClienteResponse(cliente) {
        def correntistas = Correntista.findAllByCliente(cliente)

        def contasList = []

        correntistas.each { correntista ->
            final contaMap = [
                contaId: correntista.conta.id,
                contaNumero: correntista.conta.numero,
                titularidade: correntista.titularidade
            ]

            contasList.add(contaMap)
        }

        def contasMap = contasList

        def correntistaMap = [
                clienteId: cliente.id,
                clienteNome: cliente.nome,
                contas: contasMap
        ]

        return correntistaMap
    }

    def showByContaResponse(conta) {
        def correntistas = Correntista.findAllByConta(conta)

        def clientesList = []

        correntistas.each { correntista ->
            final clienteMap = [
                    clienteId: correntista.cliente.id,
                    clienteNome: correntista.cliente.nome,
                    titularidade: correntista.titularidade
            ]

            clientesList.add(clienteMap)
        }

        def clientesMap = clientesList

        def correntistaMap = [
            contaId: conta.id,
            contaNumero: conta.numero,
            clientes: clientesMap
        ]

        return correntistaMap
    }

    def validate(correntista, messageSource, request, response) {
        JSONArray errorsJSONArray = new JSONArray()

        if (!correntista.validate()) {
            Errors errorsObject = (Errors) correntista.errors
            def allErrors = errorsObject.allErrors

            allErrors.each { ObjectError error ->
                JSONObject errorJSONObject = new JSONObject()
                errorJSONObject.put('message', messageSource.getMessage(error, null))
                errorsJSONArray.put(errorJSONObject)
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

    def validateCliente(jsonCorrentistasArray) {
        def clienteIsValid = true
        def clientesIds = []

        jsonCorrentistasArray.each { jsonCorrentistaObject ->
            Long clienteId = jsonCorrentistaObject.get("cliente").get("id") as Long

            Cliente cliente = Cliente.get(clienteId)

            if (!cliente) {
                clienteIsValid = false
            }

            clientesIds.add(clienteId)
        }

        if (!clienteIsValid) {
            return 'aplicacaofinanceirarestful.Cliente.not.found'
        }

        if (clientesIds.size() != clientesIds.unique().size()) {
            return 'aplicacaofinanceirarestful.Correntista.duplicated.cliente'
        }

        return null
    }

    def validateConta(jsonCorrentistasArray) {
        def contaIsValid = true
        def contasIds = []
        Conta conta

        jsonCorrentistasArray.each { jsonCorrentistaObject ->
            Long contaId = jsonCorrentistaObject.get("conta").get("id") as Long

            conta = Conta.get(contaId)

            if (!conta) {
                contaIsValid = false
            }

            contasIds.add(contaId)
        }

        if (!contaIsValid) {
            return 'aplicacaofinanceirarestful.Conta.not.found'
        }

        if (contasIds.unique().size() != 1) {
            return 'aplicacaofinanceirarestful.Conta.not.unique'
        }

        return conta
    }

    def validateTitularidade(jsonCorrentistasArray) {
        def titulares = []
        def naoTitulares = []

        jsonCorrentistasArray.each { jsonCorrentistaObject ->
            Boolean titularidade = jsonCorrentistaObject.getBoolean("titularidade") as Boolean

            if (titularidade == true) {
                titulares.add(titularidade)
            } else if (titularidade == false) {
                naoTitulares.add(titularidade)
            }
        }

        if (titulares.size() > 1) {
            return 'aplicacaofinanceirarestful.Correntista.duplicated.titularidade'
        }

        if (jsonCorrentistasArray.size() == naoTitulares.size()) {
            return 'aplicacaofinanceirarestful.Correntista.titularidade.not.provided'
        }

        return null
    }
}
