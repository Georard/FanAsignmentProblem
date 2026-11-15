package pl.edu.pwr.hyartsev.jp.lab02.solver;

import pl.edu.pwr.hyartsev.jp.lab02.criterion.Criterion;

public interface Optimizer {
    /**
     * Rozwiązuje problem optymalizacyjny dla zadanego
     * początkowego i kryterium.
     * @param initialState stan początkowy
     * @param criterion sparametryzowane kryterium oceny
     * @return najlepszy znaleziony stan przydziału wentylatorów
     */
    AssignmentState solve(AssignmentState initialState, Criterion criterion);
}