import java.util.*;
import java.util.stream.Collectors;

class Term {
    private Set<String> variables = new HashSet<>();
    private Set<String> constance = new HashSet<>();
    private Set<String> functions = new HashSet<>();

    private final String name;
    public Term(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public Set<String> variables() {
        return variables;
    }

    public Set<String> constants() {
        return constance;
    }

    public Set<String> functions() {
        return functions;
    }

    public <D> D eval(Structure<D> m, Map<String, D> e) {
//        throw new RuntimeException("Not implemented");
        return null;
    }

    public Term substitute(String v, Term t) {
        return null;
    }
}

class Variable extends Term {
    Variable(String name) {
        super(name);
    }

//    @Override
//    public Set<String> functions() {
//        return Collections.singleton(name());
//    }

    public Set<String> variables() {
        return Collections.singleton(name());
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof Variable))
            return false;
        return (((Variable) other).name() == this.name());
    }

    public <D> D eval(Structure<D> m, Map<String, D> e) {
        return e.get(name());
    }

    @Override
    public Term substitute(String var, Term t) {
        if (var == this.name()) {
            if (t instanceof Variable) return new Variable(t.name());
            if (t instanceof Constant) return new Constant(t.name());
            return new FunctionApplication(t.name(), ((FunctionApplication) t).subts());

        }
        return new Variable(this.name());
    }
}

class Constant extends Term {
    Constant(String name) {
        super(name);
    }

    public Set<String> constants() {
        return Collections.singleton(name());
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof Constant))
            return false;
        return (((Constant) other).name() == this.name());
    }

    public <D> D eval(Structure<D> m, Map<String, D> e) {
        return m.iC(name());
    }

    @Override
    public Term substitute(String var, Term t) {
        return new Constant(this.name());
    }
}

class FunctionApplication extends Term {
    private final List<Term> subts;
    FunctionApplication(String name, List<Term> subts) {
        super(name);
        this.subts = subts;
    }

    public List<Term> subts() {
        List<Term> toReturn = new ArrayList<>();
        for (Term t : subts) {
            if (t instanceof Variable) toReturn.add(new Variable(t.name()));
            else if (t instanceof Constant) toReturn.add(new Constant(t.name()));
            else toReturn.add(new FunctionApplication(t.name(), ((FunctionApplication) t).subts()));
        }

        return toReturn;
    }

    @Override
    public String toString() {
        return name() + "(" + subts().stream().map(Term::toString).collect(Collectors.joining(",")) + ")";
    }

    @Override
    public Set<String> variables() {
        return subts().stream()
                .map(Term::variables)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> constants() {
        return subts().stream()
                .map(Term::constants)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
    }

    @Override
    public Set<String> functions() {
        Set<String> res =  subts().stream()
                    .map(Term::functions)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
        res.add(this.name());
        return res;
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof FunctionApplication))
            return false;
        FunctionApplication otherApp = ((FunctionApplication) other);
        if (otherApp.name() != this.name()) return false;
        return this.subts.equals(otherApp.subts);
    }

    @Override
    public <D> D eval(Structure<D> m, Map<String, D> e) {
        return m.iF(this.name())
                .get(
                        this.subts().stream()
                                .map(term -> term.eval(m,e))
                                .collect(Collectors.toList())
                );
    }

    @Override
    public Term substitute(String var, Term t) {
        List<Term> newSubts = this.subts().stream()
                .map(term -> term.substitute(var, t))
                .collect(Collectors.toList());
        return new FunctionApplication(this.name(), newSubts);
    }

}


class Formula {
    private final List<Formula> subfs;
    public Formula(List<Formula> subfs) {
        this.subfs = subfs;
    }

    public List<Formula> subfs() {
        return this.subfs;
    }

