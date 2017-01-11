package aplicacaofinanceirarestful

import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.http.HttpStatus

@Transactional
class AutorizacaoService {

    def autorizar(request, actionUri) {
    	def autorizado = false
        
    	Usuario usuario = Usuario.findByNomeDeUsuarioAndToken(request.getHeader("nomeDeUsuario"), request.getHeader("token"))
		Servico servico = Servico.findByUri(actionUri)

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

    def createNotAuthorizedResponse(request, response, message) {
        int status = HttpStatus.UNAUTHORIZED.value()

        def responseBody = [:]
        responseBody.timestamp = new Date().getTime()
        responseBody.status = status
        responseBody.error = 'Unauthorized'
        responseBody.message = message
        responseBody.path = request.requestURI

        response.setStatus(status)

        return responseBody as JSON
    }
}