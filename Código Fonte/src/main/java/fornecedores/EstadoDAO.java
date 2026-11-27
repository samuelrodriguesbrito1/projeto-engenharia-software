package fornecedores;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
class EstadoDAO extends AbstractJdbcDAO<Estado> {
    EstadoDAO(Conexao conexao) {
        super(conexao);
    }

    @Override
    public List<Estado> consultar(Estado filtro) {
        if (filtro == null) {
            return jdbc.query("SELECT sigla, nome FROM estado ORDER BY sigla",
                    (rs, linha) -> new Estado(rs.getString("sigla"), rs.getString("nome")));
        }
        return jdbc.query("SELECT sigla, nome FROM estado WHERE sigla = ?",
                (rs, linha) -> new Estado(rs.getString("sigla"), rs.getString("nome")), filtro.sigla);
    }

    Estado buscar(String sigla) {
        return consultar(new Estado(sigla, null)).stream().findFirst().orElse(null);
    }
}
