package aplicacaofinanceirarestful

import groovy.time.TimeCategory

class RemoverTokensAntigosJob {
	
    static triggers = {
    	cron name: 'executarACadaCincoSegundos', cronExpression: "0/5 * * * * ?"
    }

    def execute() {
    	Date quatroHorasAtras

        use(TimeCategory) {
            quatroHorasAtras = new Date() - 4.hours                           
        }

        Usuario.executeUpdate("update Usuario usu set usu.token = null, usu.ultimoAcesso = null where usu.ultimoAcesso < '" + quatroHorasAtras + "'")
    }
}