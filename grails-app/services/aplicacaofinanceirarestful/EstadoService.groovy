package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class EstadoService {

    def findAllOrderByNome() {
        return Estado.findAll("from Estado as estado order by estado.nome")
    }

    def findById(Long id) {
        return Estado.get(id)
    }
}