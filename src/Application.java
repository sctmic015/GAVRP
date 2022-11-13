import java.io.IOException;

// Simply starts the application
public class Application {
    public static void main(String... args) throws IOException {
        final long startTime = System.currentTimeMillis();
        Configuration configuration = new Configuration();
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(configuration);
        geneticAlgorithm.execute();
        final long endTime = System.currentTimeMillis();
        System.out.println("Time to run: " + (endTime - startTime));
    }
}