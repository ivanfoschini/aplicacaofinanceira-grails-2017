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
class BancoSaveSpec extends Specification {

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

    void "Save com usuario nao autorizado"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('funcionario')
        
        when:                            
        def resposta = restBuilder.post("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'funcionario')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNAUTHORIZED.value()
    }

    void "Save com usuario autorizado mas com token invalido"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.post("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', 'tokenInvalido')
            json '{ numero: 1, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNAUTHORIZED.value()
    }

    void "Save com sucesso"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.post("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        Banco banco = Banco.findByNome('Banco do Brasil')

        resposta.status == HttpStatus.CREATED.value()
        
        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }
}