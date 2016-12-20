package aplicacaofinanceirarestful

import grails.converters.JSON
import org.springframework.http.HttpStatus

@Singleton
class NotFoundResponseUtil {

    def createNotFoundResponse(request, response, message) {
        int status = HttpStatus.NOT_FOUND.value()

        def responseBody = [:]
        responseBody.timestamp = new Date().getTime()
        responseBody.status = status
        responseBody.error = 'Not Found'
        responseBody.message = message
        responseBody.path = request.requestURI

        response.setStatus(status)

        return responseBody as JSON
    }
}
