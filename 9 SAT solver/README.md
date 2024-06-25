Cvičenie 9
==========

SAT solver
----------

Naprogramujte SAT solver, ktorý zisťuje, či je
vstupná formula (v konjunktívnej normálnej forme) splniteľná.

Na prednáške ste videli základnú kostru metódy DPLL, ktorej hlavnou ideou je
propagácia klauzúl s iba jednou premennou (_jednotková klauzula_,
<i lang="en">unit clause</i>). Tá ale hovorí o veciach ako _vymazávanie
literálov_ z klauzúl a _vymazávanie klauzúl_, čo sú veci, ktoré nie je také
ľahké efektívne (či už časovo alebo pamäťovo) implementovať, hlavne ak počas
<i lang="en">backtrack</i>ovania treba zmazané literály resp. klauzuly správne
naspäť obnovovať.

Použite teda techniku _sledovaných literálov_ na implementáciou DPLL solvera.


Odovzdávajte súbory [`Theory.java`](pu09-java/Theory.java)
a [`SatSolver.java`](pu09-java/SatSolver.java).
Program [`SatSolverTest.java`](pu09-java/SatSolverTest.java)
musí korektne zbehnúť s vašou knižnicou.
