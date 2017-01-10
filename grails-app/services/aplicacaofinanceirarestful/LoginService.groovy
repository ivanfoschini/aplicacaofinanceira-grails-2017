package aplicacaofinanceirarestful

import grails.transaction.Transactional

@Transactional
class LoginService {

    def generateRandomToken() {
        def pool = ['a'..'z', 'A'..'Z', 0..9].flatten()
        Random rand = new Random(System.currentTimeMillis())

        def tokenChars = (0..31).collect { pool[rand.nextInt(pool.size())] }
        def token = tokenChars.join()

        return token
    }

    def loginResponse(token) {
        def responseBody = [:]
        responseBody.token = token

        return responseBody
    }
}