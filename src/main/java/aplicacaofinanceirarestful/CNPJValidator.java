package aplicacaofinanceirarestful;

public class CNPJValidator {

    public static boolean validateCNPJ(String cnpj) {
        if (cnpj.length() != 14) {
            return false;
        }

        int soma = 0;
        int dig = 0;

        String digitosIniciais = cnpj.substring(0, 12);
        char[] cnpjCharArray = cnpj.toCharArray();

        /* Primeira parte da validacao */
        for (int i = 0; i < 4; i++) {
            if (cnpjCharArray[i] - 48 >= 0 && cnpjCharArray[i] - 48 <= 9) {
                soma += (cnpjCharArray[i] - 48) * (6 - (i + 1));
            }
        }

        for (int i = 0; i < 8; i++) {
            if (cnpjCharArray[i + 4] - 48 >= 0 && cnpjCharArray[i + 4] - 48 <= 9) {
                soma += (cnpjCharArray[i + 4] - 48) * (10 - (i + 1));
            }
        }

        dig = 11 - (soma % 11);

        digitosIniciais += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

        /* Segunda parte da validacao */
        soma = 0;

        for (int i = 0; i < 5; i++) {
            if (cnpjCharArray[i] - 48 >= 0 && cnpjCharArray[i] - 48 <= 9) {
                soma += (cnpjCharArray[i] - 48) * (7 - (i + 1));
            }
        }

        for (int i = 0; i < 8; i++) {
            soma += (cnpjCharArray[i + 5] - 48) * (10 - (i + 1));
        }

        dig = 11 - (soma % 11);
        digitosIniciais += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

        return cnpj.equals(digitosIniciais);
    }
}