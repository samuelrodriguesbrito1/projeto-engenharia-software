package fornecedores;

interface IStrategy {
    // Retorna null quando a regra não encontra erro.
    String processar(EntidadeDominio entidade);
}

abstract class RegraFornecedor implements IStrategy {
    @Override
    public final String processar(EntidadeDominio entidade) {
        return processarFornecedor((Fornecedor) entidade);
    }

    protected abstract String processarFornecedor(Fornecedor fornecedor);
}
