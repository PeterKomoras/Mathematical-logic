import java.util.*;

public class SatSolver {
    public static class Result {
        public boolean sat;
        public Map<String,Boolean> valuation;
        public Result(boolean sat, Map<String, Variable> vars) {
            this.sat = sat;
            valuation = new HashMap<String, Boolean>();
            for (String k : vars.keySet()) {
                valuation.put(k, vars.get(k).val());
            }
        }
        public static Result sat(Map<String, Variable> vars) { return new Result(true, vars); }
        public static Result unsat() { return new Result(false, Collections.emptyMap()); }
    }

    public Result solve(Theory t) {
        Set<UnitClause> units = new HashSet<>();

        // Step 1: Initialize watched literals and unit clauses
        if (!t.initWatched(units)) { return Result.unsat(); }

        // Step 2: Unit propagate
        while (!units.isEmpty()) {
            UnitClause unit = units.iterator().next();
            units.remove(unit);
            if (!t.setLiteral(unit.unsetLiteral(), units)) return Result.unsat();
        }


        return search(t);
    }

    private Result search(Theory t) {
        Set<UnitClause> units = new HashSet<>();

        // If all variables are assigned, check if formula is satisfied
        if (t.nAssigned() == t.vars().size()) {
            return t.cnf().stream().allMatch(clause -> clause.stream().anyMatch(Literal::isTrue))
                    ? Result.sat(t.vars()) : Result.unsat();
        }

        // Choose a variable
        Variable variable = null;
        for (Variable v : t.vars().values()) {
            if (!v.isSet()) {
                variable = v;
                break;
            }
        }


        // Save the current number of assigned literals
        int back = t.nAssigned();

        // Try both true and false assignments
        for (boolean value : new boolean[]{true, false}) {
            if (t.setLiteral(variable.lit(value), units)) {
                boolean fail = false;

                while (!units.isEmpty()) {
                    UnitClause unit = units.iterator().next();
                    units.remove(unit);
                    if (!t.setLiteral(unit.unsetLiteral(), units)) {
                        units.clear();
                        fail = true;
                        break;
                    }
                }

                if (!fail) {
                    Result result = search(t);
                    if (result.sat) {
                        return result;
                    }
                }
            }
            // Undo assignments
            while (t.nAssigned() > back) {
                t.unsetLiteral();
            }
        }

        return Result.unsat();
    }

}