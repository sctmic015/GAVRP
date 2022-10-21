import java.util.List;

// Creates city records with the name, demand and
public record City(List<Double> coordinates, int demand, String name,  int readyTime, int dueDate, int serviceTime) {
    public String getName() {
        return name;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public int getDemand() {
        return demand;
    }
    @Override
    public int readyTime() {
        return readyTime;
    }

    @Override
    public int dueDate() {
        return dueDate;
    }

    @Override
    public int serviceTime() {
        return serviceTime;
    }
}