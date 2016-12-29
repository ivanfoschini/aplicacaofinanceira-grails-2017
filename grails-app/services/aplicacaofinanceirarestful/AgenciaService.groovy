package aplicacaofinanceirarestful

import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.validation.Errors
import org.springframework.validation.ObjectError

@Transactional
class AgenciaService {

    def findAllOrderByNome() {
        return Agencia.findAll("from Agencia as agencia order by agencia.nome")
    }

    def findById(Long id) {
        return Agencia.get(id)
    }

    def validate(agencia, messageSource, request, response) {
        JSONArray errorsJSONArray = new JSONArray()

        if (!agencia.validate()) {
            Errors errorsObject = (Errors) agencia.errors
            def allErrors = errorsObject.allErrors

            allErrors.each { ObjectError error ->
                JSONObject errorJSONObject = new JSONObject()
                errorJSONObject.put('message', messageSource.getMessage(error, null))
                errorsJSONArray.put(errorJSONObject)
            }
        }

        if (!agencia.endereco.validate()) {
            Errors errorsObject = (Errors) agencia.endereco.errors
            def allErrors = errorsObject.allErrors

            allErrors.each { ObjectError error ->
                if (!error.field.toString().startsWith('agencia')) {
                    JSONObject errorJSONObject = new JSONObject()
                    errorJSONObject.put('message', messageSource.getMessage(error, null))
                    errorsJSONArray.put(errorJSONObject)
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

    def validateAgencia(jsonObject) {
        JSONObject jsonAgenciaObject = jsonObject.get("agencia")

        Agencia agencia = Agencia.get(jsonAgenciaObject.get("id"))

        if (!agencia) {
            return false
        }

        return true
    }
}