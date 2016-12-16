package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class BancoService {

    def findAllOrderByNome() {
        return Banco.findAll("from Banco as ban order by ban.nome")
    }

    def findById(Long id) {
        return Banco.get(id)
    }
}