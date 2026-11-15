package pl.edu.pwr.hyartsev.jp.lab02.criterion;

import pl.edu.pwr.hyartsev.jp.lab02.solver.AssignmentState;

/**
 * Drugi kryterium, który implementuje wzorzec strategii.
 * Zapewnia minimalizację różnicy między wolumenem wymienionego
 * powietrza a kubaturą wszystkich pomieszczeń przy jednoczesnej
 * minimalizacji kosztów.
 */
public class VolumeMatchMinCostCriterion implements Criterion{
    private final double diffWeight; // w1
    private final double costWeight;    // w2
    private final double totalBuildingVolume; // V_budynku
    private final double maxPossibleCost;    // C_norm

    public VolumeMatchMinCostCriterion(double diffWeight, double costWeight,
                                   double totalBuildingVolume, double maxPossibleCost) {
        this.diffWeight = diffWeight;
        this.costWeight = costWeight;
        // zabezpieczenie przed dzieleniem przez zero
        this.totalBuildingVolume = Math.max(1.0, totalBuildingVolume);
        this.maxPossibleCost = Math.max(1.0, maxPossibleCost);
    }


    @Override
    public double evaluate(AssignmentState state) {
        double diff = Math.abs(state.getTotalAirflow() - totalBuildingVolume);
        double normalizedDiff = diff / totalBuildingVolume;
        double normalizedCost = state.getTotalCost() / maxPossibleCost;

        return (diffWeight * normalizedDiff) + (costWeight * normalizedCost);
    }

    @Override
    public String getDescription() {
        return String.format("Dopasowanie do kubatury budynku (%.0f m3)" +
                        " i minimalizacja kosztu (w1=%.2f, w2=%.2f)",
                totalBuildingVolume, diffWeight, costWeight);
    }
}
