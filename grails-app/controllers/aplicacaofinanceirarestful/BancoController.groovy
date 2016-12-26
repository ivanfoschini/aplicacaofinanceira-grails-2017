package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.springframework.http.HttpStatus

class BancoController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    def bancoService
    def messageSource

    def delete() {
        Banco banco = bancoService.findById(params.id as Long)

        if (!banco) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null))
        }

        if (!bancoService.verifyDeletion(banco)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Banco.has.agencias', null, null), status: HttpStatus.CONFLICT
            return
        }

        banco.delete(flush: true)
        render status: HttpStatus.NO_CONTENT
    }

    def index() {
        respond bancoService.findAllOrderByNome()
    }

    def save() {
        JsonSlurper jsonSlurper = new JsonSlurper()
        Banco banco = new Banco(jsonSlurper.parseText(request.JSON.toString()))

        banco.save(flush: true)
        respond banco, [status: HttpStatus.CREATED, view: 'show']
    }

    def show() {
        Banco banco = bancoService.findById(params.id as Long)

        if (banco) {
            respond banco
        } else {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null))
        }
    }

    def update() {
        Banco banco = bancoService.findById(params.id as Long)

        if (!banco) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null))
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        banco.properties = jsonSlurper.parseText(request.JSON.toString())

        banco.save(flush: true)
        respond banco, [status: HttpStatus.OK, view: 'show']
    }
}