package aplicacaofinanceirarestful

class Cliente implements Serializable {

    String nome
    String status

    static hasMany = [correntistas: Correntista,
                      enderecos: Endereco]

    static constraints = {
        nome nullable: false
        status nullable: false, inList: ["ativo", "inativo"]

        correntistas nullable: true
        enderecos nullable: false
    }

    static mapping = {
        discriminator column: "tipo", value: "G"

        id generator: 'sequence', params: [sequence_name: 'cliente_seq']
    }
}