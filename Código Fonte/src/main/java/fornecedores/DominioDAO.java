package fornecedores;

import java.util.List;

final class DominioDAO<T extends Dominio> extends AbstractJdbcDAO<T> {
    private final String tipo;
    private final Fabrica<T> fabrica;

    DominioDAO(Conexao conexao, String tipo, Fabrica<T> fabrica) {
        super(conexao);
        this.tipo = tipo;
        this.fabrica = fabrica;
    }

    @Override
    public List<T> consultar(T filtro) {
        if (filtro != null) {
            return jdbc.query(
                    "SELECT id, chave, nome FROM dominio WHERE tipo = ? AND chave = ?",
                    (rs, linha) -> fabrica.criar(rs.getInt("id"), rs.getString("chave"), rs.getString("nome")),
                    tipo, filtro.chave);
        }
        return jdbc.query(
                "SELECT id, chave, nome FROM dominio WHERE tipo = ? ORDER BY id",
                (rs, linha) -> fabrica.criar(rs.getInt("id"), rs.getString("chave"), rs.getString("nome")),
                tipo);
    }

    T buscar(String chave) {
        if (chave == null || chave.isBlank()) {
            return null;
        }
        return consultar(fabrica.criar(null, chave, null)).stream()
                .findFirst().orElse(null);
    }

    interface Fabrica<T> {
        T criar(Integer id, String chave, String nome);
    }
}
