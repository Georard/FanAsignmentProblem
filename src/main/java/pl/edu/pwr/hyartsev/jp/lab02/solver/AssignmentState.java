package pl.edu.pwr.hyartsev.jp.lab02.solver;

import pl.edu.pwr.hyartsev.jp.lab02.data.Fan;
import pl.edu.pwr.hyartsev.jp.lab02.data.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Klasa odpowiedzialna za przechowywanie pojedynczego rozwiązania
 * zadania optymalizacji.
 * Przechowuje pojedynczy schemat przydzielenia wentylatorów oraz
 * posiada funkcjonalności niezbędne do otrzymania informacji o
 * danym przydzieleniu, a także niezbędne do minimalnego mutowania
 * (losowej zmiany) danego stanu
 */
public class AssignmentState {
    // lista wszystkich pokoi do optymalizacji
    private final List<Room> rooms;
    // tablica wentylatorów przypisanych do konkretnych pokojów
    private final Fan[] assignedFans;
    // lista nieużytych wentylatorów
    private final List<Fan> freeFans;

    /**
     * Konstruktor startowy
     * @param rooms List -e obiektów typu Room
     * @param fans List -e obiektów typu Fan
     */
    public AssignmentState(List<Room> rooms, List<Fan> fans) {
        this.rooms = rooms;
        this.assignedFans = new Fan[rooms.size()];
        this.freeFans = new ArrayList<>(fans);
    }

    /**
     * Konstruktor używany przez metodę kopiowania stanu
     * @param rooms obecną List -e obiektów typu Room
     * @param assignedFans obecną tablicę przydzielonych wentylatorów
     * @param freeFans obecną listę nieprzydzielonych wentylatorów
     */
    private AssignmentState(List<Room> rooms, Fan[] assignedFans,
                            List<Fan> freeFans){
        this.rooms = rooms;
        this.assignedFans = assignedFans.clone();
        this.freeFans = new ArrayList<>(freeFans);
    }

    /**
     * Robi głęboką kopię przydzielonych stanów, tworząc nowy AssignmentState
     * obiekt. Przeznaczona do użycia w algorytmie SA.
     * @return głęboką kopię obiektu danej klasy
     */
    public AssignmentState copy() {
        return new AssignmentState(this.rooms, this.assignedFans,
                this.freeFans);
    }

    /**
     * Próbuje dokonać minimalnej zmiany przydzielenia
     * @param random przyjmuję zewnętrzny generator liczb losowych
     */
    public void mutate(Random random) {
        if (rooms.isEmpty()) return;
        // próbuje 10 razy dokonać ruchu, bo niektóre mogą być w
        // niektórych przypadkach niemożliwe
        for (int attempt = 0; attempt < 10; attempt++) {
            int moveType = random.nextInt(4);
            boolean success = switch (moveType) {
                case 0 -> tryAdd(random);
                case 1 -> tryRemove(random);
                case 2 -> tryReplace(random);
                case 3 -> trySwap(random);
                default -> false;
            };
            if (success) return; // jeśli da się wykonać minimalny ruch to kończy
        }
    }

    /**
     * Metoda pomocnicza. Jej celem jest wylosowanie wentylatora z magazynu
     * zmiana stanu poprzez przedzielenie go do losowego pustego pokoju.
     * Nie zadziała, jeśli brak wolnych wentylatorów czy pokoi.
     * @param random zewnętrzny generator liczb losowych
     * @return zwraca wartość logiczną. True w razie skutecznej zmiany
     * stanu i przedzielenia wentylatora z magazynu oraz false w razie
     * przeciwnym.
     */
    private boolean tryAdd(Random random){
        List<Integer> emptyRooms = getRoomIndices(false);
        // brak pustych pokoi czy wolnych wentylatorów
        if (emptyRooms.isEmpty() || freeFans.isEmpty()) return false;
        // inaczej losuje losowy index z listy
        int roomIdx = emptyRooms
                .get(random.nextInt(emptyRooms.size()));

        Room chosenRoom = rooms.get(roomIdx);
        List<Fan> matchingFans = getMatchingFreeFans(rooms.get(roomIdx));
        if (matchingFans.isEmpty()) return false; // jeśli takich nie ma

        // losuje wentylator z listy
        Fan chosenFan = matchingFans.get(random.nextInt(matchingFans.size()));
        freeFans.remove(chosenFan);
        assignedFans[roomIdx] = chosenFan;
        // wentylator został poprawnie wylosowany i dodany
        return true;
    }

    /**
     * Metoda pomocnicza losowo usuwająca wentylator z losowego pokoju,
     * jeśli takie pokoje z przedzielonymi wentylatorami są
     * @param random zewnętrzny generator liczb losowych
     * @return zwraca wartość logiczną. True w razie skutecznej zmiany
     * stanu i usunięcia wentylatora z tablicy przypisanych i dodania go
     * do listy niewykorzystanych oraz false w razie przeciwnym.
     */
    private boolean tryRemove(Random random){
        List<Integer> occupiedRooms = getRoomIndices(true);
        // sprawdza, czy są wolne od wentylatorów pokoi
        if (occupiedRooms.isEmpty()) return false;
        // losuje jeden z takich, jeśli są
        int roomIdx = occupiedRooms.get(
                random.nextInt(occupiedRooms.size()));
        // usuwa z tego pokoju wentylator
        freeFans.add(assignedFans[roomIdx]);
        assignedFans[roomIdx] = null;
        return true;
    }

