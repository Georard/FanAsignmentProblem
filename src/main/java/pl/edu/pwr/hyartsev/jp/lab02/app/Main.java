package pl.edu.pwr.hyartsev.jp.lab02.app;

import pl.edu.pwr.hyartsev.jp.lab02.criterion.Criterion;
import pl.edu.pwr.hyartsev.jp.lab02.criterion.VolumeMatchMinCostCriterion;
import pl.edu.pwr.hyartsev.jp.lab02.criterion.NormalizationHelper;
import pl.edu.pwr.hyartsev.jp.lab02.criterion.MaxFlowMinCostCriterion;
import pl.edu.pwr.hyartsev.jp.lab02.data.Fan;
import pl.edu.pwr.hyartsev.jp.lab02.data.Room;
import pl.edu.pwr.hyartsev.jp.lab02.solver.AssignmentState;
import pl.edu.pwr.hyartsev.jp.lab02.solver.Optimizer;
import pl.edu.pwr.hyartsev.jp.lab02.solver.SimulatedAnnealingOptimizer;
import pl.edu.pwr.hyartsev.jp.lab02.utils.DataFilesReader;
import pl.edu.pwr.hyartsev.jp.lab02.utils.SolutionFileWriter;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Główna klasa programu odpowiedzialna za uruchomienie symulacji.
 */
public class Main {

    /**
     *  Odpowiada za wczytanie parametrów początkowych symulacji
     *  oraz z ich wykorzystaniem uruchamia wczytywanie plików z
     *  danymi i uruchamia sym. wyżarzania z wybranym kryterium
     * @param args 1- ścieżka do pliku z pomieszczeniami
     *             2- ścieżka do pliku z wentylatorami
     *             3- wybrany kryterium minimalizacji
     *             (1-MaxFlowMinCostCriterion lub 2-VolumeMatchMinCostCriterion)
     *             4- waga przepływu/kubatury
     *             5- waga kosztu
     */
    public static void main(String[] args) {
        String roomsPath;
        String fansPath;
        int criterionMode;
        double w1;
        double w2;

        // Wymuszenie kodowania UTF-8 dla wypisywania w konsoli
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        // Pobranie parametrów z linii komend lub interaktywnie z konsoli
        if (args.length >= 5) {
            roomsPath = args[0];
            fansPath = args[1];
            criterionMode = Integer.parseInt(args[2]);
            w1 = Double.parseDouble(args[3].replace(',', '.'));
            w2 = Double.parseDouble(args[4].replace(',', '.'));
        } else {
            Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
            System.out.print("Podaj ścieżkę do pliku z pomieszczeniami (domyślnie: pomieszczenia.txt): ");
            String roomsInput = scanner.nextLine().trim();
            roomsPath = roomsInput.isEmpty() ? "pomieszczenia.txt" : roomsInput;

            System.out.print("Podaj ścieżkę do pliku z wentylatorami (domyślnie: wentylatory.txt): ");
            String fansInput = scanner.nextLine().trim();
            fansPath = fansInput.isEmpty() ? "wentylatory.txt" : fansInput;

            System.out.println("Wybierz kryterium:");
            System.out.println("  1 - Maksymalizacja przepływu powietrza i minimalizacja kosztów");
            System.out.println("  2 - Dopasowanie przepływu do kubatury budynku i minimalizacja kosztów");
            System.out.print("Twój wybór (1 lub 2): ");
            criterionMode = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Podaj wagę w1 (dla przepływu/kubatury, domyślnie 0.7): ");
            String w1Input = scanner.nextLine().trim().replace(',', '.');
            w1 = w1Input.isEmpty() ? 0.7 : Double.parseDouble(w1Input);

            System.out.print("Podaj wagę w2 (dla kosztu, domyślnie 0.3): ");
            String w2Input = scanner.nextLine().trim().replace(',', '.');
            w2 = w2Input.isEmpty() ? 0.3 : Double.parseDouble(w2Input);
        }

        try {
            // wczytanie danych wejściowych
            List<Room> rooms = DataFilesReader.readRooms(roomsPath);
            int n = rooms.size();
            List<Fan> fans = DataFilesReader.readFans(fansPath);

            if (rooms.isEmpty()) {
                System.err.println("Błąd: Lista pomieszczeń jest pusta!");
                return;
            }

            // obliczenie stałych normalizacyjnych i utworzenie kryterium
            double maxCost = NormalizationHelper.maxPossibleCost(rooms, fans);
            Criterion criterion;

            if (criterionMode == 1) {
                double maxAirflow = NormalizationHelper.maxPossibleAirflow(rooms, fans);
                criterion = new MaxFlowMinCostCriterion(w1, w2, maxAirflow, maxCost);
            } else if (criterionMode == 2) {
                double totalVolume = NormalizationHelper.totalBuildingVolume(rooms);
                criterion = new VolumeMatchMinCostCriterion(w1, w2, totalVolume, maxCost);
            } else {
                System.err.println("Nieznany tryb kryterium: " + criterionMode);
                return;
            }

            // uruchomienie optymalizacji (Symulowane Wyżarzanie)
            AssignmentState initialState = new AssignmentState(rooms, fans);
            Optimizer optimizer = new SimulatedAnnealingOptimizer(n);
            AssignmentState bestSolution = optimizer.solve(initialState, criterion);

            // wypisanie planu montażu na konsolę i zapis do pliku
            SolutionFileWriter.printToConsole(bestSolution, criterion);
            SolutionFileWriter.writeToFile("wynik.txt", bestSolution, criterion);
            System.out.println("Plan montażu zapisano również do pliku: wynik.txt");

        } catch (IOException e) {
            System.err.println("Błąd podczas obsługi plików: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Błąd formatu liczby w danych lub parametrach: " + e.getMessage());
        }
    }
}