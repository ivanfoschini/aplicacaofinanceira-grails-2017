package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class EstadoController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AutorizacaoService autorizacaoService
    EstadoService estadoService
    MessageSource messageSource

     /**
     * @api {delete} /estado/:id Delete
     * @apiGroup Estado
     * @apiVersion 1.0.0
     *
     * @apiHeader {String} nomeDeUsuario Nome do usuário que está realizando a requisição.
     * @apiHeader {String} token         Token de acesso.
     *
     * @apiHeaderExample {String} Exemplo de cabeçalho:  
     *     "nomeDeUsuario": admin
     *     "token":         PXO9PmSpQwukqfjqg5ukbG3O1Hgl1yH0
     *
     * @apiParam {Number} id Identificador do estado.
     *
     * @apiSuccessExample Resposta de sucesso
     *     HTTP/1.1 204 No Content
     *
     * @apiError Unauthorized Usuário não autorizado a acessar o recurso solicitado.
     *
     * @apiErrorExample Unauthorized
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "timestamp": 1484224334405,
     *       "status": 401,
     *       "error": "Unauthorized",
     *       "message": "Usuário não autorizado a acessar o recurso solicitado.",
     *       "path": "/estado/1"
     *     }
     *
     * @apiError NotFound Não existe um estado que possua o identificador passado como parâmetro.
     *
     * @apiErrorExample NotFound
     *     {
     *       "timestamp": 1484224424107,
     *       "status": 404,
     *       "error": "Not Found",
     *       "message": "Estado não encontrado.",
     *       "path": "/estado/0"
     *     }       
     *
     * @apiError Conflict O estado cujo identificador foi passado como parâmetro não pode ser excluído.
     *
     * @apiErrorExample Conflict
     *     HTTP/1.1 409 Conflict
     *     {
     *       "timestamp": 1484224497234,
     *       "status": 409,
     *       "error": "Conflict",
     *       "message": "O estado selecionado possui pelo menos uma cidade e, portanto, não pode ser excluído.",
     *       "path": "/estado/1"
     *     }
     */
    def delete() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Estado estado = estadoService.findById(params.id as Long)

            if (!estado) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null))
                return
            }

            if (!estadoService.verifyDeletion(estado)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Estado.has.cidades', null, null), status: HttpStatus.CONFLICT
                return
            }

            estado.delete(flush: true)
            render status: HttpStatus.NO_CONTENT
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    /**
     * @api {get} /estado List
     * @apiGroup Estado
     * @apiVersion 1.0.0
     *
     * @apiHeader {String} nomeDeUsuario Nome do usuário que está realizando a requisição.
     * @apiHeader {String} token         Token de acesso.
     *
     * @apiHeaderExample {String} Exemplo de cabeçalho:  
     *     "nomeDeUsuario": admin
     *     "token":         PXO9PmSpQwukqfjqg5ukbG3O1Hgl1yH0
     *
     * @apiSuccess {Number} id        Identificador do estado.
     * @apiSuccess {String} nome      Nome do estado.
     *
     * @apiSuccessExample Resposta de sucesso
     *     HTTP/1.1 200 OK
     *     [
     *       {
     *         "id": 3,
     *         "nome": "Espirito Santo"
     *       },
     *       {
     *         "id": 2,
     *         "nome": "Rio de Janeiro"
     *       },
     *       {
     *         "id": 1,
     *         "nome": "Sao Paulo"
     *       }
     *     ] 
     *
     * @apiError Unauthorized Usuário não autorizado a acessar o recurso solicitado.
     *
     * @apiErrorExample Unauthorized
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "timestamp": 1484232706578,
     *       "status": 401,
     *       "error": "Unauthorized",
     *       "message": "Usuário não autorizado a acessar o recurso solicitado.",
     *       "path": "/estado"
     *     }
     *
     */
    def index() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            respond estadoService.findAllOrderByNome()
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }    
    }

     /**
     * @api {post} /estado Save
     * @apiGroup Estado
     * @apiVersion 1.0.0
     *
     * @apiHeader {String} Content-Type  Tipo do conteúdo da requisição.
     * @apiHeader {String} nomeDeUsuario Nome do usuário que está realizando a requisição.
     * @apiHeader {String} token         Token de acesso.
     *
     * @apiHeaderExample {String} Exemplo de cabeçalho:  
     *     "Content-Type":  application/json
     *     "nomeDeUsuario": admin
     *     "token":         PXO9PmSpQwukqfjqg5ukbG3O1Hgl1yH0
     *
     * @apiExample {json} Exemplo de requisição
     *     {
     *       "nome": "Sao Paulo"
     *     }
     *     
     * @apiSuccess {Number} id      Identificador do estado.
     * @apiSuccess {String} nome    Nome do estado.
     *
     * @apiSuccessExample Resposta de sucesso
     *     HTTP/1.1 201 Created
     *     {
     *       "id": 1,
     *       "nome": "Sao Paulo"
     *     } 
     *
     * @apiError Unauthorized Usuário não autorizado a acessar o recurso solicitado.
     *
     * @apiErrorExample Unauthorized
     *     HTTP/1.1 401 Unauthorized
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "timestamp": 1484232706578,
     *       "status": 401,
     *       "error": "Unauthorized",
     *       "message": "Usuário não autorizado a acessar o recurso solicitado.",
     *       "path": "/estado"
     *     }
     *
     * @apiError UnprocessableEntity Erros de validação de dados ou violação de regras de negócio.
     * 
     * @apiErrorExample UnprocessableEntity
     *     HTTP/1.1 422 UnprocessableEntity - "Campos obrigatórios não foram fornecidos."
     *     
     *     {
     *       "timestamp": 1484224887178,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O nome fornecido não pode ser nulo."
     *         }
     *       ],
     *       "path": "/estado"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O nome fornecido já pertence a outro estado."
     *
     *     {
     *       "timestamp": 1484232914607,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O nome fornecido já pertence a outro estado."
     *         }
     *       ],
     *       "path": "/estado"
     *     }
     */
    def save() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            JsonSlurper jsonSlurper = new JsonSlurper()
            Estado estado = new Estado(jsonSlurper.parseText(request.JSON.toString()))

            estado.save(flush: true)
            respond estado, [status: HttpStatus.CREATED, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    /**
     * @api {get} /estado/:id Show
     * @apiGroup Estado
     * @apiVersion 1.0.0
     *
     * @apiHeader {String} nomeDeUsuario Nome do usuário que está realizando a requisição.
     * @apiHeader {String} token         Token de acesso.
     *
     * @apiHeaderExample {String} Exemplo de cabeçalho:  
     *     "nomeDeUsuario": admin
     *     "token":         PXO9PmSpQwukqfjqg5ukbG3O1Hgl1yH0
     *
     * @apiParam {Number} id Identificador do estado.
     *
     * @apiSuccess {Number} id        Identificador do estado.
     * @apiSuccess {String} nome      Nome do estado.
     *
     * @apiSuccessExample Resposta de sucesso
     *     HTTP/1.1 200 OK
     *     {
     *       "id": 1,
     *       "nome": "Sao Paulo"
     *     } 
     *
     * @apiError Unauthorized Usuário não autorizado a acessar o recurso solicitado.
     *
     * @apiErrorExample Unauthorized
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "timestamp": 1484225396181,
     *       "status": 401,
     *       "error": "Unauthorized",
     *       "message": "Usuário não autorizado a acessar o recurso solicitado.",
     *       "path": "/estado/1"
     *     }
     *
     * @apiError NotFound Não existe um estado que possua o identificador passado como parâmetro.
     *
     * @apiErrorExample NotFound
     *     HTTP/1.1 404 Not Found
     *     {
     *       "timestamp": 1484225446800,
     *       "status": 404,
     *       "error": "Not Found",
     *       "message": "Estado não encontrado.",
     *       "path": "/estado/0"
     *     }        
     */
    def show() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Estado estado = estadoService.findById(params.id as Long)

            if (estado) {
                respond estado
            } else {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null))
            }
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    /**
     * @api {put} /estado/:id Update
     * @apiGroup Estado
     * @apiVersion 1.0.0
     *
     * @apiHeader {String} Content-Type  Tipo do conteúdo da requisição.
     * @apiHeader {String} nomeDeUsuario Nome do usuário que está realizando a requisição.
     * @apiHeader {String} token         Token de acesso.
     *
     * @apiHeaderExample {String} Exemplo de cabeçalho:  
     *     "Content-Type":  application/json
     *     "nomeDeUsuario": admin
     *     "token":         PXO9PmSpQwukqfjqg5ukbG3O1Hgl1yH0
     *
     * @apiParam {Number} id Identificador do estado.
     *
     * @apiParamExample {json} Exemplo de requisição
     *     {
     *       "nome": "Sao Paulo"   
     *     }
     *
     * @apiSuccess {Number} id      Identificador do estado.
     * @apiSuccess {String} nome    Nome do estado.
     *
     * @apiSuccessExample Resposta de sucesso
     *     HTTP/1.1 200 OK
     *     {
     *       "id": 1,
     *       "nome": "Sao Paulo"
     *     } 
     *
     * @apiError Unauthorized Usuário não autorizado a acessar o recurso solicitado.
     *
     * @apiErrorExample Unauthorized
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "timestamp": 1484224814226,
     *       "status": 401,
     *       "error": "Unauthorized",
     *       "message": "Usuário não autorizado a acessar o recurso solicitado.",
     *       "path": "/estado/1"
     *     }
     *
     * @apiError NotFound Não existe um estado que possua o identificador passado como parâmetro.
     *
     * @apiErrorExample NotFound
     *     HTTP/1.1 404 Not Found
     *     {
     *       "timestamp": 1484233284661,
     *       "status": 404,
     *       "error": "Not Found",
     *       "message": "Estado não encontrado.",
     *       "path": "/estado/0"
     *     }
     *
     * @apiError UnprocessableEntity Erros de validação de dados ou violação de regras de negócio.
     * 
     * @apiErrorExample UnprocessableEntity
     *     HTTP/1.1 422 UnprocessableEntity - "Campos obrigatórios não foram fornecidos."
     *     
     *     {
     *       "timestamp": 1484224887178,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O nome fornecido não pode ser nulo."
     *         }
     *       ],
     *       "path": "/estado/1"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O nome fornecido já pertence a outro estado."
     *
     *     {
     *       "timestamp": 1484225086607,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O nome fornecido já pertence a outro estado."
     *         }
     *       ],
     *       "path": "/estado/1"
     *     }
     */
    def update() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Estado estado = estadoService.findById(params.id as Long)

            if (!estado) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Estado.not.found', null, null))
                return
            }

            JsonSlurper jsonSlurper = new JsonSlurper()
            estado.properties = jsonSlurper.parseText(request.JSON.toString())

            estado.save(flush: true)
            respond estado, [status: HttpStatus.OK, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }
}