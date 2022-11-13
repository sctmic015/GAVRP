import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// Specifies Configuration things. Think neat-python
public class Configuration {
    // random generator
    public final MersenneTwisterFast randomGenerator = new MersenneTwisterFast(System.nanoTime());

    // data management
    public final String path = "instance.txt";
    public final Map<Integer, City> cities = new TreeMap<>();
    // depot
    public final int vehicleQuantity = 20;
    public final int vehicleCapacity = 200;
    // genetic algorithm
    public int populationQuantity = 3500;
    public final int maximumCountGeneration = 10000;
    public double crossoverRate = 0.7;
    public double mutationRate = 0.01;
    public final int truncationNumber = (int) (populationQuantity * 0.5);

    public double elitism = 0.2;
    public int countCities = 0;
    public List<List<Double>> distanceMatrix;

    public int tournamentSize = 2;
    // Stores distances between cities
    public void initDistanceMatrix() {
        distanceMatrix = Utility.calculateDistanceMatrix(cities);
    }
    public Configuration(){

    }

    public Configuration(int populationQuantity, double crossoverRate, double mutationRate, int tournamentSize, double elitism){
        this.populationQuantity = populationQuantity;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
        this.elitism = elitism;
    }

}