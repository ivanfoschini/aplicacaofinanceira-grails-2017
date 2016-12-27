package aplicacaofinanceirarestful

class ClientePessoaFisica extends Cliente implements Serializable {

    String rg
    String cpf

    static constraints = {
        rg nullable: false
        cpf nullable: false, minSize: 11, maxSize: 11, validator: { if (!CPFValidator.validateCPF(it)) return ['invalid'] }
    }

    static mapping = {
        discriminator value: "F"
    }
}