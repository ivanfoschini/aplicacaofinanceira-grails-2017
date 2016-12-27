package aplicacaofinanceirarestful;

import java.util.InputMismatchException;

public class CPFValidator {

    public static boolean validateCPF(String cpf) {
        /** Remove os pontos finais e o hífen do CPF. */
        cpf = cpf.replace(".", "");
        cpf = cpf.replace("-", "");

        /** CPFs formados por uma sequência de números iguais (por exemplo 11111111111 ou
         *  22222222222) são considerados inválidos.  */
        if (cpf == null || cpf.length() != 11 ||
                cpf.matches("^(0{11}|1{11}|2{11}|3{11}|4{11}|5{11}|6{11}|7{11}|9{11}|9{11})$")) {
            return false;
        }

        char digito10; /** Recebe o primeiro dígito verificador calculado. */
        char digito11; /** Recebe o segundo dígito verificador calculado. */
        int soma; /** Soma das parcelas calculadas. */
        int resto; /** Resto da divisão (operador %). */
        int numero; /** Recebe o caractere do CPF transformado no número inteiro correspondente. */
        int peso; /** Peso utilizado na definição das somas parciais. Para o primeiro dígito
         verificador começa em 10 e vai sendo decrementado de 1 a cada passo.
         Para o segundo dígito começa em 11 e vai sendo decrementado de 1 a cada
         passo. */

        try {
            /** Cálculo do primeiro dígito verificador. */
            soma = 0;
            peso = 10;

            for (int i = 0; i < 9; i++) {
                /** Convere o i-esimo caractere do CPF em número. Por exemplo, transforma o
                 *  caractere '0' no inteiro 0. 48 é a posição de '0' na tabela ASCII. */
                numero = (int) (cpf.charAt(i) - 48);
                soma = soma + (numero * peso);
                peso = peso - 1;
            }

            resto = 11 - (soma % 11);

            if ((resto == 10) || (resto == 11)) {
                digito10 = '0';
            } else {
                digito10 = (char) (resto + 48); /** Converte para o respectivo caractere numérico.*/
            }

            /** Cálculo do segundo dígito verificador. */
            soma = 0;
            peso = 11;

            for (int i = 0; i < 10; i++) {
                numero = (int) (cpf.charAt(i) - 48);
                soma = soma + (numero * peso);
                peso = peso - 1;
            }

            resto = 11 - (soma % 11);

            if ((resto == 10) || (resto == 11)) {
                digito11 = '0';
            } else {
                digito11 = (char) (resto + 48);
            }

            /** Verifica se os dígitos calculados conferem com os dígitos informados. */
            if ((digito10 == cpf.charAt(9)) && (digito11 == cpf.charAt(10))) {
                return true;
            } else {
                return false;
            }
        } catch (InputMismatchException erro) {
            return false;
        }
    }
}