    @Override
    public String toString() {
        return this.subfs().stream()
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return ((Formula) other).subfs().equals(this.subfs());

    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public Set<String> variables() {
        return this.subfs().stream()
                .map(Formula::variables)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Set<String> constants() {
        return this.subfs().stream()
                .map(Formula::constants)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Set<String> functions() {
        return this.subfs().stream()
                .map(Formula::functions)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Set<String> predicates() {
        return this.subfs().stream()
                .map(Formula::predicates)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        return true;
    }

    public Set<String> freeVariables() {
        return this.subfs().stream()
                .map(Formula::freeVariables)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Formula substitute(String var, Term t) throws NotApplicableException {
        return null;
    }
}

class AtomicFormula extends Formula {
    private final List<Term> subts;
    public AtomicFormula(List<Term> subts) {
        super(Collections.emptyList());
        this.subts = subts;
    }

    public List<Term> subts() {
        return this.subts;
    }


    public Set<String> variables() {
        return this.subts().stream()
                .map(Term::variables)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Set<String> constants() {
        return this.subts().stream()
                .map(Term::constants)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Set<String> functions() {
        return this.subts().stream()
                .map(Term::functions)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> freeVariables() { return this.variables(); }

}

class PredicateAtom extends AtomicFormula {
    private final String name;
    public PredicateAtom(String name, List<Term> subts) {
        super(subts);
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name() + "(" + subts().stream().map(Term::toString).collect(Collectors.joining(",")) + ")";
    }

    @Override
    public Set<String> predicates() {
        return Collections.singleton(name);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (!(other instanceof PredicateAtom))
            return false;
        return ((PredicateAtom) other).subts().equals(this.subts()) && name() == ((PredicateAtom) other).name();
    }

    @Override
    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        List<D> values = subts().stream()
                .map(term -> term.eval(m,e))
                .collect(Collectors.toList());

        return m.iP(this.name())
                .contains(values);
    }

    @Override
    public Formula substitute(String var, Term t) throws NotApplicableException {
        return new PredicateAtom(
                this.name(),
                subts().stream()
                .map(sf -> sf.substitute(var, t))
                .collect(Collectors.toList())
        );
    }


}

class EqualityAtom extends AtomicFormula {
    EqualityAtom(Term leftTerm, Term rightTerm) {
        super(Arrays.asList(leftTerm, rightTerm));
    }
    Term leftTerm() {
        return this.subts().get(0);
    }
    Term rightTerm() {
        return this.subts().get(1);
    }

    @Override
    public String toString() {
        return leftTerm() + "=" + rightTerm();
    }

    @Override
    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        return this.leftTerm().eval(m,e) == this.rightTerm().eval(m,e);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (!(other instanceof EqualityAtom))
            return false;
        return ((EqualityAtom) other).subts().equals(this.subts());
    }

    @Override
    public Formula substitute(String var, Term t) throws NotApplicableException {
        return new EqualityAtom(
                this.leftTerm().substitute(var,t),
                this.rightTerm().substitute(var,t));
    }

}

class Negation extends Formula {
    Negation(Formula originalFormula) {
        super(Collections.singletonList(originalFormula));
    }

    @Override
    public Formula substitute(String var, Term t) throws NotApplicableException {
        return new Negation(
                this.originalFormula().substitute(var,t)
        );
    }

    Formula originalFormula() {
        return this.subfs().get(0);
    }

    @Override
    public String toString() {
        return "-" + this.originalFormula();
    }

    @Override
    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        return !this.originalFormula().isSatisfied(m,e);
    }


}

class Disjunction extends Formula {
    Disjunction(List<Formula> disjuncts) {
        super(disjuncts);
    }

    @Override
    public Formula substitute(String var, Term t) throws NotApplicableException {
        return new Disjunction(
                this.subfs().stream().map(f -> {
                    try {
                        return f.substitute(var,t);
                    } catch (NotApplicableException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        return "(" +
                this.subfs().stream()
                .map(Objects::toString)
                .collect(Collectors.joining("|"))
                + ")";
    }

    @Override
    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        return this.subfs().stream()
                .anyMatch(f -> f.isSatisfied(m,e));
    }
}

class Conjunction extends Formula {
    Conjunction(List<Formula> conjuncts) {
        super(conjuncts);
    }

    @Override
    public Formula substitute(String var, Term t) throws NotApplicableException {
        return new Conjunction(
                this.subfs().stream().map(f -> {
                    try {
                        return f.substitute(var,t);
                    } catch (NotApplicableException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        return "(" +
                this.subfs().stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining("&"))
                + ")";
    }

    @Override
    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        return this.subfs().stream()
                .allMatch(f -> f.isSatisfied(m,e));
    }
}

class BinaryFormula extends Formula {
    private final String connective;
    BinaryFormula(Formula leftSide, Formula rightSide, String connective) {
        super(Arrays.asList(leftSide, rightSide));
        this.connective = connective;
    }

    public Formula leftSide() {
        return this.subfs().get(0);
    }

    public Formula rightSide() {
        return this.subfs().get(1);
    }

    @Override
    public String toString() {
        return "(" + this.leftSide() + connective + this.rightSide() + ")";
    }
}

class Implication extends BinaryFormula {
    Implication(Formula leftSide, Formula rightSide) {
        super(leftSide, rightSide, "->");
    }

    @Override
    public Formula substitute(String var, Term t) throws NotApplicableException {
        return new Implication(
                this.leftSide().substitute(var, t),
                this.rightSide().substitute(var,t)
        );
    }

    @Override
    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        return !(this.leftSide().isSatisfied(m,e) && !this.rightSide().isSatisfied(m,e));
    }
}

class Equivalence extends BinaryFormula {
    Equivalence(Formula leftSide, Formula rightSide) {
        super(leftSide, rightSide, "<->");
    }

    @Override
    public Formula substitute(String var, Term t) throws NotApplicableException {
        return new Equivalence(
                this.leftSide().substitute(var, t),
                this.rightSide().substitute(var,t)
        );
    }

    @Override
    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        return this.leftSide().isSatisfied(m,e) == this.rightSide().isSatisfied(m,e);
    }
}

class QuantifiedFormula extends Formula {
    private String quantifier, qvar;
    QuantifiedFormula(String quantifier, String qvar, Formula originalFormula) {
        super(Arrays.asList(originalFormula));
        this.quantifier = quantifier;
        this.qvar = qvar;
    }

    public Formula originalFormula() {
        return this.subfs().get(0);
    }

    public String qvar() {
        return qvar;
    }

    @Override
    public String toString() {
        return this.quantifier + this.qvar + " " + this.originalFormula();
    }

    @Override
    public Set<String> variables() {
        Set<String> res = super.variables();
        res.add(qvar);
        return res;
    }

    @Override
    public Set<String> freeVariables() {
        Set<String> fvs = super.freeVariables();
        fvs.remove(qvar());
        return fvs;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) return false;
        return ((QuantifiedFormula) other).qvar().equals(this.qvar()) && ((QuantifiedFormula) other).quantifier.equals(this.quantifier);
    }

}

class ForAll extends QuantifiedFormula {
    ForAll(String qvar, Formula originalFormula) {
        super("∀", qvar, originalFormula);
    }

    @Override
    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        Map<String, D> newE = new HashMap<>();
        for (String key : e.keySet()) {
            newE.put(key, e.get(key));
        }

        for (D d : m.domain()) {
            newE.put(this.qvar(), d);
            if (!this.originalFormula().isSatisfied(m,newE))
                return false;
        }
        return true;
    }

    @Override
    public Formula substitute(String var, Term t) throws NotApplicableException {
        if (qvar() == var) return new ForAll(this.qvar(), this.originalFormula().substitute("=", t));
        if (t.variables().contains(qvar()) && this.freeVariables().contains(var))throw new NotApplicableException(this, var, t);
        return new ForAll(this.qvar(), originalFormula().substitute(var,t));
    }
}

class Exists extends QuantifiedFormula {
    Exists(String qvar, Formula originalFormula) {
        super("∃", qvar, originalFormula);
    }


    @Override
    public Formula substitute(String var, Term t) throws NotApplicableException {
        if (qvar() == var) return new Exists(this.qvar(), this.originalFormula().substitute("∃", t));
        if (t.variables().contains(qvar()) && this.freeVariables().contains(var))throw new NotApplicableException(this, var, t);
        return new Exists(this.qvar(), originalFormula().substitute(var,t));
    }

    @Override
    public <D> boolean isSatisfied(Structure<D> m, Map<String, D> e) {
        Map<String, D> newE = new HashMap<>();
        for (String key : e.keySet()) {
            newE.put(key, e.get(key));
        }

        for (D d : m.domain()) {
            newE.put(this.qvar(), d);
            if (this.originalFormula().isSatisfied(m,newE))
                return true;
        }
        return false;
    }
}
