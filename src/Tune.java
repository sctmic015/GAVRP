import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class Tune {
    static ArrayList<Double> crossOverParams = new ArrayList<>(Arrays.asList(0.5, 0.55, 0.06, 0.65, 0.07));
    static ArrayList<Double> mutationParams = new ArrayList<>(Arrays.asList(0.001, 0.005, 0.01, 0.015, 0.02));
    static ArrayList<Integer> tournamentSize = new ArrayList<>(Arrays.asList(2, 5, 10));
    static ArrayList<Integer> popSize = new ArrayList<>(Arrays.asList(2500, 3000, 3500));

    static ArrayList<Double> elitism = new ArrayList<>(Arrays.asList(0.0, 0.2, 0.4, 0.6));
    static ArrayList<Thread> threadList = new ArrayList<Thread>();

    public static void main(String[] args) throws IOException, InterruptedException {
        int startTime = (int) System.currentTimeMillis();
        FileWriter writer = new FileWriter("tuning.csv");
        writer.write("Population Size, CrossOver Rate, Mutation Rate, Tournament Size, Best Fitness, Elitism\n");
        for (Integer popSize:popSize){
            for (Double crossOver:crossOverParams){
                for (Double mutation:mutationParams){
                    for (Integer tournament:tournamentSize){
                        for (Double elitism:elitism) {
                            Configuration configuration = new Configuration(popSize.intValue(), crossOver.doubleValue(), mutation.doubleValue(), tournament.intValue(), elitism.doubleValue());
                            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(configuration);
                            threadList.add(new Thread(geneticAlgorithm));
                        }
                    }
                }
            }
        }
        for (Thread thread:threadList){
            thread.start();
        }
        for (Thread thread:threadList){
            thread.join();
        }
        Vector<String> output = GeneticAlgorithm.output;
        for (String out:output){
            System.out.println(out);
            writer.write(out + "\n");
        }
        writer.close();
        int endTime = (int) System.currentTimeMillis();
        System.out.println("Time taken for execution: " + (endTime - startTime));
    }
}
