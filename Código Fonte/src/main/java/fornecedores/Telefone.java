package fornecedores;

final class Telefone extends EntidadeDominio {
    final TipoTelefone tipo;
    final String ddd;
    final String numero;

    Telefone(TipoTelefone tipo, String ddd, String numero) {
        this.tipo = tipo;
        this.ddd = ddd;
        this.numero = numero;
    }
}
