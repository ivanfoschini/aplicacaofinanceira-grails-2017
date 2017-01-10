package aplicacaofinanceirarestful

class Usuario implements Serializable {

    String nomeDeUsuario
    String senha
    String token
    Date ultimoAcesso

    static hasMany = [papeis: Papel]

    static constraints = {
        nomeDeUsuario nullable: false, unique: true
        senha nullable: false, minSize: 32, maxSize: 32
        token nullable: true, minSize: 32, maxSize: 32
        ultimoAcesso nullable: true

        papeis nullable: true
    }

    static mapping = {
        id generator: 'sequence', params: [sequence_name: 'usuario_seq']

        papeis joinTable: [name: "usuario_papel", key: 'usuario_id']
    }
}