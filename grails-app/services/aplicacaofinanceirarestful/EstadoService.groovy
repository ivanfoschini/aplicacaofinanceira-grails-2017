package aplicacaofinanceirarestful

import grails.transaction.Transactional
import org.grails.web.json.JSONObject

@Transactional
class EstadoService {

    def findAllOrderByNome() {
        return Estado.findAll("from Estado as estado order by estado.nome")
    }

    def findById(Long id) {
        return Estado.get(id)
    }

    def validateEstado(JSONObject jsonObject) {
        JSONObject jsonEstadoObject = jsonObject.get("estado")

        Estado estado = Estado.get(jsonEstadoObject.get("id"))

        if (!estado) {
            return false
        }

        return true
    }

    def verifyCidadeIsUnique(JSONObject jsonObject) {
        Estado estado = Estado.get(jsonObject.get("estado").get("id"))

        def queryResult = Cidade.executeQuery("from Cidade as cidade join cidade.estado as estado where cidade.nome = '" + jsonObject.get("nome") + "' and estado.id = " + estado.id)

        if (!queryResult.isEmpty()) {
            return false
        }

        return true
    }

    def verifyDeletion(Estado estado) {
        if (!estado.cidades?.isEmpty()) {
            return false
        }

        return true
    }
}