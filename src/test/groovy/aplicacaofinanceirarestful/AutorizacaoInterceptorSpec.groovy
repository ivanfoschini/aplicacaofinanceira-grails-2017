package aplicacaofinanceirarestful


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(AutorizacaoInterceptor)
class AutorizacaoInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test autorizacao interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"autorizacao")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
