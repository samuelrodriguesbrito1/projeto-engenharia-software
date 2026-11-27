package fornecedores;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class ComplementarFormatacao extends RegraFornecedor {
    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        fornecedor.cnpj = normalizarCnpj(fornecedor.cnpj);
        Telefone t = fornecedor.telefone;
        fornecedor.telefone = new Telefone(t.tipo, t.ddd, t.numero.replaceAll("[() .-]", ""));
        Endereco e = fornecedor.endereco;
        fornecedor.endereco = new Endereco(e.tipoLogradouro, e.logradouro, e.numero, e.complemento, e.bairro,
                e.cep.replace("-", ""), e.cidade, e.estado, e.pais);
        return null;
    }

    static String normalizarCnpj(String cnpj) {
        return cnpj.toUpperCase(Locale.ROOT).replaceAll("[./-]", "");
    }
}

final class ValidarDadosObrigatoriosFornecedor extends RegraFornecedor {
    private final ValidarTelefone validarTelefone = new ValidarTelefone();
    private final ValidarEndereco validarEndereco = new ValidarEndereco();

    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        List<String> ausentes = new ArrayList<>();
        if (fornecedor.razaoSocial.isBlank()) ausentes.add("Razão social");
        if (fornecedor.nomeFantasia.isBlank()) ausentes.add("Nome fantasia");
        if (fornecedor.cnpj.isBlank()) ausentes.add("CNPJ");
        if (fornecedor.email.isBlank()) ausentes.add("E-mail");

        Telefone telefone = fornecedor.telefone;
        if (telefone.tipo == null) ausentes.add("Tipo de telefone");
        if (telefone.ddd.isBlank()) ausentes.add("DDD");
        if (telefone.numero.isBlank()) ausentes.add("Número de telefone");

        Endereco endereco = fornecedor.endereco;
        if (endereco.tipoLogradouro == null) ausentes.add("Tipo de logradouro");
        if (endereco.logradouro.isBlank()) ausentes.add("Logradouro");
        if (endereco.numero.isBlank()) ausentes.add("Número do endereço");
        if (endereco.bairro.isBlank()) ausentes.add("Bairro");
        if (endereco.cep.isBlank()) ausentes.add("CEP");
        if (endereco.cidade.isBlank()) ausentes.add("Cidade");
        if (endereco.estado == null) ausentes.add("Estado");
        if (endereco.pais == null) ausentes.add("País");
        if (!ausentes.isEmpty()) {
            return "Preencha os campos obrigatórios: " + String.join(", ", ausentes) + ".";
        }
        if (fornecedor.razaoSocial.length() > 100 || fornecedor.nomeFantasia.length() > 60) {
            return "A razão social pode ter até 100 caracteres e o nome fantasia, até 60.";
        }
        String erroTelefone = validarTelefone.processar(fornecedor.telefone);
        return erroTelefone == null ? validarEndereco.processar(fornecedor.endereco) : erroTelefone;
    }
}

final class ValidarTelefone implements IStrategy {
    @Override
    public String processar(EntidadeDominio entidade) {
        Telefone telefone = (Telefone) entidade;
        if (telefone.tipo == null || !telefone.ddd.matches("[1-9][0-9]") || !telefone.numero.matches("[0-9]{8,9}")) {
            return "Informe o tipo de telefone, um DDD de 2 dígitos e um número de 8 ou 9 dígitos.";
        }
        return null;
    }
}

final class ValidarEndereco implements IStrategy {
    @Override
    public String processar(EntidadeDominio entidade) {
        Endereco endereco = (Endereco) entidade;
        if (!endereco.cep.matches("[0-9]{8}")) {
            return "Informe um CEP com 8 dígitos.";
        }
        if (endereco.logradouro.length() > 100 || endereco.numero.length() > 10
                || endereco.complemento.length() > 60 || endereco.bairro.length() > 60
                || endereco.cidade.length() > 60) {
            return "Um dos campos do endereço excede o limite de caracteres.";
        }
        return null;
    }
}

final class ValidarCnpj extends RegraFornecedor {
    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        return fornecedor.cnpj.matches("[A-Z0-9]{12}[0-9]{2}") ? null : "Formato de CNPJ inválido.";
    }
}

final class ValidarEmail extends RegraFornecedor {
    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        if (fornecedor.email.length() > 100 || !fornecedor.email.matches("[^@\\s]+@[^@\\s.]+(?:\\.[^@\\s.]+)+")) {
            return "Informe um e-mail válido de até 100 caracteres.";
        }
        return null;
    }
}

final class ValidarCnpjExistente extends RegraFornecedor {
    private final IDAO<Fornecedor> dao;

    ValidarCnpjExistente(IDAO<Fornecedor> dao) {
        this.dao = dao;
    }

    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        Fornecedor filtro = new Fornecedor();
        filtro.cnpj = fornecedor.cnpj;
        return dao.consultar(filtro).stream()
                .filter(outro -> !outro.codigo.equals(fornecedor.codigo))
                .findFirst()
                .map(outro -> "CNPJ já cadastrado para o fornecedor " + outro.codigo + ".")
                .orElse(null);
    }
}

final class ComplementarDtCadastro extends RegraFornecedor {
    @Override
    protected String processarFornecedor(Fornecedor fornecedor) {
        fornecedor.dtCadastro = fornecedor.estadoAnterior == null
                ? LocalDateTime.now().truncatedTo(ChronoUnit.MICROS) : fornecedor.estadoAnterior.dtCadastro;
        return null;
    }
}
