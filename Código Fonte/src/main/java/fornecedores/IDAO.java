package fornecedores;

import java.util.List;

interface IDAO<T extends EntidadeDominio> {
    default void salvar(T entidade) {
        throw new UnsupportedOperationException();
    }

    default void alterar(T entidade) {
        throw new UnsupportedOperationException();
    }

    default void excluir(T entidade) {
        throw new UnsupportedOperationException();
    }

    default List<T> consultar(T filtro) {
        throw new UnsupportedOperationException();
    }
}
