package aplicacaofinanceirarestful

import aplicacaofinanceirarestful.utils.SQLUtil
import grails.plugins.rest.client.RestBuilder
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import spock.lang.Specification

@Integration
@Rollback
class BancoShowSpec extends Specification {

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

    void "Show com usuario nao autorizado"() {
        given:
        SQLUtil.instance.executeQuery("INSERT INTO usuario(id, version, nome_de_usuario, senha, token, ultimo_acesso) VALUES (nextval('usuario_seq'), 0, 'teste', '698dc19d489c4e4db73e28a713eab07b', null, null);")
        Usuario usuario = Usuario.findByNomeDeUsuario('teste')
        
        when:                            
        def resposta = restBuilder.get("${baseUrl}/banco")	 {
            contentType('application/json')
            header('nomeDeUsuario', 'teste')
            header('token', usuario.token)
        }

        then:
        resposta.status == HttpStatus.UNAUTHORIZED.value()
		
		cleanup:	
        SQLUtil.instance.executeQuery("DELETE FROM usuario WHERE id = " + usuario.id)
    }

    void "Show com usuario autorizado mas com token invalido"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.get("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', 'tokenInvalido')
        }

        then:
        resposta.status == HttpStatus.UNAUTHORIZED.value()
    }

    void "Show com usuario cujo papel eh admin"() {
        given:
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        Banco banco = Banco.findByNome('Banco do Brasil')
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.get("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
        }

        then:
        resposta.status == HttpStatus.OK.value()
        resposta.json.id == banco.id
		
		cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Show com usuario cujo papel eh funcionario"() {
        given:
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        Banco banco = Banco.findByNome('Banco do Brasil')
        Usuario usuario = Usuario.findByNomeDeUsuario('funcionario')
        
        when:                            
        def resposta = restBuilder.get("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'funcionario')
            header('token', usuario.token)
        }

        then:
        resposta.status == HttpStatus.OK.value()
        resposta.json.id == banco.id
		
		cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Show com banco inexistente"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.get("${baseUrl}/banco/0") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
        }

        then:
        resposta.status == HttpStatus.NOT_FOUND.value()
        resposta.json.message == messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null)
    }
}