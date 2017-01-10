package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class AutenticacaoService {

    def findByNomeDeUsuario(nomeDeUsuario) {
    	return Usuario.findByNomeDeUsuario(nomeDeUsuario)
    }

    def findByNomeDeUsuarioAndSenha(nomeDeUsuario, senha) {
    	return Usuario.findByNomeDeUsuarioAndSenha(nomeDeUsuario, senha.encodeAsMD5())
    }
}