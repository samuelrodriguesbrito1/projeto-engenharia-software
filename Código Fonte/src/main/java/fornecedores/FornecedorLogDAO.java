package fornecedores;

import org.springframework.stereotype.Repository;

@Repository
class FornecedorLogDAO extends AbstractJdbcDAO<FornecedorLog> {
    FornecedorLogDAO(Conexao conexao) {
        super(conexao);
    }

    @Override
    public void salvar(FornecedorLog log) {
        jdbc.update("""
                INSERT INTO fornecedor_log (fornecedor_codigo, operacao, data_hora, usuario, dados_antes,
                                            dados_depois, justificativa, categoria_inativacao_id, categoria_ativacao_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, log.fornecedor.codigo, log.operacao.name(), log.dataHora, log.usuario, log.dadosAntes,
                log.dadosDepois, log.justificativa, id(log.categoriaInativacao), id(log.categoriaAtivacao));
    }

    private Integer id(Dominio categoria) {
        return categoria == null ? null : categoria.id;
    }
}
