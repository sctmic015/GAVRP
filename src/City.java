import java.util.List;

// Creates city records with the name, demand and
public record City(List<Double> coordinates, int demand, String name) {
    public String getName() {
        return name;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public int getDemand() {
        return demand;
    }
}