# Google Sheets / Excel (Cycle Detection + Update Propagation) — LLD (Uber SDE-1)

## Objective
Design an in-memory spreadsheet engine (like Google Sheets / Excel) that supports:
1. Setting a **constant numeric value** in a cell
2. Setting a **formula** in a cell that can reference other cells
3. Getting the **evaluated numeric value** of any cell
4. **Propagating updates** to dependent cells when a referenced cell changes
5. Detecting and rejecting **cyclic dependencies** (direct or indirect)

---

## Cell Addressing
- Each cell is identified by a string like:
  - `A1`, `B2`, `Z100`
- (Optional extension) Support multi-letter columns like `AA1`

---

## Supported Formulas
A formula is a string expression containing:
- Numbers: `10`, `3.14`, `.5`
- Operators: `+`, `-`, `*`, `/`
- Parentheses: `(`, `)`
- Cell references: `A1`, `B2`, etc.

Examples:
- `A1 + 10`
- `(A1 + B1) * 2`
- `A1 / (B2 - 3)`

---

## Required APIs
Implement at least the following:
- `setValue(cellId, number)`
- `setFormula(cellId, formulaString)`
- `getValue(cellId) -> number`

---

## Functional Requirements
1. If a cell has a constant value, `getValue` returns that value.
2. If a cell has a formula, `getValue` returns the evaluated result based on referenced cells.
3. When a cell value/formula is updated, all cells that depend on it must reflect the change.
4. When setting a formula:
   - If it introduces a cycle (e.g., `A1 = B1 + 1`, `B1 = A1 + 1`), reject it.
5. If a referenced cell is not set yet, treat it as `0` (or define a consistent behavior).

---

## Constraints & Non-goals (for this interview version)
- No need for UI.
- No need for persistence.
- No need for built-in functions like `SUM`, `MAX` (mention as extension).
- No need for ranges like `A1:A10` (mention as extension).

---

## Example Scenario
1. `setValue("A1", 10)`
2. `setValue("B1", 5)`
3. `setFormula("C1", "A1 + B1 * 2")`
   - `getValue("C1")` => `20`
4. `setValue("B1", 7)`
   - `getValue("C1")` => `24` (must update via propagation)
5. `setFormula("A1", "C1 + 1")`
   - Must be rejected if it creates a cycle through dependencies