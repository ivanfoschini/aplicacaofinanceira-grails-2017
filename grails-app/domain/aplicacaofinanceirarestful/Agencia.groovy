package aplicacaofinanceirarestful

class Agencia implements Serializable {

    Long numero
    String nome

    Banco banco
    Endereco endereco

    static hasMany = [contas: Conta]

    static constraints = {
        numero nullable: false, min: 1L, unique: true
        nome nullable: false

        banco nullable: false
        endereco nullable: false

        contas nullable: true
    }

    static mapping = {
        id generator: 'sequence', params: [sequence_name: 'agencia_seq']

        banco column: "banco_id"
        endereco column: "endereco_id"
    }
}