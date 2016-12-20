package aplicacaofinanceirarestful

class Estado implements Serializable {

    String nome

    static hasMany = [cidades: Cidade]

    static constraints = {
        nome nullable: false, unique: true

        cidades nullable: true
    }

    static mapping = {
        id generator: 'sequence', params: [sequence_name: 'estado_seq']
    }
}