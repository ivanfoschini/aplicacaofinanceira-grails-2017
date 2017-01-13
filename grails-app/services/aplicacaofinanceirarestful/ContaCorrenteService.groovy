package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class ContaCorrenteService {

    def findAllOrderByNumero() {
        return ContaCorrente.findAll("from ContaCorrente as contaCorrente order by contaCorrente.numero")
    }

    def findById(Long id) {
        return ContaCorrente.get(id)
    }

    def verifyDeletion(ContaCorrente contaCorrente) {
        if (!contaCorrente.correntistas?.isEmpty()) {
            return false
        }

        return true
    }
}