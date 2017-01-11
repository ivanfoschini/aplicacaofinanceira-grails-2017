package aplicacaofinanceirarestful

import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class LogoutController {

	static allowedMethods = [login: "POST"]

	AutenticacaoService autenticacaoService
    AutorizacaoService autorizacaoService
    LogoutService logoutService
	MessageSource messageSource

    def logout() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
        	def jsonObject = request.JSON

            Usuario usuario = autenticacaoService.findByNomeDeUsuario(jsonObject.nomeDeUsuario)

        	if (usuario) {
        		usuario.token = null
        		usuario.ultimoAcesso = null

                respond logoutService.logoutResponse(usuario.nomeDeUsuario), status: HttpStatus.OK
        	} else {
        		render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.found', null, null))
        	}
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }
}