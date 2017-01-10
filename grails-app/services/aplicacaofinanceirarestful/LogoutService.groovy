package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class LogoutService {

    def logoutResponse(nomeDeUsuario) {
        def responseBody = [:]
        responseBody.nomeDeUsuario = nomeDeUsuario

        return responseBody
    }
}