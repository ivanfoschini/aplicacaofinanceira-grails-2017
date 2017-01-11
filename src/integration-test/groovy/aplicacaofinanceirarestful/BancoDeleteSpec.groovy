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
class BancoDeleteSpec extends Specification {

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

    void "Delete com usuario nao autorizado"() {
        given:
		SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')        

        Usuario usuario = Usuario.findByNomeDeUsuario('funcionario')
        
        when:                            
        def resposta = restBuilder.delete("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'teste')
            header('token', usuario.token)
        }

        then:
        resposta.status == HttpStatus.UNAUTHORIZED.value()

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Delete com usuario autorizado mas com token invalido"() {
        given:
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')
        
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.delete("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', 'tokenInvalido')
        }

        then:
        resposta.status == HttpStatus.UNAUTHORIZED.value()

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Delete com banco inexistente"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
        when:                            
        def resposta = restBuilder.delete("${baseUrl}/banco/0") {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
        }

        then:
        resposta.status == HttpStatus.NOT_FOUND.value()
        resposta.json.message == messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null)     		
    }

    void "Delete com banco que possui pelo menos uma agencia associada"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        SQLUtil.instance.executeQuery("INSERT INTO estado(id, version, nome) VALUES (nextval('estado_seq'), 0, 'Sao Paulo');")

        Estado estado = Estado.findByNome('Sao Paulo')

		SQLUtil.instance.executeQuery("INSERT INTO cidade(id, version, estado_id, nome) VALUES (nextval('cidade_seq'), 0, " + estado.id + ", 'Sao Carlos');")

		Cidade cidade = Cidade.findByNome('Sao Carlos')

		SQLUtil.instance.executeQuery("INSERT INTO endereco(id, version, bairro, cep, cidade_id, cliente_id, complemento, logradouro, numero) VALUES (nextval('endereco_seq'), 0, 'Bairro Agencia Centro', '11111-111', " + cidade.id + ", null, null, 'Logradouro Agencia Centro', '1');")
		SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")

		Endereco endereco = Endereco.findByNumero('1')
		Banco banco = Banco.findByNome('Banco do Brasil')		

		SQLUtil.instance.executeQuery("INSERT INTO agencia(id, version, banco_id, endereco_id, nome, numero) VALUES (nextval('agencia_seq'), 0, " + banco.id + ", " + endereco.id + ", 'Agencia Centro', 1);")
        
		when:                            
        def resposta = restBuilder.delete("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
        }

        then:
        resposta.status == HttpStatus.CONFLICT.value()
        resposta.json.message == messageSource.getMessage('aplicacaofinanceirarestful.Banco.has.agencias', null, null)

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM agencia;")
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
        SQLUtil.instance.executeQuery("DELETE FROM endereco;")
        SQLUtil.instance.executeQuery("DELETE FROM cidade;")
        SQLUtil.instance.executeQuery("DELETE FROM estado;")
    }

    void "Delete com sucesso"() {
        given:
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
		Banco banco = Banco.findByNome('Banco do Brasil')		
		Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        
		when:                            
        def resposta = restBuilder.delete("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
        }

        then:
        resposta.status == HttpStatus.NO_CONTENT.value()
    }
}