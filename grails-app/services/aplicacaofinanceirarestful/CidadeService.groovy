package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class CidadeService {

    def findAllOrderByNome() {
        return Cidade.findAll("from Cidade as cidade order by cidade.nome")
    }

    def findById(Long id) {
        return Cidade.get(id)
    }
}