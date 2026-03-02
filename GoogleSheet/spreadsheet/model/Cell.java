package spreadsheet.model;

import spreadsheet.formula.CompiledFormula;

public class Cell {

    private final String id;

    private Double constValue;
    private CompiledFormula folmula;

    private boolean dirty;
    private double cachedValue;

    public Cell(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Double getConstValue() {
        return constValue;
    }

    public void setConstValue(Double constValue) {
        this.constValue = constValue;
    }

    public CompiledFormula getFolmula() {
        return folmula;
    }

    public void setFolmula(CompiledFormula folmula) {
        this.folmula = folmula;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public double getCachedValue() {
        return cachedValue;
    }

    public void setCachedValue(double cachedValue) {
        this.cachedValue = cachedValue;
    }

}