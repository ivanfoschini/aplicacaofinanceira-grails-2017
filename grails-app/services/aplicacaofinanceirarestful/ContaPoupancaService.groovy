package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class ContaPoupancaService {

    def findAllOrderByNumero() {
        return ContaPoupanca.findAll("from ContaPoupanca as contaPoupanca order by contaPoupanca.numero")
    }

    def findById(Long id) {
        return ContaPoupanca.get(id)
    }

    def verifyDeletion(ContaPoupanca contaPoupanca) {
        if (!contaPoupanca.correntistas?.isEmpty()) {
            return false
        }

        return true
    }
}