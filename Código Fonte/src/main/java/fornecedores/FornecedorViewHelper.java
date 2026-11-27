package fornecedores;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
class FornecedorViewHelper implements IViewHelper {
    private final DominioViewHelper dominios;
    private final JsonMapper json;

    FornecedorViewHelper(DominioViewHelper dominios, JsonMapper json) {
        this.dominios = dominios;
        this.json = json;
    }

    @Override
    public Fornecedor getEntidade(Map<String, String> parametros) {
        Operacao operacao = Operacao.valueOf(parametros.get("operacao"));
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.codigo = ler(parametros, "code").toUpperCase(Locale.ROOT);
        if (operacao == Operacao.SALVAR) {
            fornecedor.codigo = "";
        }

        switch (operacao) {
            case SALVAR, ALTERAR -> {
                lerIdentificacao(parametros, fornecedor);
                fornecedor.email = ler(parametros, "email");
                fornecedor.telefone = new Telefone(dominios.telefones.buscar(ler(parametros, "phoneType")),
                        ler(parametros, "ddd"), ler(parametros, "phone"));
                fornecedor.endereco = new Endereco(dominios.logradouros.buscar(ler(parametros, "streetType")),
                        ler(parametros, "street"), ler(parametros, "number"), ler(parametros, "complement"),
                        ler(parametros, "neighborhood"), ler(parametros, "zipCode"), ler(parametros, "city"),
                        dominios.estados.buscar(ler(parametros, "state")), dominios.paises.buscar(ler(parametros, "country")));
            }
            case INATIVAR -> fornecedor.motivoStatus = new MotivoStatus(ler(parametros, "justification"),
                    dominios.inativacoes.buscar(ler(parametros, "category")), null);
            case ATIVAR -> fornecedor.motivoStatus = new MotivoStatus(ler(parametros, "justification"),
                    null, dominios.ativacoes.buscar(ler(parametros, "category")));
            case CONSULTAR -> {
                lerIdentificacao(parametros, fornecedor);
                String cnpj = ComplementarFormatacao.normalizarCnpj(fornecedor.cnpj);
                if (!fornecedor.cnpj.isEmpty() && !cnpj.matches("[A-Z0-9]{12}[0-9]{2}")) {
                    throw new IllegalArgumentException("Informe um CNPJ completo para consultar.");
                }
                fornecedor.cnpj = cnpj;

                String status = ler(parametros, "status");
                if (!status.isEmpty()) {
                    try {
                        fornecedor.status = StatusFornecedor.valueOf(status);
                    } catch (IllegalArgumentException erro) {
                        throw new IllegalArgumentException("Selecione um status válido.");
                    }
                }
                String sigla = ler(parametros, "state").toUpperCase(Locale.ROOT);
                Estado estado = sigla.isEmpty() ? null : new Estado(sigla, null);
                fornecedor.endereco = new Endereco(null, null, null, null, null, null,
                        ler(parametros, "city"), estado, null);
            }
        }
        return fornecedor;
    }

    private void lerIdentificacao(Map<String, String> parametros, Fornecedor fornecedor) {
        fornecedor.razaoSocial = ler(parametros, "company");
        fornecedor.nomeFantasia = ler(parametros, "fantasyName");
        fornecedor.cnpj = ler(parametros, "cnpj");
    }

    private String ler(Map<String, String> parametros, String chave) {
        return parametros.getOrDefault(chave, "").strip();
    }

    @Override
    public String getView(Resultado resultado) {
        var entidades = resultado.entidades().stream()
                .map(entidade -> dados((Fornecedor) entidade))
                .toList();
        return json.writeValueAsString(new Resposta(resultado.mensagem(), resultado.sucesso(), entidades));
    }

    String snapshot(Fornecedor fornecedor) {
        return fornecedor == null ? null : json.writeValueAsString(dados(fornecedor));
    }

    private DadosFornecedor dados(Fornecedor fornecedor) {
        Telefone telefone = fornecedor.telefone;
        Endereco endereco = fornecedor.endereco;
        return new DadosFornecedor(fornecedor.codigo, fornecedor.razaoSocial, fornecedor.nomeFantasia,
                fornecedor.cnpj, fornecedor.email, fornecedor.status, fornecedor.usuario, fornecedor.dtCadastro,
                telefone.tipo.chave, telefone.ddd, telefone.numero, endereco.tipoLogradouro.chave,
                endereco.logradouro, endereco.numero, endereco.complemento, endereco.bairro, endereco.cep,
                endereco.cidade, endereco.estado.sigla, endereco.pais.chave);
    }

    record DadosFornecedor(String code, String company, String fantasyName, String cnpj, String email,
                           StatusFornecedor status, String usuario, LocalDateTime dtCadastro, String phoneType,
                           String ddd, String phone, String streetType, String street, String number,
                           String complement, String neighborhood, String zipCode, String city,
                           String state, String country) {
    }

    record Resposta(String mensagem, boolean sucesso, List<DadosFornecedor> entidades) {
    }
}
