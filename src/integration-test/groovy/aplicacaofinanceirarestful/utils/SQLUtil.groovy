package aplicacaofinanceirarestful.utils

import groovy.sql.Sql

@Singleton
class SQLUtil {

	public executeQuery(query) {
		def db = [url: "jdbc:postgresql://localhost:5432/aplicacao_financeira_test", user: "postgres", password: "postgres", driver: "org.postgresql.Driver"]
        def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)

        def resultado = sql.execute(query)
	}
}