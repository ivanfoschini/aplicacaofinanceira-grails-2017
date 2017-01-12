package aplicacaofinanceirarestful

import groovy.json.JsonSlurper
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class BancoController {

    static allowedMethods = [delete: "DELETE", index: "GET", save: "POST", show: "GET", update: "PUT"]

    AutorizacaoService autorizacaoService
    BancoService bancoService
    MessageSource messageSource

    /**
     * @api {delete} /banco/:id Delete
     * @apiGroup Banco
     * @apiVersion 1.0.0
     *
     * @apiHeader {String} nomeDeUsuario Nome do usuário que está realizando a requisição.
     * @apiHeader {String} token         Token de acesso.
     *
     * @apiHeaderExample {String} Exemplo de cabeçalho:  
     *     "nomeDeUsuario": admin
     *     "token":         PXO9PmSpQwukqfjqg5ukbG3O1Hgl1yH0
     *
     * @apiParam {Number} id Identificador do banco.
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
     *       "path": "/banco/1"
     *     }
     *
     * @apiError NotFound Não existe um banco que possua o identificador passado como parâmetro.
     *
     * @apiErrorExample NotFound
     *     HTTP/1.1 404 Not Found
     *     {
     *       "timestamp": 1484224424107,
     *       "status": 404,
     *       "error": "Not Found",
     *       "message": "Banco não encontrado.",
     *       "path": "/banco/0"
     *     }       
     *
     * @apiError Conflict O banco cujo identificador foi passado como parâmetro não pode ser excluído.
     *
     * @apiErrorExample Conflict
     *     HTTP/1.1 409 Conflict
     *     {
     *       "timestamp": 1484224497234,
     *       "status": 409,
     *       "error": "Conflict",
     *       "message": "O banco selecionado possui agências e, portanto, não pode ser excluído.",
     *       "path": "/banco/1"
     *     }
     */
    def delete() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Banco banco = bancoService.findById(params.id as Long)

            if (!banco) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null))
                return
            }

            if (!bancoService.verifyDeletion(banco)) {
                render message: messageSource.getMessage('aplicacaofinanceirarestful.Banco.has.agencias', null, null), status: HttpStatus.CONFLICT
                return
            }

            banco.delete(flush: true)
            render status: HttpStatus.NO_CONTENT
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    /**
     * @api {get} /banco List
     * @apiGroup Banco
     * @apiVersion 1.0.0
     *
     * @apiHeader {String} nomeDeUsuario Nome do usuário que está realizando a requisição.
     * @apiHeader {String} token         Token de acesso.
     *
     * @apiHeaderExample {String} Exemplo de cabeçalho:  
     *     "nomeDeUsuario": admin
     *     "token":         PXO9PmSpQwukqfjqg5ukbG3O1Hgl1yH0
     *
     * @apiSuccess {Number} id        Identificador do banco.
     * @apiSuccess {Number} numero    Numero do banco.
     * @apiSuccess {String} cnpj      CNPJ do banco.
     * @apiSuccess {String} nome      Nome do banco.
     *
     * @apiSuccessExample Resposta de sucesso
     *     HTTP/1.1 200 OK
     *     [
     *       {
     *         "id": 1,
     *         "numero": 1,
     *         "cnpj": "00000000000191",
     *         "nome": "Banco do Brasil"
     *       },
     *       {
     *         "id": 2,
     *         "numero": 2,
     *         "cnpj": "00360305000104",
     *         "nome": "Caixa Econômica Federal"
     *       },
     *       {
     *         "id": 3,
     *         "numero": 3,
     *         "cnpj": "60872504000123",
     *         "nome": "Itaú"
     *       }
     *     ] 
     *
     * @apiError Unauthorized Usuário não autorizado a acessar o recurso solicitado.
     *
     * @apiErrorExample Unauthorized
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Usuário não autorizado a acessar o recurso solicitado."
     *     }
     *
     */
    def index() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            respond bancoService.findAllOrderByNome()
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        }     
    }

    /**
     * @api {post} /banco Save
     * @apiGroup Banco
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
     *       "numero": 1, 
     *       "cnpj": "00000000000191",   
     *       "nome": "Banco do Brasil"   
     *     }
     *
     * @apiSuccess {Number} id      Identificador do banco.
     * @apiSuccess {Number} numero  Numero do banco.
     * @apiSuccess {String} cnpj    CNPJ do banco.
     * @apiSuccess {String} nome    Nome do banco.
     *
     * @apiSuccessExample Resposta de sucesso
     *     HTTP/1.1 201 Created
     *     {
     *       "id": 1,
     *       "numero": 1,
     *       "cnpj": "00000000000191",
     *       "nome": "Banco do Brasil"
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
     *       "path": "/banco"
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
     *           "message": "O número fornecido não pode ser nulo."
     *         },
     *         {
     *           "message": "O CNPJ fornecido não pode ser nulo."
     *         },
     *         {
     *           "message": "O nome fornecido não pode ser nulo."
     *         }
     *       ],
     *       "path": "/banco"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O número fornecido é menor do que zero."
     *
     *     {
     *       "timestamp": 1484225003793,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O número fornecido deve ser maior do que zero."
     *         }
     *       ],
     *       "path": "/banco"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O número fornecido já pertence a outro banco."
     *
     *     {
     *       "timestamp": 1484225086607,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O número fornecido já pertence a outro banco."
     *         }
     *       ],
     *       "path": "/banco"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O CNPJ fornecido possui menos do que catorze caracteres."
     *
     *     {
     *       "timestamp": 1484225163986,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O CNPJ fornecido deve possuir 14 caracteres."
     *         },
     *         {
     *           "message": "O CNPJ fornecido é inválido."
     *         }
     *       ],
     *       "path": "/banco"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O CNPJ fornecido possui mais do que catorze caracteres."
     *
     *     {
     *       "timestamp": 1484225163986,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O CNPJ fornecido deve possuir 14 caracteres."
     *         },
     *         {
     *           "message": "O CNPJ fornecido é inválido."
     *         }
     *       ],
     *       "path": "/banco"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O CNPJ fornecido é inválido."
     *
     *     {
     *       "timestamp": 1484225163986,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O CNPJ fornecido é inválido."
     *         }
     *       ],
     *       "path": "/banco"
     *     }
     */
    def save() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            JsonSlurper jsonSlurper = new JsonSlurper()
            Banco banco = new Banco(jsonSlurper.parseText(request.JSON.toString()))

            banco.save(flush: true)
            respond banco, [status: HttpStatus.CREATED, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    /**
     * @api {get} /banco/:id Show
     * @apiGroup Banco
     * @apiVersion 1.0.0
     *
     * @apiHeader {String} nomeDeUsuario Nome do usuário que está realizando a requisição.
     * @apiHeader {String} token         Token de acesso.
     *
     * @apiHeaderExample {String} Exemplo de cabeçalho:  
     *     "nomeDeUsuario": admin
     *     "token":         PXO9PmSpQwukqfjqg5ukbG3O1Hgl1yH0
     *
     * @apiParam {Number} id Identificador do banco.
     *
     * @apiSuccess {Number} id        Identificador do banco.
     * @apiSuccess {Number} numero    Numero do banco.
     * @apiSuccess {String} cnpj      CNPJ do banco.
     * @apiSuccess {String} nome      Nome do banco.
     *
     * @apiSuccessExample Resposta de sucesso
     *     HTTP/1.1 200 OK
     *     {
     *       "id": 1,
     *       "numero": 1,
     *       "cnpj": "00000000000191",
     *       "nome": "Banco do Brasil"
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
     *       "path": "/banco/1"
     *     }
     *
     * @apiError NotFound Não existe um banco que possua o identificador passado como parâmetro.
     *
     * @apiErrorExample NotFound
     *     HTTP/1.1 404 Not Found
     *     {
     *       "timestamp": 1484225446800,
     *       "status": 404,
     *       "error": "Not Found",
     *       "message": "Banco não encontrado.",
     *       "path": "/banco/0"
     *     }       
     */
    def show() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Banco banco = bancoService.findById(params.id as Long)

            if (banco) {
                respond banco
            } else {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null))
            }
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }

    /**
     * @api {put} /banco/:id Update
     * @apiGroup Banco
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
     * @apiParam {Number} id Identificador do banco.
     *
     * @apiParamExample {json} Exemplo de requisição
     *     {
     *       "numero": 1, 
     *       "cnpj": "00000000000191",   
     *       "nome": "Banco do Brasil"   
     *     }
     *
     * @apiSuccess {Number} id        Identificador do banco.
     * @apiSuccess {Number} numero    Numero do banco.
     * @apiSuccess {String} cnpj      CNPJ do banco.
     * @apiSuccess {String} nome      Nome do banco.
     *
     * @apiSuccessExample Resposta de sucesso
     *     HTTP/1.1 200 OK
     *     {
     *       "id": 1,
     *       "numero": 1,
     *       "cnpj": "00000000000191",
     *       "nome": "Banco do Brasil"
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
     *       "path": "/banco/1"
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
     *           "message": "O número fornecido não pode ser nulo."
     *         },
     *         {
     *           "message": "O CNPJ fornecido não pode ser nulo."
     *         },
     *         {
     *           "message": "O nome fornecido não pode ser nulo."
     *         }
     *       ],
     *       "path": "/banco/1"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O número fornecido é menor do que zero."
     *
     *     {
     *       "timestamp": 1484225003793,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O número fornecido deve ser maior do que zero."
     *         }
     *       ],
     *       "path": "/banco/1"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O número fornecido já pertence a outro banco."
     *
     *     {
     *       "timestamp": 1484225086607,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O número fornecido já pertence a outro banco."
     *         }
     *       ],
     *       "path": "/banco/1"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O CNPJ fornecido possui menos do que catorze caracteres."
     *
     *     {
     *       "timestamp": 1484225163986,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O CNPJ fornecido deve possuir 14 caracteres."
     *         },
     *         {
     *           "message": "O CNPJ fornecido é inválido."
     *         }
     *       ],
     *       "path": "/banco/1"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O CNPJ fornecido possui mais do que catorze caracteres."
     *
     *     {
     *       "timestamp": 1484225163986,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O CNPJ fornecido deve possuir 14 caracteres."
     *         },
     *         {
     *           "message": "O CNPJ fornecido é inválido."
     *         }
     *       ],
     *       "path": "/banco/1"
     *     }
     *
     *     HTTP/1.1 422 UnprocessableEntity - "O CNPJ fornecido é inválido."
     *
     *     {
     *       "timestamp": 1484225163986,
     *       "status": 422,
     *       "error": "Unprocessable Entity",
     *       "errors": [
     *         {
     *           "message": "O CNPJ fornecido é inválido."
     *         }
     *       ],
     *       "path": "/banco/1"
     *     }
     */
    def update() {
        def autorizado = autorizacaoService.autorizar(request, actionUri)

        if (autorizado) {
            Banco banco = bancoService.findById(params.id as Long)

            if (!banco) {
                render NotFoundResponseUtil.instance.createNotFoundResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Banco.not.found', null, null))
                return
            }

            JsonSlurper jsonSlurper = new JsonSlurper()
            banco.properties = jsonSlurper.parseText(request.JSON.toString())

            banco.save(flush: true)
            respond banco, [status: HttpStatus.OK, view: 'show']
        } else {
            render autorizacaoService.createNotAuthorizedResponse(request, response, messageSource.getMessage('aplicacaofinanceirarestful.Usuario.not.authorized', null, null))
        } 
    }
}