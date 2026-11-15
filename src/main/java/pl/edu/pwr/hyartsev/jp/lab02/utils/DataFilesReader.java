package pl.edu.pwr.hyartsev.jp.lab02.utils;

import pl.edu.pwr.hyartsev.jp.lab02.data.Fan;
import pl.edu.pwr.hyartsev.jp.lab02.data.Room;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Klasa zapewniająca funkcjonalności do wczytywania plików
 * wejściowych z danymi niezbędnymi do symulacji
 */
public class DataFilesReader {

    /**
     * Wczytuje plik zawierający listę pokojów
     */
    public static List<Room> readRooms(String filePath) throws IOException {
        List<Room> rooms = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] tokens = line.split(",");
            if (tokens.length >= 3) {
                int id = Integer.parseInt(tokens[0].trim());
                int volume = Integer.parseInt(tokens[1].trim());
                int slotSize = Integer.parseInt(tokens[2].trim());
                rooms.add(new Room(id, volume, slotSize));
            }
        }
        return rooms;
    }

    /**
     * Wczytuje plik zawierający listę wentylatorów
     */
    public static List<Fan> readFans(String filePath) throws IOException {
        List<Fan> fans = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] tokens = line.split(",");
            if (tokens.length >= 4) {
                int id = Integer.parseInt(tokens[0].trim());
                int airflow = Integer.parseInt(tokens[1].trim());
                int connectorSize = Integer.parseInt(tokens[2].trim());
                int cost = Integer.parseInt(tokens[3].trim());
                fans.add(new Fan(id, airflow, connectorSize, cost));
            }
        }
        return fans;
    }
}
