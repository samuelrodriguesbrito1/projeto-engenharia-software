package fornecedores;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;

@Repository
class FornecedorDAO extends AbstractJdbcDAO<Fornecedor> {
    private static final String SELECT = """
            SELECT f.*, t.ddd, t.numero AS telefone_numero,
                   tt.id AS telefone_tipo_id, tt.chave AS telefone_tipo_chave, tt.nome AS telefone_tipo_nome,
                   e.logradouro, e.numero AS endereco_numero, e.complemento, e.bairro, e.cep, e.cidade,
                   tl.id AS logradouro_tipo_id, tl.chave AS logradouro_tipo_chave, tl.nome AS logradouro_tipo_nome,
                   uf.sigla, uf.nome AS estado_nome,
                   p.id AS pais_id, p.chave AS pais_chave, p.nome AS pais_nome
              FROM fornecedor f
              JOIN telefone t ON t.fornecedor_codigo = f.codigo
              JOIN dominio tt ON tt.id = t.tipo_id
              JOIN endereco e ON e.fornecedor_codigo = f.codigo
              JOIN dominio tl ON tl.id = e.tipo_id
              JOIN estado uf ON uf.sigla = e.estado_sigla
              JOIN dominio p ON p.id = e.pais_id
             WHERE 1 = 1
            """;

    FornecedorDAO(Conexao conexao) {
        super(conexao);
    }

