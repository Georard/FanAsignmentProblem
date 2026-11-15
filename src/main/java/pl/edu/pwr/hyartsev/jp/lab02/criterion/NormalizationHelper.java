package pl.edu.pwr.hyartsev.jp.lab02.criterion;

import pl.edu.pwr.hyartsev.jp.lab02.data.Fan;
import pl.edu.pwr.hyartsev.jp.lab02.data.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * To mała klasa pomocnicza, której celem jest zapewnić funkcjonalności pomagające
 * otrzymać informacje niezbędne do wyliczenia kryterium
 */
public class NormalizationHelper {

    /**
     * Metoda pomocnicza.
     * @param rooms listę wszystkich pokoi w budynku
     * @return objętość całego pomieszczenia
     */
    public static int totalBuildingVolume(List<Room> rooms){
        int totalBuildingVolume = 0;
        for(Room room : rooms)
            totalBuildingVolume += room.volume();

        return totalBuildingVolume;
    }

    /**
     * Metoda pomocnicza.
     * @param rooms lista wszystkich pokoi
     * @param fans listę wszystkich wentylatorów w magazynie
     * @return największą możliwą wydajność zainstalowanych wentylatorów
     */
    public static int maxPossibleAirflow(List<Room> rooms, List<Fan> fans) {
        List<Room> sortedRooms = new ArrayList<>(rooms);
        sortedRooms.sort(Comparator.comparingInt(Room::slotSize).reversed());

        List<Fan> availableFans = new ArrayList<>(fans);
        availableFans.sort(Comparator.comparingInt(Fan::airflow).reversed());
        int maxPossibleAirflow = 0;

        for (Room room : sortedRooms) {
            for (int i = 0; i < availableFans.size(); i++) {
                if (availableFans.get(i).connectorSize() <= room.slotSize()) {
                    maxPossibleAirflow += availableFans.get(i).airflow();
                    availableFans.remove(i);
                    break;
                }
            }
        }
        return maxPossibleAirflow;
    }

    /**
     * Metoda pomocnicza.
     * @param rooms Listę wszystkich pokoi
     * @param fans Listę wszystkich wentylatorów w magazynie
     * @return największą możliwą cenę zainstalowanych wentylatorów.
     */
    public static int maxPossibleCost(List<Room> rooms, List<Fan> fans) {
        int maxPossibleCost = 0;

        List<Room> sortedRooms = new ArrayList<>(rooms);
        sortedRooms.sort(Comparator.comparingInt(Room::slotSize).reversed());

        List<Fan> availableFans = new ArrayList<>(fans);
        availableFans.sort(Comparator.comparingInt(Fan::cost).reversed());

        for (Room room : sortedRooms) {
            for (int i = 0; i < availableFans.size(); i++) {
                if (availableFans.get(i).connectorSize() <= room.slotSize()) {
                    maxPossibleCost += availableFans.get(i).cost();
                    availableFans.remove(i);
                    break;
                }
            }
        }
        return maxPossibleCost;
    }

}
