package aplicacaofinanceirarestful

class BootStrap {

    def init = { servletContext ->
        def usuarios = Usuario.findAll()

        if (usuarios.isEmpty()) {
            Usuario usuarioAdmin = new Usuario(nomeDeUsuario: 'admin', senha: 'admin'.encodeAsMD5(), token: 'PXO9PmSpQwukqfjqg5ukbG3O1Hgl1yH0', ultimoAcesso: new Date()).save()
            Usuario usuarioFuncionario = new Usuario(nomeDeUsuario: 'funcionario', senha: 'funcionario'.encodeAsMD5(), token: 'cc7a84634199040d54376793842fe035', ultimoAcesso: new Date()).save()

            Papel papelAdmin = new Papel(nome: 'ADMIN').save()
            Papel papelFuncionario = new Papel(nome: 'FUNCIONARIO').save()

            usuarioAdmin.addToPapeis(papelAdmin).save()
            usuarioAdmin.addToPapeis(papelFuncionario).save()
            usuarioFuncionario.addToPapeis(papelFuncionario).save()

            Servico bancoDelete = new Servico(uri: "/banco/delete").save()
            Servico bancoList = new Servico(uri: "/banco/list").save()
            Servico bancoSave = new Servico(uri: "/banco/save").save()
            Servico bancoShow = new Servico(uri: "/banco/show").save()
            Servico bancoUpdate = new Servico(uri: "/banco/update").save()

            papelAdmin.addToServicos(bancoDelete).save()

            papelAdmin.addToServicos(bancoList).save()
            papelFuncionario.addToServicos(bancoList).save()

            papelAdmin.addToServicos(bancoSave).save()

            papelAdmin.addToServicos(bancoShow).save()
            papelFuncionario.addToServicos(bancoShow).save()

            papelAdmin.addToServicos(bancoUpdate).save()

            Servico estadoDelete = new Servico(uri: "/estado/delete").save()
            Servico estadoList = new Servico(uri: "/estado/list").save()
            Servico estadoSave = new Servico(uri: "/estado/save").save()
            Servico estadoShow = new Servico(uri: "/estado/show").save()
            Servico estadoUpdate = new Servico(uri: "/estado/update").save()

            papelAdmin.addToServicos(estadoDelete).save()

            papelAdmin.addToServicos(estadoList).save()
            papelFuncionario.addToServicos(estadoList).save()

            papelAdmin.addToServicos(estadoSave).save()

            papelAdmin.addToServicos(estadoShow).save()
            papelFuncionario.addToServicos(estadoShow).save()

            papelAdmin.addToServicos(estadoUpdate)

            Servico agenciaDelete = new Servico(uri: "/agencia/delete").save()
            Servico agenciaList = new Servico(uri: "/agencia/list").save()
            Servico agenciaSave = new Servico(uri: "/agencia/save").save()
            Servico agenciaShow = new Servico(uri: "/agencia/show").save()
            Servico agenciaUpdate = new Servico(uri: "/agencia/update").save()

            papelAdmin.addToServicos(agenciaDelete).save()

            papelAdmin.addToServicos(agenciaList).save()
            papelFuncionario.addToServicos(agenciaList).save()

            papelAdmin.addToServicos(agenciaSave).save()

            papelAdmin.addToServicos(agenciaShow).save()
            papelFuncionario.addToServicos(agenciaShow).save()

            papelAdmin.addToServicos(agenciaUpdate).save()

            Servico cidadeDelete = new Servico(uri: "/cidade/delete").save()
            Servico cidadeList = new Servico(uri: "/cidade/list").save()
            Servico cidadeSave = new Servico(uri: "/cidade/save").save()
            Servico cidadeShow = new Servico(uri: "/cidade/show").save()
            Servico cidadeUpdate = new Servico(uri: "/cidade/update").save()

            papelAdmin.addToServicos(cidadeDelete).save()

            papelAdmin.addToServicos(cidadeList).save()
            papelFuncionario.addToServicos(cidadeList).save()

            papelAdmin.addToServicos(cidadeSave).save()

            papelAdmin.addToServicos(cidadeShow).save()
            papelFuncionario.addToServicos(cidadeShow).save()

            papelAdmin.addToServicos(cidadeUpdate).save()

            Servico clientePessoaFisicaDelete = new Servico(uri: "/clientePessoaFisica/delete").save()
            Servico clientePessoaFisicaList = new Servico(uri: "/clientePessoaFisica/list").save()
            Servico clientePessoaFisicaSave = new Servico(uri: "/clientePessoaFisica/save").save()
            Servico clientePessoaFisicaShow = new Servico(uri: "/clientePessoaFisica/show").save()
            Servico clientePessoaFisicaUpdate = new Servico(uri: "/clientePessoaFisica/update").save()

            papelAdmin.addToServicos(clientePessoaFisicaDelete).save()

            papelAdmin.addToServicos(clientePessoaFisicaList).save()
            papelFuncionario.addToServicos(clientePessoaFisicaList).save()

            papelAdmin.addToServicos(clientePessoaFisicaSave).save()

            papelAdmin.addToServicos(clientePessoaFisicaShow).save()
            papelFuncionario.addToServicos(clientePessoaFisicaShow).save()

            papelAdmin.addToServicos(clientePessoaFisicaUpdate).save()

            Servico clientePessoaJuridicaDelete = new Servico(uri: "/clientePessoaJuridica/delete").save()
            Servico clientePessoaJuridicaList = new Servico(uri: "/clientePessoaJuridica/list").save()
            Servico clientePessoaJuridicaSave = new Servico(uri: "/clientePessoaJuridica/save").save()
            Servico clientePessoaJuridicaShow = new Servico(uri: "/clientePessoaJuridica/show").save()
            Servico clientePessoaJuridicaUpdate = new Servico(uri: "/clientePessoaJuridica/update").save()

            papelAdmin.addToServicos(clientePessoaJuridicaDelete).save()

            papelAdmin.addToServicos(clientePessoaJuridicaList).save()
            papelFuncionario.addToServicos(clientePessoaJuridicaList).save()

            papelAdmin.addToServicos(clientePessoaJuridicaSave).save()

            papelAdmin.addToServicos(clientePessoaJuridicaShow).save()
            papelFuncionario.addToServicos(clientePessoaJuridicaShow).save()

            papelAdmin.addToServicos(clientePessoaJuridicaUpdate).save()

            Servico contaCorrenteDelete = new Servico(uri: "/contaCorrente/delete").save()
            Servico contaCorrenteList = new Servico(uri: "/contaCorrente/list").save()
            Servico contaCorrenteSave = new Servico(uri: "/contaCorrente/save").save()
            Servico contaCorrenteShow = new Servico(uri: "/contaCorrente/show").save()
            Servico contaCorrenteUpdate = new Servico(uri: "/contaCorrente/update").save()

            papelAdmin.addToServicos(contaCorrenteDelete).save()

            papelAdmin.addToServicos(contaCorrenteList).save()
            papelFuncionario.addToServicos(contaCorrenteList).save()

            papelAdmin.addToServicos(contaCorrenteSave).save()

            papelAdmin.addToServicos(contaCorrenteShow).save()
            papelFuncionario.addToServicos(contaCorrenteShow).save()

            papelAdmin.addToServicos(contaCorrenteUpdate).save()            

            Servico contaPoupancaDelete = new Servico(uri: "/contaPoupanca/delete").save()
            Servico contaPoupancaList = new Servico(uri: "/contaPoupanca/list").save()
            Servico contaPoupancaSave = new Servico(uri: "/contaPoupanca/save").save()
            Servico contaPoupancaShow = new Servico(uri: "/contaPoupanca/show").save()
            Servico contaPoupancaUpdate = new Servico(uri: "/contaPoupanca/update").save()

            papelAdmin.addToServicos(contaPoupancaDelete).save()

            papelAdmin.addToServicos(contaPoupancaList).save()
            papelFuncionario.addToServicos(contaPoupancaList).save()

            papelAdmin.addToServicos(contaPoupancaSave).save()

            papelAdmin.addToServicos(contaPoupancaShow).save()
            papelFuncionario.addToServicos(contaPoupancaShow).save()

            papelAdmin.addToServicos(contaPoupancaUpdate).save() 

            Servico correntistaAssociate = new Servico(uri: "/correntista/associate").save()
            Servico correntistaShowByCliente = new Servico(uri: "/correntista/showByCliente").save()
            Servico correntistaShowByConta = new Servico(uri: "/correntista/showByConta").save()

            papelAdmin.addToServicos(correntistaAssociate).save()

            papelAdmin.addToServicos(correntistaShowByCliente).save()
            papelFuncionario.addToServicos(correntistaShowByCliente).save()

            papelAdmin.addToServicos(correntistaShowByConta).save()
            papelFuncionario.addToServicos(correntistaShowByConta).save()
        }
    }

    def destroy = {
    }
}
