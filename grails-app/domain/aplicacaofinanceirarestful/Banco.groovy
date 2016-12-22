package aplicacaofinanceirarestful

class Banco implements Serializable {

    Long numero
    String cnpj
    String nome

    static hasMany = [agencias: Agencia]

    static constraints = {
        numero nullable: false, min: 1L, unique: true
        cnpj nullable: false, minSize: 14, maxSize: 14, validator: { if (!CNPJValidator.validateCNPJ(it)) return ['invalid'] }
        nome nullable: false

        agencias nullable: true
    }

    static mapping = {
        id generator: 'sequence', params: [sequence_name: 'banco_seq']
    }
}