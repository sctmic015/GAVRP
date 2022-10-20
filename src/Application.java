import java.io.IOException;

// Simply starts the application
public class Application {
    public static void main(String... args) throws IOException {
        final long startTime = System.currentTimeMillis();
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.execute();
        final long endTime = System.currentTimeMillis();
        System.out.println("Time to run: " + (endTime - startTime));
    }
}