package aplicacaofinanceirarestful

class ContaPoupanca extends Conta implements Serializable {

    Date dataDeAniversario
    Double correcaoMonetaria
    Double juros

    static constraints = {
        dataDeAniversario nullable: false
        correcaoMonetaria nullable: false, min: 0D
        juros nullable: false, min: 0D
    }
}