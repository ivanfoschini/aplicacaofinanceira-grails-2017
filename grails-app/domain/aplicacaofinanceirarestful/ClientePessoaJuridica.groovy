package aplicacaofinanceirarestful

class ClientePessoaJuridica extends Cliente implements Serializable {

    String cnpj

    static constraints = {
        cnpj nullable: false, minSize: 14, maxSize: 14, validator: { if (!CNPJValidator.validateCNPJ(it)) return ['invalid'] }
    }

    static mapping = {
        discriminator value: "J"
    }
}
