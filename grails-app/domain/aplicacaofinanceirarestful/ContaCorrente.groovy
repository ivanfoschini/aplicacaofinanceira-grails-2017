package aplicacaofinanceirarestful

class ContaCorrente extends Conta implements Serializable {

    Double limite

    static constraints = {
        limite nullable: false, min: 0D
    }
}