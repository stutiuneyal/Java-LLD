package spreadsheet.formula;

import java.util.List;
import java.util.Set;

public class CompiledFormula {

    private final List<Token> rpn;
    private final Set<String> references;

    public CompiledFormula(List<Token> rpn, Set<String> references) {
        this.rpn = rpn;
        this.references = references;
    }

    public List<Token> getRpn() {
        return rpn;
    }

    public Set<String> getReferences() {
        return references;
    }

}
