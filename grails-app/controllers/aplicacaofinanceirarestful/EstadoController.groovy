package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class EstadoController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    EstadoService estadoService
    MessageSource messageSource

    def delete() {
        Estado estado = estadoService.findById(params.id as Long)

        if (!estado) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null))
            return
        }

        estado.delete(flush: true)
        render status: HttpStatus.NO_CONTENT
    }

    def index() {
        respond estadoService.findAllOrderByNome()
    }

    def save() {
        JsonSlurper jsonSlurper = new JsonSlurper()
        Estado estado = new Estado(jsonSlurper.parseText(request.JSON.toString()))

        estado.save(flush: true)
        respond estado, [status: HttpStatus.CREATED, view: 'show']
    }

    def show() {
        Estado estado = estadoService.findById(params.id as Long)

        if (estado) {
            respond estado
        } else {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null))
        }
    }

    def update() {
        Estado estado = estadoService.findById(params.id as Long)

        if (!estado) {
            render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null))
            return
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        estado.properties = jsonSlurper.parseText(request.JSON.toString())

        estado.save(flush: true)
        respond estado, [status: HttpStatus.OK, view: 'show']
    }
}