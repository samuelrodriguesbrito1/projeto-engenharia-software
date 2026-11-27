package fornecedores;

import org.springframework.jdbc.core.JdbcTemplate;

abstract class AbstractJdbcDAO<T extends EntidadeDominio> implements IDAO<T> {
    protected final JdbcTemplate jdbc;

    AbstractJdbcDAO(Conexao conexao) {
        jdbc = conexao.jdbc();
    }
}
