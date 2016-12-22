package aplicacaofinanceirarestful

import grails.transaction.Transactional
import org.grails.web.json.JSONObject

@Transactional
class BancoService {

    def findAllOrderByNome() {
        return Banco.findAll("from Banco as banco order by banco.nome")
    }

    def findById(Long id) {
        return Banco.get(id)
    }

    def validateBanco(jsonObject) {
        JSONObject jsonBancoObject = jsonObject.get("banco")

        Banco banco = Banco.get(jsonBancoObject.get("id"))

        if (!banco) {
            return false
        }

        return true
    }
}