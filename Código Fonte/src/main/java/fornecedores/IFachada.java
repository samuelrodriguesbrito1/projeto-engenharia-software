package fornecedores;

interface IFachada {
    Resultado salvar(EntidadeDominio entidade);
    Resultado alterar(EntidadeDominio entidade);
    Resultado inativar(EntidadeDominio entidade);
    Resultado ativar(EntidadeDominio entidade);
    Resultado consultar(EntidadeDominio entidade);
}
