package pl.edu.pwr.hyartsev.jp.lab02.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Generator nietrywialnych danych wejściowych.
 */
public class DataGenerator {

    /**
     * Generuje pliki pomieszczeń oraz wentylatorów z wąskimi złączami (U)
     * oraz różnym stosunkiem wydajności do kosztu.
     * @param roomsPath ścieżka docelowa dla pliku z pomieszczeniami
     * @param fansPath  ścieżka docelowa dla pliku z wentylatorami
     * @param numRooms  liczba pomieszczeń [n]
     * @param numFans   liczba wentylatorów [k]
     * @param seed      ziarno generatora losowego, żeby wyniki były powtarzalne
     */
    public static void generateTestData(String roomsPath, String fansPath,
                                        int numRooms, int numFans, long seed) throws IOException {
        Random random = new Random(seed);

        // Generowanie pomieszczeń
        StringBuilder roomsSb = new StringBuilder();
        roomsSb.append("# nr pomieszczenia, kubatura [m3], rozmiar miejsca montażu [U]\n");

        for (int id = 1; id <= numRooms; id++) {
            // Dostępność złączy: 40% -> 1U, 30% -> 2U, 20% -> 3U, 10% -> 4U
            int roll = random.nextInt(100);
            int slotSize;
            int minVolume;
            int maxVolume;

            if (roll < 40) {
                slotSize = 1;
                minVolume = 40;
                maxVolume = 120;
            } else if (roll < 70) {
                slotSize = 2;
                minVolume = 100;
                maxVolume = 240;
            } else if (roll < 90) {
                slotSize = 3;
                minVolume = 200;
                maxVolume = 380;
            } else {
                slotSize = 4;
                minVolume = 320;
                maxVolume = 550;
            }

            // Kubatury zaokrąglone do pełnych dziesiątek
            int volume = ((minVolume + random.nextInt(maxVolume - minVolume + 1)) / 10) * 10;
            roomsSb.append(String.format("%d, %d, %d\n", id, volume, slotSize));
        }
        Files.writeString(Paths.get(roomsPath), roomsSb.toString());

        // Generowanie wentylatorów
        StringBuilder fansSb = new StringBuilder();
        fansSb.append("# nr wentylatora, wydajność [m3/h], rozmiar przyłącza [U], koszt\n");

        for (int id = 1; id <= numFans; id++) {
            // więcej średnich i dużych wentylatorów, żeby stworzyć większą konkurencję o słoty
            int roll = random.nextInt(100);
            int connectorSize;
            int baseAirflow;

            // mały(1U): 25%; średni(2U): 30%; duży(3U): 25%; XL(4U): 20%
            if (roll < 25) {
                connectorSize = 1;
                baseAirflow = 50 + random.nextInt(91);   // 50 - 140 m3/h
            } else if (roll < 55) {
                connectorSize = 2;
                baseAirflow = 130 + random.nextInt(151); // 130 - 280 m3/h
            } else if (roll < 80) {
                connectorSize = 3;
                baseAirflow = 250 + random.nextInt(201); // 250 - 450 m3/h
            } else {
                connectorSize = 4;
                baseAirflow = 400 + random.nextInt(301); // 400 - 700 m3/h
            }

            // zaokrąglenie do piątek wyniku
            int airflow = (baseAirflow / 5) * 5;

            // Bazowy koszt powiązany z wydajnością i rozmiarem + losowe odchylenie
            double priceFactor = 0.65 + (random.nextDouble() * 0.90); // mnożnik od 0.65x do 1.55x
            int cost = Math.max(10, (int) Math.round((airflow * 1.4 + connectorSize * 15) * priceFactor));

            fansSb.append(String.format("%d, %d, %d, %d\n", id, airflow, connectorSize, cost));
        }
        Files.writeString(Paths.get(fansPath), fansSb.toString());
    }

    // Szybkie wygenerowanie plików bez uruchamiania głównej aplikacji
    public static void main(String[] args) {
        try {
            generateTestData("rooms.txt", "fans.txt", 60, 180, 42L);
            System.out.println("Wygenerowano pliki: rooms.txt (60) oraz fans.txt (180).");
        } catch (IOException e) {
            System.err.println("Błąd podczas generowania plików: " + e.getMessage());
        }
    }
}