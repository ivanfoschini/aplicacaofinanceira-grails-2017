package aplicacaofinanceirarestful

class Papel implements Serializable {

    String nome

    static belongsTo = Usuario

    static hasMany = [usuarios: Usuario,
                      servicos: Servico]

    static constraints = {
        nome nullable: false, unique: true

        usuarios nullable: true
        servicos nullable: true
    }

    static mapping = {
        id generator: 'sequence', params: [sequence_name: 'papel_seq']

        usuarios joinTable: [name: "usuario_papel", key: 'papel_id']
        servicos joinTable: [name: "papel_servico", key: 'papel_id']
    }
}