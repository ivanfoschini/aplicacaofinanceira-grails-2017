package aplicacaofinanceirarestful

class Correntista implements Serializable {

    Boolean titularidade

    Conta conta
    Cliente cliente

    static belongsTo = Conta

    static constraints = {
        titularidade nullable: false

        conta nullable: false
        cliente nullable: false
    }

    static mapping = {
        id composite: ['conta', 'cliente']

        conta column: "conta_id"
        cliente column: "cliente_id"
    }
}