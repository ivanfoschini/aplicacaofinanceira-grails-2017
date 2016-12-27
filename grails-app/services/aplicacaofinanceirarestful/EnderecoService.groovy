package aplicacaofinanceirarestful

import grails.transaction.Transactional
import org.grails.web.json.JSONArray

@Transactional
class EnderecoService {

    private validateEnderecos(jsonObject) {
        JSONArray jsonEnderecosArray = jsonObject.get("enderecos")

        if (jsonEnderecosArray.isEmpty()) {
            return false
        }

        return true
    }
}