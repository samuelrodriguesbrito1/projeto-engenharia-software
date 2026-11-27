export const search = `

<header>Cadastro de Fornecedores</header>
<main>
    <section class="search">
        <form>

            <div class="input">
                <label for="code">Código:</label>
                <input type="text" id="code" name="code" maxlength="10">
            </div>

            <div class="input">
                <label for="company">Razão Social:</label>
                <input type="text" id="company" name="company" maxlength="100">
            </div>

            <div class="input">
                <label for="fantasyName">Nome fantasia:</label>
                <input type="text" id="fantasyName" name="fantasyName" maxlength="60">
            </div>

            <div class="input">
                <label for="cnpj">CNPJ:</label>
                <input type="text" id="cnpj" name="cnpj" maxlength="18">
            </div>

            <div class="input">
                <label for="city">Cidade:</label>
                <input type="text" id="city" name="city" maxlength="60">

                <label for="uf">Estado:</label>
                <select name="state" id="uf">
                    <option value="">Todos</option>
                </select>

            </div>

            <div class="input-search">
                <div>
                    <label for="status">Status:</label>
                    <select id="status" name="status">
                        <option value="">Todos</option>
                        <option value="ATIVO">Ativo</option>
                        <option value="INATIVO">Inativo</option>
                    </select>
                </div>
                <button class="search-button" type="submit" disabled>Buscar</button>
                <button type="button" data-acao="novo" disabled>Novo</button>
            </div>
        </form>

        <p data-resultado-vazio role="status" hidden>Nenhum fornecedor encontrado.</p>
    </section>
    <section id="user">
        <fieldset>
            <legend>Usuário</legend>
            <p data-usuario></p>
        </fieldset>
    </section>
</main>
<footer></footer>

`;
