package spreadsheet;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import spreadsheet.formula.CompiledFormula;
import spreadsheet.formula.FormulaEngine;
import spreadsheet.graph.DependencyGraph;
import spreadsheet.model.Cell;

public class Spreadsheet {

    private final Map<String, Cell> cells = new HashMap<>();
    private final DependencyGraph graph = new DependencyGraph();
    private final FormulaEngine formulaEngine = new FormulaEngine();

    public void setValue(String cellId, double value) {
        cellId = cellId.trim().toUpperCase();

        Cell cell = cells.computeIfAbsent(cellId, Cell::new);

        // Remove dependencies if any
        graph.updateDependencies(cellId, Collections.emptySet());

        cell.setFolmula(null);
        cell.setConstValue(value);

        cell.setCachedValue(value);
        cell.setDirty(false);

        graph.markDependentsDirty(cellId, cells);
    }

    public void setFormula(String cellId, String formulaStr) {
        cellId = cellId.trim().toUpperCase();

        Cell cell = cells.computeIfAbsent(cellId, Cell::new);

        CompiledFormula formula = formulaEngine.compile(formulaStr);

        // Save the old deps so we can rollback if cycle
        Set<String> oldDeps = new HashSet<>(graph.getDepedencies(cellId));

        graph.updateDependencies(cellId, formula.getReferences());

        if (hasCycle()) {
            graph.updateDependencies(cellId, oldDeps); // rollback
            throw new IllegalArgumentException("Cycle detected while setting formula for " + cellId);
        }

        cell.setFolmula(formula);
        cell.setConstValue(null);
        cell.setDirty(true);

        graph.markDependentsDirty(cellId, cells);
    }

    public double getValue(String cellId) {
        return evalCell(cellId.trim().toUpperCase(), new HashSet<>());
    }

    public double evalCell(String cellId, Set<String> stack){

        Cell cell = cells.computeIfAbsent(cellId, Cell::new);

        if(!cell.isDirty()){
            return cell.getCachedValue();
        }

        if(cell.getFolmula() == null){
            double v = (cell.getConstValue() == null)?0d : cell.getConstValue();
            cell.setCachedValue(v);
            cell.setDirty(false);
            return v;
        }

        if(stack.contains(cellId)){
            throw new IllegalStateException("Cycle detected during evaluation at " + cellId);
        }

        stack.add(cellId);

        double result = formulaEngine.evaluate(cellId, cell.getFolmula(), this, stack);

        stack.remove(cellId);

        cell.setCachedValue(result);
        cell.setDirty(false);

        return result;
    }

    private boolean hasCycle() {
        Set<String> nodes = new HashSet<>(cells.keySet());
        return graph.hasCycle(nodes);
    }
}
