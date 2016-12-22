package aplicacaofinanceirarestful

import grails.transaction.Transactional
import org.grails.web.json.JSONObject

@Transactional
class CidadeService {

    def findAllOrderByNome() {
        return Cidade.findAll("from Cidade as cidade order by cidade.nome")
    }

    def findById(Long id) {
        return Cidade.get(id)
    }

    def validateCidade(jsonObject) {
        JSONObject jsonCidadeObject = jsonObject.get("endereco").get("cidade")

        Cidade cidade = Cidade.get(jsonCidadeObject.get("id"))

        if (!cidade) {
            return false
        }

        return true
    }
}