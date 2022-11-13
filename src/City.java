import java.util.List;

// Creates city records with the name, demand and
public class City {
    List<Double> coordinates;
    int demand;
    String name;
    int readyTime;
    int dueDate;
    int serviceTime;
    public City(List<Double> coordinates, int demand, String name,  int readyTime, int dueDate, int serviceTime){
        this.coordinates=coordinates;
        this.demand = demand;
        this.readyTime = readyTime;
        this.dueDate = dueDate;
        this.serviceTime = serviceTime;
    }
    public String getName() {
        return name;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public int getDemand() {
        return demand;
    }

    public int readyTime() {
        return readyTime;
    }


    public int dueDate() {
        return dueDate;
    }


    public int serviceTime() {
        return serviceTime;
    }
}