    /**
     * Metoda pomocnicza losowo zamieniająca wentylator z losowego pokoju,
     * jeśli takie pokoje z przedzielonymi wentylatorami są, i zamieniająca
     * go innym pasującym wentylatorem z puli niezajętych
     * @param random zewnętrzny generator liczb losowych
     * @return zwraca wartość logiczną. True w razie skutecznej zmiany
     * stanu i zamiany wentylatora oraz false w razie przeciwnym.
     */
    private boolean tryReplace(Random random){
        List<Integer> occupiedRooms = getRoomIndices(true);
        // sprawdza, czy są takie pokoi
        if (occupiedRooms.isEmpty() || freeFans.isEmpty()) return false;
        // losuje jeden z takich, jeśli są
        int roomIdx = occupiedRooms.get(
                random.nextInt(occupiedRooms.size()));
        // szuka pasujących złączem wentylatorów
        List<Fan> matchingFans = getMatchingFreeFans(rooms.get(roomIdx));
        if (matchingFans.isEmpty()) return false; // jeśli takich nie ma
        // losuje wentylator z listy
        Fan chosenFan = matchingFans.get(random.nextInt(matchingFans.size()));
        freeFans.remove(chosenFan);
        freeFans.add(assignedFans[roomIdx]);
        assignedFans[roomIdx] = chosenFan;
        // wentylator został poprawnie wylosowany dodany
        return true;
    }

    /**
     * Metoda pomocnicza losowo zamieniająca miejscami dwa wentylatora
     * z puli przedzielonych do pokojów, jeśli takie pokoje
     * z przedzielonymi wentylatorami są i pasują rozmiary złącz
     * @param random zewnętrzny generator liczb losowych
     * @return zwraca wartość logiczną. True w razie skutecznej zmiany
     * stanu i zamiany wentylatorów oraz false w razie przeciwnym.
     */
    private boolean trySwap(Random random){
        if(rooms.size() < 2) return false;
        // losuje dwa pokoje z z wentylatorami
        int roomIdxA = random.nextInt(rooms.size());
        int roomIdxB = random.nextInt(rooms.size());
        if (roomIdxA == roomIdxB) return false;
        Fan fanA = assignedFans[roomIdxA];
        Fan fanB = assignedFans[roomIdxB];

        // sprawdza cza pasują rozmiary
        if(fanA.connectorSize() > rooms.get(roomIdxB).slotSize()) return false;
        if(fanB.connectorSize() > rooms.get(roomIdxA).slotSize()) return false;
        // zamienia miejscami
        assignedFans[roomIdxA] = fanB;
        assignedFans[roomIdxB] = fanA;
        return true;
    }

    /** Metoda pomocnicza.
     * Zwraca indeksy pokojów zajętych lub pustych w zależności od
     * parametru wejściowego
     * @param occupied indeksy zajętych (occupied = true) lub pustych (occupied = false)
     * @return listę indeksów
     */
    private List<Integer> getRoomIndices(boolean occupied) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < assignedFans.length; i++) {
            if ((assignedFans[i] != null) == occupied) {
                indices.add(i);
            }
        }
        return indices;
    }

    //

    /**
     * Metoda pomocnicza.
     * Zwraca wolne wentylatory pasujące rozmiarem do danego pokoju
     * @param room pokój, który sprawdzamy
     * @return listę pasujących wentylatorów
     */
    private List<Fan> getMatchingFreeFans(Room room) {
        List<Fan> matching = new ArrayList<>();
        for (Fan fan : freeFans) {
            if (fan.connectorSize() <= room.slotSize()) {
                matching.add(fan);
            }
        }
        return matching;
    }


    /**
     * Pomaga otrzymać całkowitą wydajność zainstalowanych wentylatorów
     * @return całkowitą wydajność przydzielonych wentylatorów
     */
    public int getTotalAirflow(){
        int totalAirFlow = 0;
        for(Fan fan : assignedFans){
            if(fan != null) totalAirFlow += fan.airflow();
        }
        return totalAirFlow;
    }

    /**
     * Pomaga otrzymać całkowity koszt zainstalowanych wentylatorów
     * @return całkowity koszt przydzielonych wentylatorów
     */
    public int getTotalCost(){
        int totalCost = 0;
        for(Fan fan : assignedFans){
            if(fan != null) totalCost += fan.cost();
        }
        return totalCost;
    }

    /**
     * Standardowy getter pomagający otrzymać dostęp
     * do odczytania listy pokojów z klasy
     * @return listę pokojów
     */
    public List<Room> getRooms() { return Collections.unmodifiableList(rooms); }

    /**
     * Standardowy getter zwracający obiekt Fan
     * @param roomIndex index konkretnego ciekawiącego nas pokoju
     * @return wentylator zamontowany w tym pokoju.
     * Wypluwa null, jeśli w tym pokoju nie zamontowano wentylatora
     */
    public Fan getAssignedFan(int roomIndex) { return assignedFans[roomIndex]; }
}
