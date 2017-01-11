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

    /*void "Save sem campos obrigatorios"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.post("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: null, cnpj: null, nome: null }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        //resposta.json.toString() == '{"errors":["' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.numero.nullable', null, null) + '","' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.cnpj.nullable', null, null, null) + '","' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.nome.nullable', null, null, null) + '"]}'
    } 

    void "Save com numero menor do que um"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.post("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 0, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        //resposta.json.toString() == '{"errors":["' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.numero.min.error', null, null) + '"]}'
    }

    void "Save com numero duplicado"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")

        when:                            
        def resposta = restBuilder.post("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        println resposta.json
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        //resposta.json.toString() == '{"errors":["' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.numero.unique', null, null) + '"]}'

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Save com CNPJ com menos do que catorze caracteres"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.post("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "0000000000019", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        //resposta.json.toString() == '{"errors":["' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.cnpj.minSize.error', null, null) + '","' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.cnpj.invalid', null, null) + '"]}'
    }

    void "Save com CNPJ com mais do que catorze caracteres"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.post("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "000000000001910", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        //resposta.json.toString() == '{"errors":["' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.cnpj.maxSize.error', null, null) + '","' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.cnpj.invalid', null, null) + '"]}'
    }

    void "Save com CNPJ invalido"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.post("${baseUrl}/banco") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "11111111111111", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        //resposta.json.toString() == '{"errors":["' + messageSource.getMessage('aplicacaofinanceirarestful.Banco.cnpj.invalid', null, null) + '"]}'
    }*/
}