    @Override
    public void salvar(Fornecedor fornecedor) {
        Long sequencia = jdbc.queryForObject("SELECT nextval('fornecedor_codigo')", Long.class);
        fornecedor.codigo = String.format(Locale.ROOT, "F%04d", sequencia);
        jdbc.update("""
                INSERT INTO fornecedor (codigo, razao_social, nome_fantasia, cnpj, email, status, usuario, dt_cadastro)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """, fornecedor.codigo, fornecedor.razaoSocial, fornecedor.nomeFantasia, fornecedor.cnpj,
                fornecedor.email, fornecedor.status.name(), fornecedor.usuario, fornecedor.dtCadastro);

        Telefone telefone = fornecedor.telefone;
        jdbc.update("INSERT INTO telefone (tipo_id, ddd, numero, fornecedor_codigo) VALUES (?, ?, ?, ?)",
                telefone.tipo.id, telefone.ddd, telefone.numero, fornecedor.codigo);

        Endereco endereco = fornecedor.endereco;
        jdbc.update("""
                INSERT INTO endereco (tipo_id, logradouro, numero, complemento, bairro, cep, cidade,
                                      estado_sigla, pais_id, fornecedor_codigo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, endereco.tipoLogradouro.id, endereco.logradouro, endereco.numero, endereco.complemento,
                endereco.bairro, endereco.cep, endereco.cidade, endereco.estado.sigla, endereco.pais.id, fornecedor.codigo);
    }

    @Override
    public void alterar(Fornecedor fornecedor) {
        if (fornecedor.motivoStatus != null) {
            jdbc.update("UPDATE fornecedor SET status = ?, usuario = ? WHERE codigo = ?",
                    fornecedor.status.name(), fornecedor.usuario, fornecedor.codigo);
            return;
        }
        jdbc.update("""
                UPDATE fornecedor SET razao_social = ?, nome_fantasia = ?, cnpj = ?, email = ?, usuario = ?
                WHERE codigo = ?
                """, fornecedor.razaoSocial, fornecedor.nomeFantasia, fornecedor.cnpj, fornecedor.email,
                fornecedor.usuario, fornecedor.codigo);

        Telefone telefone = fornecedor.telefone;
        jdbc.update("UPDATE telefone SET tipo_id = ?, ddd = ?, numero = ? WHERE fornecedor_codigo = ?",
                telefone.tipo.id, telefone.ddd, telefone.numero, fornecedor.codigo);

        Endereco endereco = fornecedor.endereco;
        jdbc.update("""
                UPDATE endereco SET tipo_id = ?, logradouro = ?, numero = ?, complemento = ?, bairro = ?,
                                    cep = ?, cidade = ?, estado_sigla = ?, pais_id = ?
                WHERE fornecedor_codigo = ?
                """, endereco.tipoLogradouro.id, endereco.logradouro, endereco.numero, endereco.complemento,
                endereco.bairro, endereco.cep, endereco.cidade, endereco.estado.sigla, endereco.pais.id, fornecedor.codigo);
    }

    @Override
    public List<Fornecedor> consultar(Fornecedor filtro) {
        StringBuilder sql = new StringBuilder(SELECT);
        List<Object> parametros = new ArrayList<>();
        adicionarFiltro(sql, parametros, "f.codigo = ?", filtro.codigo);
        adicionarFiltro(sql, parametros, "strpos(lower(f.razao_social), lower(?)) > 0", filtro.razaoSocial);
        adicionarFiltro(sql, parametros, "strpos(lower(f.nome_fantasia), lower(?)) > 0", filtro.nomeFantasia);
        adicionarFiltro(sql, parametros, "f.cnpj = ?", filtro.cnpj);
        if (filtro.endereco != null) {
            adicionarFiltro(sql, parametros, "strpos(lower(e.cidade), lower(?)) > 0", filtro.endereco.cidade);
            if (filtro.endereco.estado != null) {
                adicionarFiltro(sql, parametros, "e.estado_sigla = ?", filtro.endereco.estado.sigla);
            }
        }
        if (filtro.status != null) {
            adicionarFiltro(sql, parametros, "f.status = ?", filtro.status.name());
        }
        sql.append(" ORDER BY f.dt_cadastro, f.codigo");
        return jdbc.query(sql.toString(), this::montar, parametros.toArray());
    }

    Fornecedor buscarParaAlterar(String codigo) {
        // Mantém a linha bloqueada até a gravação do fornecedor e do log.
        List<String> codigos = jdbc.queryForList(
                "SELECT codigo FROM fornecedor WHERE codigo = ? FOR UPDATE", String.class, codigo);
        if (codigos.isEmpty()) {
            return null;
        }
        Fornecedor filtro = new Fornecedor();
        filtro.codigo = codigo;
        return consultar(filtro).stream().findFirst().orElse(null);
    }

    private void adicionarFiltro(StringBuilder sql, List<Object> parametros, String condicao, String valor) {
        if (valor != null && !valor.isBlank()) {
            sql.append(" AND ").append(condicao);
            parametros.add(valor);
        }
    }

    private Fornecedor montar(ResultSet rs, int linha) throws SQLException {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.codigo = rs.getString("codigo");
        fornecedor.razaoSocial = rs.getString("razao_social");
        fornecedor.nomeFantasia = rs.getString("nome_fantasia");
        fornecedor.cnpj = rs.getString("cnpj");
        fornecedor.email = rs.getString("email");
        fornecedor.status = StatusFornecedor.valueOf(rs.getString("status"));
        fornecedor.usuario = rs.getString("usuario");
        fornecedor.dtCadastro = rs.getObject("dt_cadastro", LocalDateTime.class);

        TipoTelefone tipoTelefone = new TipoTelefone(rs.getInt("telefone_tipo_id"),
                rs.getString("telefone_tipo_chave"), rs.getString("telefone_tipo_nome"));
        fornecedor.telefone = new Telefone(tipoTelefone, rs.getString("ddd"), rs.getString("telefone_numero"));

        TipoLogradouro tipoLogradouro = new TipoLogradouro(rs.getInt("logradouro_tipo_id"),
                rs.getString("logradouro_tipo_chave"), rs.getString("logradouro_tipo_nome"));
        Estado estado = new Estado(rs.getString("sigla"), rs.getString("estado_nome"));
        Pais pais = new Pais(rs.getInt("pais_id"), rs.getString("pais_chave"), rs.getString("pais_nome"));
        fornecedor.endereco = new Endereco(tipoLogradouro, rs.getString("logradouro"), rs.getString("endereco_numero"),
                rs.getString("complemento"), rs.getString("bairro"), rs.getString("cep"), rs.getString("cidade"),
                estado, pais);
        return fornecedor;
    }
}
