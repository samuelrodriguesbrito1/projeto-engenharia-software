package fornecedores;

import java.time.LocalDateTime;

final class GerarLog extends RegraFornecedor {
    private final IDAO<FornecedorLog> dao;
    private final FornecedorViewHelper viewHelper;
    private final OperacaoLog operacao;

    GerarLog(IDAO<FornecedorLog> dao, FornecedorViewHelper viewHelper, OperacaoLog operacao) {
        this.dao = dao;
        this.viewHelper = viewHelper;
        this.operacao = operacao;
    }

    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        FornecedorLog log = new FornecedorLog();
        log.fornecedor = fornecedor;
        log.operacao = operacao;
        log.dataHora = LocalDateTime.now();
        log.usuario = fornecedor.usuario;
        log.dadosAntes = viewHelper.snapshot(fornecedor.estadoAnterior);
        log.dadosDepois = viewHelper.snapshot(fornecedor);
        if (fornecedor.motivoStatus != null) {
            log.justificativa = fornecedor.motivoStatus.justificativa();
            log.categoriaInativacao = fornecedor.motivoStatus.categoriaInativacao();
            log.categoriaAtivacao = fornecedor.motivoStatus.categoriaAtivacao();
        }
        dao.salvar(log);
        return null;
    }
}
