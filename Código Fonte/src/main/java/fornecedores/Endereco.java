package fornecedores;

final class Endereco extends EntidadeDominio {
    final TipoLogradouro tipoLogradouro;
    final String logradouro;
    final String numero;
    final String complemento;
    final String bairro;
    final String cep;
    final String cidade;
    final Estado estado;
    final Pais pais;

    Endereco(TipoLogradouro tipoLogradouro, String logradouro, String numero, String complemento,
             String bairro, String cep, String cidade, Estado estado, Pais pais) {
        this.tipoLogradouro = tipoLogradouro;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
    }
}
