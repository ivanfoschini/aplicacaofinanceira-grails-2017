package aplicacaofinanceirarestful

class Conta implements Serializable {

    Long numero
    Date dataDeAbertura
    Double saldo

    static hasOne = [agencia: Agencia]

    static hasMany = [correntistas: Correntista]

    static constraints = {
        numero nullable: false, min: 1L, unique: true
        dataDeAbertura nullable: false
        saldo nullable: false

        agencia nullable: false

        correntistas nullable: true
    }

    static mapping = {
        tablePerHierarchy false

        id generator: 'sequence', params: [sequence_name: 'conta_seq']
    }
}