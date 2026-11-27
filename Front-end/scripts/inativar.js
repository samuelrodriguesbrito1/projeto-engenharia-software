export const inativar = `
<header>Cadastro de Fornecedores&nbsp;&nbsp;|&nbsp;&nbsp;Inativar Fornecedor</header>
<main class="inativar-main">
    <section class="inativar-conteudo">
        <p class="inativar-fornecedor"><strong>Fornecedor</strong></p>

        <form>
            <div class="inativar-categoria">
                <label for="category">Categoria:<span class="obrigatorio" aria-hidden="true">*</span></label>
                <select id="category" name="category" required></select>
            </div>

            <div class="inativar-justificativa">
                <label for="justification">Justificativa:<span class="obrigatorio" aria-hidden="true">*</span></label>
                <textarea id="justification" name="justification" maxlength="255" required></textarea>
            </div>

            <p class="legenda-obrigatorio"><span class="obrigatorio" aria-hidden="true">*</span> Campos obrigatórios</p>

            <div class="inativar-acoes">
                <button type="submit" disabled>Confirmar</button>
                <button type="button">Cancelar</button>
            </div>
        </form>
    </section>

    <section class="inativar-usuario">
        <fieldset>
            <legend>Usuário</legend>
            <p data-usuario></p>
        </fieldset>
    </section>
</main>
<footer></footer>
`;
