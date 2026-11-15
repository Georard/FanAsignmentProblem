package pl.edu.pwr.hyartsev.jp.lab02.solver;

import pl.edu.pwr.hyartsev.jp.lab02.criterion.Criterion;

import java.util.Random;

public class SimulatedAnnealingOptimizer implements Optimizer{
    private final double initialTemp;
    private final double minTemp;
    private final double coolingRate;
    private final int epochLength;
    private final Random random;

    private SimulatedAnnealingOptimizer(){
        this(0.65, 1e-5, 0.99, 50, new Random());
    }

    public SimulatedAnnealingOptimizer(double initialTemp, double minTemp,
                                       double coolingRate, int epochLength,
                                       Random random) {
        this.initialTemp = initialTemp;
        this.minTemp = minTemp;
        this.coolingRate = coolingRate;
        this.epochLength = epochLength;
        this.random = random;
    }

    @Override
    public AssignmentState solve(AssignmentState initialState, Criterion criterion) {
        // procedury inicjalizacji przed pętlą samego algorytmu
        // tworzenie kopii i zapisanie stanu bieżącego
        AssignmentState currentState = initialState.copy();
        // zapisanie bieżącego znaczenia funkcji celu
        double currentScore = criterion.evaluate(currentState);
        // zapisanie najlepszego stanu i wyniku
        AssignmentState bestState = currentState.copy();
        double bestScore = currentScore;

        double temperature = initialTemp;

        // pętla symulacyjna
        while (temperature > minTemp) {
            // prubujemy dokonać kilka mutacji przy tej samej temperaturze
            for (int i = 0; i < epochLength; i++) {
                // Wygeneruj sąsiada. Kopia stanu bieżącego i losowy ruch
                AssignmentState candidateState = currentState.copy();
                candidateState.mutate(random);
                double candidateScore = criterion.evaluate(candidateState);

                // Oblicza różnicę funkcji celu. delta < 0 - poprawa, delta > 0, pogorszenie
                double delta = candidateScore - currentScore;

                if (delta < 0) {
                    // Nowy stan jest lepszy — akceptujemy zawsze
                    currentState = candidateState;
                    currentScore = candidateScore;

                    // Sprawdzamy, czy pobiliśmy globalny rekord
                    if (currentScore < bestScore) {
                        bestState = currentState.copy();
                        bestScore = currentScore;
                    }
                } else {
                    // Nowy stan jest gorszy — akceptujemy z prawdopodobieństwem Boltzmanna
                    double acceptanceProbability = Math.exp(-delta / temperature);
                    if (random.nextDouble() < acceptanceProbability) {
                        currentState = candidateState;
                        currentScore = candidateScore;
                    }
                }
            }
            // Schładzanie po zakończeniu epoki
            temperature *= coolingRate;
        }

        return bestState;
    }
}
