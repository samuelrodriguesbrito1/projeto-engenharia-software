package fornecedores;

enum Operacao {
    SALVAR("FORNECEDOR_INCLUIR"),
    ALTERAR("FORNECEDOR_ALTERAR"),
    INATIVAR("FORNECEDOR_INATIVAR"),
    ATIVAR("FORNECEDOR_ATIVAR"),
    CONSULTAR("FORNECEDOR_CONSULTAR");

    private final String permissao;

    Operacao(String permissao) {
        this.permissao = permissao;
    }

    String permissao() {
        return permissao;
    }
}

enum StatusFornecedor {
    ATIVO, INATIVO
}

enum OperacaoLog {
    INCLUSAO, ALTERACAO, INATIVACAO, ATIVACAO
}
