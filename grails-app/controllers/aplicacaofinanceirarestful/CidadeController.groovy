package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class CidadeController {
    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    CidadeService cidadeService
    EstadoService estadoService
    MessageSource messageSource

    def delete() {
        Cidade cidade = cidadeService.findById(params.id as Long)

        if (!cidade) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null))
            return
        }

        if (!cidadeService.verifyDeletion(cidade)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.has.enderecos', null, null), status: HttpStatus.CONFLICT
            return
        }

        cidade.delete(flush: true)
        render status: HttpStatus.NO_CONTENT
    }

    def index() {
        respond cidadeService.findAllOrderByNome()
    }

    def save() {
        JSONObject jsonObject = request.JSON

        if (!estadoService.validateEstado(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        if (!estadoService.verifyCidadeIsUnique(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.unique.for.Estado', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        Cidade cidade = new Cidade(jsonSlurper.parseText(jsonObject.toString()))

        cidade.save(flush: true)
        respond cidade, [status: HttpStatus.CREATED, view: 'show']
    }

    def show() {
        Cidade cidade = cidadeService.findById(params.id as Long)

        if (cidade) {
            respond cidade
        } else {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null))
        }
    }

    def update() {
        Cidade cidade = cidadeService.findById(params.id as Long)

        if (!cidade) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.found', null, null))
            return
        }

        JSONObject jsonObject = request.JSON

        if (!estadoService.validateEstado(jsonObject)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        if (!estadoService.verifyCidadeIsUnique(jsonObject, cidade.id)) {
            render message: messageSource.getMessage('aplicacaofinanceirarestful.Cidade.not.unique.for.Estado', null, null), status: HttpStatus.UNPROCESSABLE_ENTITY
            return
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        cidade.properties = jsonSlurper.parseText(request.JSON.toString())

        cidade.save(flush: true)
        respond cidade, [status: HttpStatus.OK, view: 'show']
    }
}