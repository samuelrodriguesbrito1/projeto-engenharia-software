package fornecedores;

import java.util.Locale;

final class CarregarEstadoAnterior extends RegraFornecedor {
    private final FornecedorDAO dao;

    CarregarEstadoAnterior(FornecedorDAO dao) {
        this.dao = dao;
    }

    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        if (fornecedor.codigo.isBlank()) {
            return "Informe o código do fornecedor.";
        }
        fornecedor.estadoAnterior = dao.buscarParaAlterar(fornecedor.codigo);
        return fornecedor.estadoAnterior == null ? "Fornecedor não encontrado." : null;
    }
}

final class ValidarStatusAtual extends RegraFornecedor {
    private final StatusFornecedor esperado;

    ValidarStatusAtual(StatusFornecedor esperado) {
        this.esperado = esperado;
    }

    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        if (fornecedor.estadoAnterior.status == esperado) {
            return null;
        }
        String status = fornecedor.estadoAnterior.status.name().toLowerCase(Locale.ROOT);
        return "O fornecedor já está " + status + ".";
    }
}

final class ValidarMotivoStatus extends RegraFornecedor {
    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        MotivoStatus motivo = fornecedor.motivoStatus;
        boolean temCategoria = (motivo.categoriaAtivacao() != null) != (motivo.categoriaInativacao() != null);
        if (!temCategoria && motivo.justificativa().isBlank()) {
            return "Selecione uma categoria e informe a justificativa.";
        }
        if (!temCategoria) {
            return "Selecione uma categoria válida.";
        }
        if (motivo.justificativa().isBlank()) {
            return "Informe a justificativa.";
        }
        return motivo.justificativa().length() > 255 ? "A justificativa pode ter até 255 caracteres." : null;
    }
}

final class CompletarComEstadoAnterior extends RegraFornecedor {
    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        Fornecedor anterior = fornecedor.estadoAnterior;
        fornecedor.razaoSocial = anterior.razaoSocial;
        fornecedor.nomeFantasia = anterior.nomeFantasia;
        fornecedor.cnpj = anterior.cnpj;
        fornecedor.email = anterior.email;
        fornecedor.telefone = anterior.telefone;
        fornecedor.endereco = anterior.endereco;
        return null;
    }
}

final class ManterStatusAtual extends RegraFornecedor {
    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        fornecedor.status = fornecedor.estadoAnterior.status;
        return null;
    }
}

final class ComplementarStatus extends RegraFornecedor {
    private final StatusFornecedor status;

    ComplementarStatus(StatusFornecedor status) {
        this.status = status;
    }

    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        fornecedor.status = status;
        return null;
    }
}
