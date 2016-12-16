package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.springframework.http.HttpStatus

class BancoController {

    static allowedMethods = [delete: "DELETE", list: "GET", save: "POST", show: "GET", update: "PUT"]

    def bancoService

    def delete() {
        Banco banco = bancoService.findById(params.id as Long)

        if (!banco) {
            render status: HttpStatus.NOT_FOUND
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

        banco.validate()

        banco.save(flush: true)
        respond banco, [status: HttpStatus.CREATED, view:'show']
    }

    def show() {
        Banco banco = bancoService.findById(params.id as Long)

        if (banco) {
            respond banco
        } else {
            render status: HttpStatus.NOT_FOUND
        }
    }

    def update() {
        def banco = bancoService.findById(params.id as Long)

        if (!banco) {
            render status: HttpStatus.NOT_FOUND
        }

        JsonSlurper jsonSlurper = new JsonSlurper()
        banco.properties = jsonSlurper.parseText(request.JSON.toString())

        banco.validate()

        banco.save(flush: true)
        respond banco, [status: HttpStatus.OK, view:'show']
    }
}