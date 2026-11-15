package pl.edu.pwr.hyartsev.jp.lab02.criterion;

import pl.edu.pwr.hyartsev.jp.lab02.solver.AssignmentState;

/**
 * Interfejs pomagający w realizacji wzorca Strategia.
 * Za jego pomocą można wybrać, według którego kryterium
 * należy dokonać optymalizacji.
 */
public interface Criterion {
    /**
     * Oblicza wartość funkcji celu dla danego stanu.
     * Im mniej wartość tego kryterium tym "lepiej"
     * @param state obecny stan
     * @return Wyliczoną wartość kryterium
     */
    double evaluate(AssignmentState state);

    /**
     * Przydatne do otrzymania opisu wybranego kryterium
     * @return zwraca czytelny opis kryterium i jego parametrów
     */
    String getDescription();
}
