package aplicacaofinanceirarestful

import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class LoginController {
	
	static allowedMethods = [login: "POST"]

    AutenticacaoService autenticacaoService
    LoginService loginService
	MessageSource messageSource

    def login() {
    	def jsonObject = request.JSON

        Usuario usuario = autenticacaoService.findByNomeDeUsuarioAndSenha(jsonObject.nomeDeUsuario, jsonObject.senha)

    	if (usuario) {
    		def token = loginService.generateRandomToken()

    		usuario.token = token
    		usuario.ultimoAcesso = new Date()
    		usuario.save()

            respond loginService.loginResponse(token), status: HttpStatus.OK
    	} else {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.found', null, null))
    	}
    }
}