package aplicacaofinanceirarestful

import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Transactional
class CidadeService {

    def findAllOrderByNome() {
        return Cidade.findAll("from Cidade as cidade order by cidade.nome")
    }

    def findById(Long id) {
        return Cidade.get(id)
    }

    def validateCidadeForAgencia(jsonObject) {
        JSONObject jsonCidadeObject = jsonObject.get("endereco").get("cidade")

        Cidade cidade = Cidade.get(jsonCidadeObject.get("id"))

        if (!cidade) {
            return false
        }

        return true
    }

    def validateCidadeForCliente(jsonObject) {
        def isValid = true

        JSONArray jsonEnderecosArray = jsonObject.get("enderecos")

        jsonEnderecosArray.each { jsonEnderecoObject ->
            JSONObject jsonCidadeObject = jsonEnderecoObject.get("cidade")

            Cidade cidade = Cidade.get(jsonCidadeObject.get("id"))

            if (!cidade) {
                isValid = false
            }
        }

        return isValid
    }

    def verifyDeletion(Cidade cidade) {
        if (!cidade.enderecos?.isEmpty()) {
            return false
        }

        return true
    }
}