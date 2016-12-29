package aplicacaofinanceirarestful

import java.text.SimpleDateFormat

@Singleton
class DateUtil {

    def dateToString(date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd")

        try {
            return formatter.format(date)
        } catch (Exception e) {
            return null
        }
    }

    def stringToDate(dateString) {
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd")

        try {
            dateParser.lenient = false
            return dateParser.parse(dateString)
        } catch (Exception e) {
            return null
        }
    }

    def validateDataDeAbertura(jsonObject) {
        String dataDeAberturaString = jsonObject.get("dataDeAbertura")

        Date dataDeAbertura = stringToDate(dataDeAberturaString)

        if (!dataDeAbertura) {
            return false
        }

        return true
    }
}