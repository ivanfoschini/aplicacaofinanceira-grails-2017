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

    def validateDateFromJSON(jsonObject, key) {
        String dateString = jsonObject.get(key)

        Date date = stringToDate(dateString)

        if (!date) {
            return false
        }

        return true
    }
}