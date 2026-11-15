package pl.edu.pwr.hyartsev.jp.lab02.criterion;

import pl.edu.pwr.hyartsev.jp.lab02.data.Fan;
import pl.edu.pwr.hyartsev.jp.lab02.data.Room;

import java.util.ArrayList;
import java.util.Collections;
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
     * @param fans listę wszystkich wentylatorów w magazynie
     * @param n ilość wszystkich pokoi
     * @return największą możliwą wydajność zainstalowanych wentylatorów
     */
    public static int maxPossibleAirflow(List<Fan> fans, int n) {
        int maxPossibleAirflow = 0;
        List<Integer> airFlows = new ArrayList<>();
        for (Fan fan : fans)
            airFlows.add(fan.airflow());
        airFlows.sort(Collections.reverseOrder());

        for(int i = 0; i < Math.min(n, airFlows.size()); i++)
            maxPossibleAirflow += airFlows.get(i);
        return maxPossibleAirflow;
    }

    /**
     * Metoda pomocnicza.
     * @param fans Listę wszystkich wentylatorów w magazynie
     * @param n ilość wszystkich pokoi
     * @return największą możliwą cenę zainstalowanych wentylatorów.
     */
    public static int maxPossibleCost(List<Fan> fans, int n) {
        int maxPossibleCost = 0;
        List<Integer> costs = new ArrayList<>();
        for (Fan fan : fans) costs.add(fan.cost());

        costs.sort(Collections.reverseOrder());

        for(int i = 0; i < Math.min(n, costs.size()); i++)
            maxPossibleCost += costs.get(i);

        return  maxPossibleCost;
    }

}
