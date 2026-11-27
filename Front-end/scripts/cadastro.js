export const cadastro = `
<header>Cadastro de Fornecedores&nbsp;&nbsp;|&nbsp;&nbsp;Novo Fornecedor</header>
<main class="cadastro-main">
    <form class="cadastro-form">
        <fieldset>
            <legend>Dados do Fornecedor</legend>
            <div class="cadastro-linha">
                <label for="code">Código:</label>
                <input class="campo-codigo" type="text" id="code" name="code" value="(gerado)" disabled>

                <label for="cnpj">CNPJ:</label>
                <input class="campo-cnpj" type="text" id="cnpj" name="cnpj" maxlength="18" required>
            </div>
            <div class="cadastro-linha">
                <label for="company">Razão Social:</label>
                <input class="campo-razao" type="text" id="company" name="company" maxlength="100" required>

                <label for="fantasyName">Nome Fantasia:</label>
                <input class="campo-fantasia" type="text" id="fantasyName" name="fantasyName" maxlength="60" required>
            </div>
            <div class="cadastro-linha">
                <label for="email">E-mail:</label>
                <input class="campo-email" type="email" id="email" name="email" maxlength="100" required>
            </div>
            <div class="cadastro-linha" data-status hidden>
                <label for="status">Status:</label>
                <input type="text" id="status" name="status" disabled>
            </div>
        </fieldset>

        <fieldset>
            <legend>Telefone</legend>
            <div class="cadastro-linha">
                <label for="phoneType">Tipo:</label>
                <select class="campo-tipo-telefone" id="phoneType" name="phoneType" required></select>

                <label for="ddd">DDD:</label>
                <input class="campo-ddd" type="text" id="ddd" name="ddd" maxlength="2" inputmode="numeric" required>

                <label for="phone">Número:</label>
                <input class="campo-telefone" type="text" id="phone" name="phone" maxlength="14" inputmode="tel" required>
            </div>
        </fieldset>

        <fieldset>
            <legend>Endereço</legend>
            <div class="cadastro-linha">
                <label for="streetType">Tipo Logradouro:</label>
                <select class="campo-tipo-logradouro" id="streetType" name="streetType" required></select>

                <label for="street">Logradouro:</label>
                <input class="campo-logradouro" type="text" id="street" name="street" maxlength="100" required>
            </div>
            <div class="cadastro-linha">
                <label for="number">Número:</label>
                <input class="campo-numero" type="text" id="number" name="number" maxlength="10" required>

                <label for="complement">Complemento:</label>
                <input class="campo-complemento" type="text" id="complement" name="complement" maxlength="60">

                <label for="neighborhood">Bairro:</label>
                <input class="campo-bairro" type="text" id="neighborhood" name="neighborhood" maxlength="60" required>
            </div>
            <div class="cadastro-linha">
                <label for="zipCode">CEP:</label>
                <input class="campo-cep" type="text" id="zipCode" name="zipCode" maxlength="9" inputmode="numeric" required>

                <label for="city">Cidade:</label>
                <input class="campo-cidade" type="text" id="city" name="city" maxlength="60" required>

                <label for="state">Estado:</label>
                <select class="campo-estado" id="state" name="state" required></select>
            </div>
            <div class="cadastro-linha">
                <label for="country">País:</label>
                <select class="campo-pais" id="country" name="country" required></select>
            </div>
        </fieldset>

        <div class="cadastro-acoes">
            <button type="submit" disabled>Salvar</button>
            <button type="button">Cancelar</button>
        </div>
    </form>

    <section class="cadastro-usuario">
        <fieldset>
            <legend>Usuário</legend>
            <p data-usuario></p>
        </fieldset>
    </section>
</main>
<footer></footer>
`;
