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
class BancoUpdateSpec extends Specification {

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

    void "Update com usuario nao autorizado"() {
    	given:
    	SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')
        
        Usuario usuario = Usuario.findByNomeDeUsuario('funcionario')
        
        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'funcionario')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNAUTHORIZED.value()

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Update com usuario autorizado mas com token invalido"() {
    	given:
    	SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')
        
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', 'tokenInvalido')
            json '{ numero: 1, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNAUTHORIZED.value()

		cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")        
    }

    void "Update com banco inexistente"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/0") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.NOT_FOUND.value()
        resposta.json.message == messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null)		
    }

    void "Update com sucesso"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')

        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 2, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.OK.value()

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }
}