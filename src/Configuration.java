import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// Specifies Configuration things. Think neat-python
public enum Configuration {
    INSTANCE;

    // random generator
    public final MersenneTwisterFast randomGenerator = new MersenneTwisterFast(System.nanoTime());

    // data management
    public final String path = "instance.txt";
    public final Map<Integer, City> cities = new TreeMap<>();
    // depot
    public final int vehicleQuantity = 33;
    public final int vehicleCapacity = 200;
    // genetic algorithm
    public final int populationQuantity = 2250;
    public final int maximumCountGeneration = 1000;
    public final double crossoverRate = 0.7;
    public final double mutationRate = 0.0003;
    public final int truncationNumber = 250;
    public int countCities = 0;
    public List<List<Double>> distanceMatrix;
    // Stores distances between cities
    public void initDistanceMatrix() {
        distanceMatrix = Utility.calculateDistanceMatrix(cities);
    }
}