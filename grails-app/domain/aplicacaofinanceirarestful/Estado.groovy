package aplicacaofinanceirarestful

class Estado implements Serializable {

    String nome

    static constraints = {
        nome nullable: false, unique: true
    }

    static mapping = {
        id generator: 'sequence', params: [sequence_name: 'estado_seq']
    }
}