package fornecedores;

interface ICommand {
    Resultado executar(EntidadeDominio entidade);
}
