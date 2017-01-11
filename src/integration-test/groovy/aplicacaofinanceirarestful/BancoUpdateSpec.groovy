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

    /*void "Update sem campos obrigatorios"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')
        
        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: null, cnpj: null, nome: null }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        resposta.json.toString() == '{"errors":["' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.numero.nullable', null) + '","' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.cnpj.nullable', null) + '","' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.nome.nullable', null) + '"]}'

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    } 

    void "Update com numero menor do que um"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')
        
        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 0, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        resposta.json.toString() == '{"errors":["' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.numero.min.error', null) + '"]}'

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Update com numero duplicado"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00360305000104', 'Caixa Economica Federal', 2);")

        Banco banco = Banco.findByNome('Caixa Economica Federal')

        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "00000000000191", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        resposta.json.toString() == '{"errors":["' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.numero.unique', null) + '"]}'

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Update com CNPJ com menos do que catorze caracteres"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')
        
        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "0000000000019", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        resposta.json.toString() == '{"errors":["' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.cnpj.minSize.error', null) + '","' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.cnpj.invalid', null) + '"]}'

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Update com CNPJ com mais do que catorze caracteres"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')
        
        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "000000000001910", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        resposta.json.toString() == '{"errors":["' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.cnpj.maxSize.error', null) + '","' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.cnpj.invalid', null) + '"]}'

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }

    void "Update com CNPJ invalido"() {
        given:
        Usuario usuario = Usuario.findByNomeDeUsuario('admin')
        SQLUtil.instance.executeQuery("INSERT INTO banco(id, version, cnpj, nome, numero) VALUES (nextval('banco_seq'), 0, '00000000000191', 'Banco do Brasil', 1);")
        
        Banco banco = Banco.findByNome('Banco do Brasil')
        
        when:                            
        def resposta = restBuilder.put("${baseUrl}/banco/" + banco.id) {
            contentType('application/json')
            header('nomeDeUsuario', 'admin')
            header('token', usuario.token)
            json '{ numero: 1, cnpj: "11111111111111", nome: "Banco do Brasil" }'
        }

        then:
        resposta.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        resposta.json.toString() == '{"errors":["' + MensagemUtil.instance.recuperarMensagem('aplicacaofinanceirarestful.Banco.cnpj.invalid', null) + '"]}'

        cleanup:
        SQLUtil.instance.executeQuery("DELETE FROM banco;")
    }*/
}