package fornecedores;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
class DominioViewHelper implements IViewHelper {
    final DominioDAO<TipoTelefone> telefones;
    final DominioDAO<TipoLogradouro> logradouros;
    final DominioDAO<Pais> paises;
    final DominioDAO<CategoriaInativacao> inativacoes;
    final DominioDAO<CategoriaAtivacao> ativacoes;
    final EstadoDAO estados;
    private final JsonMapper json;

    DominioViewHelper(Conexao conexao, EstadoDAO estados, JsonMapper json) {
        this.estados = estados;
        this.json = json;
        telefones = new DominioDAO<>(conexao, "TELEFONE", TipoTelefone::new);
        logradouros = new DominioDAO<>(conexao, "LOGRADOURO", TipoLogradouro::new);
        paises = new DominioDAO<>(conexao, "PAIS", Pais::new);
        inativacoes = new DominioDAO<>(conexao, "INATIVACAO", CategoriaInativacao::new);
        ativacoes = new DominioDAO<>(conexao, "ATIVACAO", CategoriaAtivacao::new);
    }

    @Override
    public EntidadeDominio getEntidade(Map<String, String> parametros) {
        return switch (parametros.getOrDefault("tipo", "")) {
            case "phoneType" -> telefones.buscar(parametros.get("chave"));
            case "streetType" -> logradouros.buscar(parametros.get("chave"));
            case "country" -> paises.buscar(parametros.get("chave"));
            case "inactivation" -> inativacoes.buscar(parametros.get("chave"));
            case "activation" -> ativacoes.buscar(parametros.get("chave"));
            case "state" -> estados.buscar(parametros.get("chave"));
            default -> null;
        };
    }

    @Override
    public String getView(Resultado resultado) {
        return json.writeValueAsString(resultado.entidades().stream().map(this::opcao).toList());
    }

    String catalogos() {
        return json.writeValueAsString(Map.of(
                "phoneType", opcoes(telefones.consultar(null)),
                "streetType", opcoes(logradouros.consultar(null)),
                "country", opcoes(paises.consultar(null)),
                "inactivation", opcoes(inativacoes.consultar(null)),
                "activation", opcoes(ativacoes.consultar(null)),
                "state", opcoes(estados.consultar(null))));
    }

    private List<Opcao> opcoes(List<? extends EntidadeDominio> entidades) {
        return entidades.stream().map(this::opcao).toList();
    }

    private Opcao opcao(EntidadeDominio entidade) {
        if (entidade instanceof Estado estado) {
            return new Opcao(estado.sigla, estado.nome);
        }
        Dominio dominio = (Dominio) entidade;
        return new Opcao(dominio.chave, dominio.nome);
    }

    record Opcao(String chave, String nome) {
    }
}
