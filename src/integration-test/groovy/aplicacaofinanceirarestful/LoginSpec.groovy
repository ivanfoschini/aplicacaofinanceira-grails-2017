package aplicacaofinanceirarestful

import grails.plugins.rest.client.RestBuilder
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import spock.lang.Specification

@Integration
@Rollback
class LoginSpec extends Specification {

    @Value('${local.server.port}')
    Integer serverPort

    String baseUrl
	RestBuilder restBuilder
    MessageSource messageSource

    def setup() {
        baseUrl = "http://localhost:$serverPort"
    	restBuilder = new RestBuilder()
    }

    def cleanup() {} 

    void "Login com sucesso"() {
        when:                            
        def resposta = restBuilder.post("${baseUrl}/login/login") {
            contentType('application/json')
            json '{ nomeDeUsuario: "admin", senha: "admin" }'
        }

        then:
        resposta.status == HttpStatus.OK.value()
        resposta.json.token != null
    }

    void "Login com nome de usuario incorreto"() {
        when:
        def resposta = restBuilder.post("${baseUrl}/login/login") {
            contentType('application/json')
            json '{ nomeDeUsuario: "admin2", senha: "admin" }'
        }

        then:
        resposta.status == HttpStatus.NOT_FOUND.value()
        resposta.json.message == messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.found', null, null)
    }

    void "Login com senha incorreta"() {
       when:
       def resposta = restBuilder.post("${baseUrl}/login/login") {
            contentType('application/json')
           json '{ nomeDeUsuario: "admin", senha: "admin2" }'
        }

        then:
        resposta.status == HttpStatus.NOT_FOUND.value()
        resposta.json.message == messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.found', null, null)
    }

    void "Login sem nome de usuario e sem senha"() {
        when:
        def resposta = restBuilder.post("${baseUrl}/login/login") {
           contentType('application/json')
            json '{ nomeDeUsuario: null, senha: null }'
        }

        then:
        resposta.status == HttpStatus.NOT_FOUND.value()
        resposta.json.message == messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.found', null, null)
    }
}