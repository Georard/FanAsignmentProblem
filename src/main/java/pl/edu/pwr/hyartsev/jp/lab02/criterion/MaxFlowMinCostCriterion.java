package pl.edu.pwr.hyartsev.jp.lab02.criterion;

import pl.edu.pwr.hyartsev.jp.lab02.solver.AssignmentState;

/**
 * Kryterium, który implementuje wzorzec strategii.
 * Zapewnia maksymalną wydajność wentylatorów z minimalizacją
 * kosztów.
 */
public class MaxFlowMinCostCriterion implements Criterion{
    private final double airflowWeight; // w1
    private final double costWeight;    // w2
    private final double maxPossibleAirflow; // Q_norm
    private final double maxPossibleCost;    // C_norm

    public MaxFlowMinCostCriterion(double airflowWeight, double costWeight,
                                   double maxPossibleAirflow, double maxPossibleCost) {
        this.airflowWeight = airflowWeight;
        this.costWeight = costWeight;
        // zabezpieczenie przed dzieleniem przez zero
        this.maxPossibleAirflow = Math.max(1.0, maxPossibleAirflow);
        this.maxPossibleCost = Math.max(1.0, maxPossibleCost);
    }

    @Override
    public double evaluate(AssignmentState state) {
        double normalizedAirflow = state.getTotalAirflow() / maxPossibleAirflow;
        double normalizedCost = state.getTotalCost() / maxPossibleCost;

        return (-airflowWeight * normalizedAirflow) + (costWeight * normalizedCost);
    }

    @Override
    public String getDescription() {
        return String.format("Maksymalizacja przepływu i minimalizacja kosztu (w1=%.2f, w2=%.2f)",
                airflowWeight, costWeight);
    }
}
