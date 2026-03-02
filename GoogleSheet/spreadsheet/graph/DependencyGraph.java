package spreadsheet.graph;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import spreadsheet.model.Cell;

public class DependencyGraph {

    // cell -> dependencies (cells it reads)
    private final Map<String, Set<String>> deps = new HashMap<>();

    // cell -> dependents (cell that reads it)
    private final Map<String, Set<String>> rev = new HashMap<>();

    public Set<String> getDepedencies(String cell) {
        return deps.getOrDefault(cell, Collections.emptySet());
    }

    public void updateDependencies(String cell, Set<String> newDeps) {
        cell = cell.trim().toUpperCase();
        newDeps = normalizeAll(newDeps);

        Set<String> oldDeps = deps.getOrDefault(cell, Collections.emptySet());

        // remove reverse edges for the old deps
        for (String d : oldDeps) {
            Set<String> dependents = rev.get(d);
            if (dependents != null) {
                dependents.remove(cell);
                if (dependents.isEmpty()) {
                    rev.remove(d);
                }
            }
        }

        // add reverse edges for new deps
        for (String s : newDeps) {
            rev.computeIfAbsent(s, k -> new HashSet<>()).add(cell);
        }

        if (newDeps.isEmpty()) {
            deps.remove(cell);
        } else {
            deps.put(cell, new HashSet<>(newDeps));
        }
    }

    public void markDependentsDirty(String start, Map<String, Cell> cells) {

        start = start.trim().toUpperCase();

        Deque<String> q = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();

        q.add(start);
        seen.add(start);

        while (!q.isEmpty()) {

            String cur = q.poll();

            for (String next : rev.getOrDefault(cur, Collections.emptySet())) {
                if (!seen.contains(next)) {
                    seen.add(next);
                    Cell c = cells.computeIfAbsent(next, Cell::new);
                    c.setDirty(true);
                    q.add(next);
                }
            }
        }
    }

    public boolean hasCycle(Set<String> allNodes) {

        Map<String, Integer> color = new HashMap<>();

        for (String n : allNodes) {
            if (color.getOrDefault(n, 0) == 0) { // unvisited
                if (dfsCycle(n, color)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dfsCycle(String node, Map<String, Integer> color) {
        int c = color.getOrDefault(node, 0);

        if (c == 1) {
            return true;
        }
        if (c == 2) {
            return false;
        }

        color.put(node, 1);

        for (String s : deps.getOrDefault(node, Collections.emptySet())) {
            if (dfsCycle(s, color)) {
                return true;
            }
        }

        color.put(node, 2);
        return false;
    }

    private Set<String> normalizeAll(Set<String> s) {
        Set<String> out = new HashSet<>();
        for (String x : s) {
            out.add(x.trim().toUpperCase());
        }
        return out;
    }
}
