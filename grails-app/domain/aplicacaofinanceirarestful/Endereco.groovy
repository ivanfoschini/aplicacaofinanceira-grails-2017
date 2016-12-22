package aplicacaofinanceirarestful

class Endereco implements Serializable {

    String logradouro
    String numero
    String complemento
    String bairro
    String cep

    Cidade cidade

    static hasOne = [agencia: Agencia]

    static constraints = {
        logradouro nullable: false
        numero nullable: false
        complemento nullable: true
        bairro nullable: false
        cep nullable: false, minSize: 9, maxSize: 9, validator: { if (!it.matches("\\d{5}-\\d{3}")) return ['invalid'] }

        cidade nullable: false

        agencia nullable: true
    }

    static mapping = {
        id generator: 'sequence', params: [sequence_name: 'endereco_seq']

        cidade column: "cidade_id"
    }
}
