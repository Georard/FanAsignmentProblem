package pl.edu.pwr.hyartsev.jp.lab02.utils;

import pl.edu.pwr.hyartsev.jp.lab02.criterion.Criterion;
import pl.edu.pwr.hyartsev.jp.lab02.criterion.NormalizationHelper;
import pl.edu.pwr.hyartsev.jp.lab02.data.Fan;
import pl.edu.pwr.hyartsev.jp.lab02.data.Room;
import pl.edu.pwr.hyartsev.jp.lab02.solver.AssignmentState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Klasa odpowiedzialna za formatowanie, wyświetlanie i zapisywanie
 * planu montażu wentylatorów.
 */
public class SolutionFileWriter {

    public static String formatReport(AssignmentState solution, Criterion criterion) {
        StringBuilder sb = new StringBuilder();
        List<Room> rooms = solution.getRooms();
        int totalVolume = NormalizationHelper.totalBuildingVolume(rooms);
        int totalAirflow = solution.getTotalAirflow();
        int totalCost = solution.getTotalCost();
        double score = criterion.evaluate(solution);

        sb.append("=================================================================\n");
        sb.append("                 PLAN MONTAŻU WENTYLATORÓW\n");
        sb.append("=================================================================\n");
        sb.append("Kryterium: ").append(criterion.getDescription()).append("\n");
        sb.append(String.format("Osiągnięta wartość funkcji celu: %.6f\n", score));
        sb.append("-----------------------------------------------------------------\n");
        sb.append(String.format("Kubatura wszystkich pomieszczeń: %d m3\n", totalVolume));
        sb.append(String.format("Całkowity przepływ powietrza:    %d m3/h (różnica: %d)\n",
                totalAirflow, Math.abs(totalAirflow - totalVolume)));
        sb.append(String.format("Całkowity koszt wentylatorów:    %d\n", totalCost));
        sb.append("-----------------------------------------------------------------\n");
        sb.append(String.format("%-10s | %-13s | %-10s | %-25s\n",
                "Pokój ID", "Kubatura [m3]", "Złącze [U]", "Zamontowany wentylator"));
        sb.append("-----------------------------------------------------------------\n");

        int assignedCount = 0;
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            Fan fan = solution.getAssignedFan(i);
            String fanInfo;
            if (fan != null) {
                assignedCount++;
                fanInfo = String.format("ID: %d (wyd: %d m3/h, %dU, koszt: %d)",
                        fan.id(), fan.airflow(), fan.connectorSize(), fan.cost());
            } else {
                fanInfo = "BRAK";
            }
            sb.append(String.format("%-10d | %-13d | %-10d | %s\n",
                    room.id(), room.volume(), room.slotSize(), fanInfo));
        }

        sb.append("-----------------------------------------------------------------\n");
        sb.append(String.format("Obsadzono pomieszczeń: %d / %d\n", assignedCount, rooms.size()));
        sb.append("=================================================================\n");

        return sb.toString();
    }

    public static void printToConsole(AssignmentState solution, Criterion criterion) {
        System.out.println(formatReport(solution, criterion));
    }

    public static void writeToFile(String filePath, AssignmentState solution, Criterion criterion) throws IOException {
        String report = formatReport(solution, criterion);
        Files.writeString(Paths.get(filePath), report);
    }
}