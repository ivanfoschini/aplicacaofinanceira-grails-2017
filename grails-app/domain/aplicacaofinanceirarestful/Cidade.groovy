package aplicacaofinanceirarestful

class Cidade implements Serializable {

    String nome

    Estado estado

    static hasMany = [enderecos: Endereco]

    static constraints = {
        nome nullable: false

        estado nullable: false

        enderecos nullable: true
    }

    static mapping = {
        id generator: 'sequence', params: [sequence_name: 'cidade_seq']

        estado column: 'estado_id'
    }
}