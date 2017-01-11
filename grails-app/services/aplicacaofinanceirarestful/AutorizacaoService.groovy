package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class AutorizacaoService {

    def autorizar(request) {
    	def autorizado = false
        
    	Usuario usuario = Usuario.findByNomeDeUsuarioAndToken(request.getHeader("nomeDeUsuario"), request.getHeader("token"))       
    	Servico servico = Servico.findByRequestUriAndRequestMethod(request.requestURI, request.method)

        if (usuario && servico) {
        	usuario.papeis.each { Papel papelDoUsuario ->
        		servico.papeis.each { Papel papelDoServico ->
        			if (papelDoUsuario.nome.equals(papelDoServico.nome)) {
        				usuario.ultimoAcesso = new Date()
        				autorizado = true
	        		}
        		}        		
        	}
    	} 

    	return autorizado
    }
}