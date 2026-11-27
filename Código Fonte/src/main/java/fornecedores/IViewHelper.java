package fornecedores;

import java.util.Map;

interface IViewHelper {
    EntidadeDominio getEntidade(Map<String, String> parametros);
    String getView(Resultado resultado);
}
