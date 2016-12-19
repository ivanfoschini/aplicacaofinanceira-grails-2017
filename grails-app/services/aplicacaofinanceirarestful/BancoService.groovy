package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class BancoService {

    def findAllOrderByNome() {
        return Banco.findAll("from Banco as banco order by banco.nome")
    }

    def findById(Long id) {
        return Banco.get(id)
    }
}