package fornecedores;

abstract class AbstractCommand implements ICommand {
    protected final IFachada fachada;

    AbstractCommand(IFachada fachada) {
        this.fachada = fachada;
    }
}

final class SalvarCommand extends AbstractCommand {
    SalvarCommand(IFachada fachada) { super(fachada); }

    @Override
    public Resultado executar(EntidadeDominio entidade) {
        return fachada.salvar(entidade);
    }
}

final class AlterarCommand extends AbstractCommand {
    AlterarCommand(IFachada fachada) { super(fachada); }

    @Override
    public Resultado executar(EntidadeDominio entidade) {
        return fachada.alterar(entidade);
    }
}

final class InativarCommand extends AbstractCommand {
    InativarCommand(IFachada fachada) { super(fachada); }

    @Override
    public Resultado executar(EntidadeDominio entidade) {
        return fachada.inativar(entidade);
    }
}

final class AtivarCommand extends AbstractCommand {
    AtivarCommand(IFachada fachada) { super(fachada); }

    @Override
    public Resultado executar(EntidadeDominio entidade) {
        return fachada.ativar(entidade);
    }
}

final class ConsultarCommand extends AbstractCommand {
    ConsultarCommand(IFachada fachada) { super(fachada); }

    @Override
    public Resultado executar(EntidadeDominio entidade) {
        return fachada.consultar(entidade);
    }
}
