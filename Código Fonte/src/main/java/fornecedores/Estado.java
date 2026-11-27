package fornecedores;

final class Estado extends EntidadeDominio {
    final String sigla;
    final String nome;

    Estado(String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;
    }
}
