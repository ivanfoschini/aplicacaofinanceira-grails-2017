package aplicacaofinanceirarestful

class Servico implements Serializable {

    String uri

    static belongsTo = Papel

    static hasMany = [papeis: Papel]

    static constraints = {
        uri nullable: false, unique: true

        papeis nullable: true
    }

    static mapping = {
        id generator: 'sequence', params: [sequence_name: 'servico_seq']

        papeis joinTable: [name: "papel_servico", key: 'servico_id']
    }
}