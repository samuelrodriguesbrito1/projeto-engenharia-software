package fornecedores;

import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
class Fachada implements IFachada {
    private final Map<Class<?>, IDAO<Fornecedor>> daos;
    private final Map<Class<?>, Map<Operacao, List<IStrategy>>> regrasAntes;
    private final Map<Class<?>, Map<Operacao, List<IStrategy>>> regrasDepois;
    private final TransactionTemplate transacao;
    private final ValidarCnpjExistente cnpjExistente;

    Fachada(FornecedorDAO fornecedorDAO, FornecedorLogDAO logDAO, FornecedorViewHelper viewHelper,
            PlatformTransactionManager gerenciador) {
        daos = Map.of(Fornecedor.class, fornecedorDAO);
        transacao = new TransactionTemplate(gerenciador);

        var formatar = new ComplementarFormatacao();
        var dadosObrigatorios = new ValidarDadosObrigatoriosFornecedor();
        var cnpj = new ValidarCnpj();
        var email = new ValidarEmail();
        cnpjExistente = new ValidarCnpjExistente(fornecedorDAO);
        var data = new ComplementarDtCadastro();
        var estadoAnterior = new CarregarEstadoAnterior(fornecedorDAO);
        var motivo = new ValidarMotivoStatus();
        var completar = new CompletarComEstadoAnterior();
        var ativo = new ComplementarStatus(StatusFornecedor.ATIVO);
        var inativo = new ComplementarStatus(StatusFornecedor.INATIVO);

        regrasAntes = Map.of(Fornecedor.class, Map.of(
                Operacao.SALVAR, List.of(formatar, dadosObrigatorios, cnpj, email, cnpjExistente, ativo, data),
                Operacao.ALTERAR, List.of(estadoAnterior, formatar, dadosObrigatorios, cnpj, email,
                        cnpjExistente, new ManterStatusAtual(), data),
                Operacao.INATIVAR, List.of(estadoAnterior, new ValidarStatusAtual(StatusFornecedor.ATIVO),
                        motivo, completar, inativo, data),
                Operacao.ATIVAR, List.of(estadoAnterior, new ValidarStatusAtual(StatusFornecedor.INATIVO),
                        motivo, completar, ativo, data)));
        regrasDepois = Map.of(Fornecedor.class, Map.of(
                Operacao.SALVAR, List.of(new GerarLog(logDAO, viewHelper, OperacaoLog.INCLUSAO)),
                Operacao.ALTERAR, List.of(new GerarLog(logDAO, viewHelper, OperacaoLog.ALTERACAO)),
                Operacao.INATIVAR, List.of(new GerarLog(logDAO, viewHelper, OperacaoLog.INATIVACAO)),
                Operacao.ATIVAR, List.of(new GerarLog(logDAO, viewHelper, OperacaoLog.ATIVACAO))));
    }

    @Override
    public Resultado salvar(EntidadeDominio entidade) {
        return executar(Operacao.SALVAR, entidade);
    }

    @Override
    public Resultado alterar(EntidadeDominio entidade) {
        return executar(Operacao.ALTERAR, entidade);
    }

    @Override
    public Resultado inativar(EntidadeDominio entidade) {
        return executar(Operacao.INATIVAR, entidade);
    }

    @Override
    public Resultado ativar(EntidadeDominio entidade) {
        return executar(Operacao.ATIVAR, entidade);
    }

    @Override
    public Resultado consultar(EntidadeDominio entidade) {
        return executar(Operacao.CONSULTAR, entidade);
    }

    private Resultado executar(Operacao operacao, EntidadeDominio entidade) {
        Fornecedor fornecedor = (Fornecedor) entidade;
        IDAO<Fornecedor> dao = daos.get(Fornecedor.class);
        if (operacao == Operacao.CONSULTAR) {
            return Resultado.sucesso("Consulta realizada.", dao.consultar(fornecedor));
        }

        try {
            return transacao.execute(status -> {
                String erro = processarRegras(regrasAntes, operacao, fornecedor);
                if (erro != null) {
                    return Resultado.erro(erro);
                }

                if (operacao == Operacao.SALVAR) {
                    dao.salvar(fornecedor);
                } else {
                    dao.alterar(fornecedor);
                }

                erro = processarRegras(regrasDepois, operacao, fornecedor);
                if (erro != null) {
                    status.setRollbackOnly();
                    return Resultado.erro(erro);
                }
                return Resultado.sucesso(mensagem(operacao, fornecedor.codigo), List.of(fornecedor));
            });
        } catch (DuplicateKeyException erro) {
            String mensagem = cnpjExistente.processar(fornecedor);
            if (mensagem == null) {
                throw erro;
            }
            return Resultado.erro(mensagem);
        }
    }

    private String processarRegras(Map<Class<?>, Map<Operacao, List<IStrategy>>> regras,
                                  Operacao operacao, EntidadeDominio entidade) {
        for (IStrategy regra : regras.get(entidade.getClass()).getOrDefault(operacao, List.of())) {
            String erro = regra.processar(entidade);
            if (erro != null) {
                return erro;
            }
        }
        return null;
    }

    private String mensagem(Operacao operacao, String codigo) {
        return switch (operacao) {
            case SALVAR -> "Fornecedor salvo com sucesso! Código " + codigo;
            case ALTERAR -> "Fornecedor alterado com sucesso!";
            case INATIVAR -> "Fornecedor inativado com sucesso!";
            case ATIVAR -> "Fornecedor ativado com sucesso!";
            case CONSULTAR -> "Consulta realizada.";
        };
    }
}
