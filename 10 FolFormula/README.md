Cvičenie 10
===========


## FolFormula

Vytvorte objektovú hierarchiu na reprezentáciu prvorádových formúl.
Zadefinujte základné triedy `Term` a `Formula` a od nich odvodené triedy pre
jednotlivé typy termov a formúl.



```
Term
 │  constructor(...)
 │  name() -> String              // vrati nazov termu (i.e. premennej, konst., funkcie)
 │  toString() -> String          // vrati retazcovu reprezentaciu termu
 │  equals(Term other) -> Bool    // vrati true, ak je tento term rovnaky ako other
 │  variables() -> Set of String  // vrati mnozinu mien premennych
 │  constants() -> Set of String  // vrati mnozinu mien konstant
 │  functions() -> Set of String  // vrati mnozinu mien funkcii
 │  eval(Structure m, Valuation e) -> m.domain  // vrati hodnotu termu v m pri e
 │  substitute(String var, Term t) -> Term      // substituuje term t za vsetky vyskyty
 │                                              // premennej var v tomto terme
 │
 ├─ Variable
 │      constructor(String name)
 │
 ├─ Constant
 │      constructor(String name)
 │
 └─ FunctionApplication
        constructor(String name, Array of Term subts)
        subts() -> Array of Term      // vrati vsetky "priame" podtermy

Formula
 │  constructor()
 │  subfs() -> Array of Formula   // vrati vsetky priame podformuly ako pole
 │  toString() -> String          // vrati retazcovu reprezentaciu formuly
 │  equals(Formula other) -> Bool // vrati true, ak je tato formula rovnaka ako other
 │  variables() -> Set of String  // vrati mnozinu mien premennych
 │  constants() -> Set of String  // vrati mnozinu mien konstant
 │  functions() -> Set of String  // vrati mnozinu mien funkcii
 │  predicates() -> Set of String // vrati mnozinu mien predikatov
 │  isSatisfied(Structure m, Valuation e) -> Bool  // vrati true, ak je formula
 │                                                 // splnena v m pri e
 │  freeVariables() -> Set of String   // vrati mnozinu vsetkych volnych premennych
 │                                     // v tejto formule
 │  substitute(String var, Term t) -> Formula // substituuje term t za vsetky volne vyskyty
 │                                            // premennej var; vyhodi vynimku, ak substitucia
 │                                            // nie je aplikovatelna
 │
 ├─ AtomicFormula
 │   │  subts() -> Array of Term  // vrati termy, ktore su argumentmi predikatu/rovnosti
 │   │
 │   ├─ PredicateAtom
 │   │      constructor(String name, Array of Term subts)
 │   │      name() -> String          // vrati meno predikatu
 │   │
 │   └─ EqualityAtom
 │          constructor(Term leftTerm, Term rightTerm)
 │          leftTerm() -> Term
 │          rightTerm() -> Term
 │
 ├─ Negation
 │      constructor(Formula originalFormula)
 │      originalFormula() -> Formula // vrati povodnu formulu
 │                                   // (jedinu priamu podformulu)
 │
 ├─ Disjunction
 │      constructor(Array of Formula disjuncts)
 │
 ├─ Conjunction
 │      constructor(Array of Formula conjuncts)
 │
 ├─ BinaryFormula
 │   │  constructor(Formula leftSide, Formula rightSide)
 │   │  Formula leftSide()    // vrati lavu priamu podformulu
 │   │  Formula rightSide()   // vrati pravu priamu podformulu
 │   │
 │   ├─ Implication
 │   │
 │   └─ Equivalence
 │
 └─ QuantifiedFormula
     │  constructor(String qvar, Formula originalFormula)
     │  originalFormula() -> Formula  // vrati povodnu formulu
     │  qvar() -> String              // vrati meno kvantifikovanej premennej
     │
     ├─ ForAll
     │
     └─ Exists
```

