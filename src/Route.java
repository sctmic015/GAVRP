import java.util.ArrayList;
import java.util.List;

public class Route {
    private List<Gene> genes;
    // Think a route will have an associated fitness
    private double[] fitness = new double[2];

    public Route() {
    }

    // Build A route
    public static Route build(List<Integer> cityIndexList, Configuration Configuration) {
        List<Gene> tempGenes = new ArrayList<>();
        Route route = new Route();
        int n = Configuration.countCities / Configuration.vehicleQuantity;

        for (int k = 0; k < Configuration.vehicleQuantity; k++) {
            List<Integer> genes = new ArrayList<>();
            // Simply populates each gene removing the first item from the global list in the process
            for (int j = 0; j < n; j++) {
                if (j == n - 1 && k == Configuration.vehicleQuantity - 1) {
                    for (int l = 0; l < Configuration.countCities % Configuration.vehicleQuantity; l++) {
                        genes.add(cityIndexList.get(0));
                        int index = cityIndexList.remove(0);
                        cityIndexList.add(index);
                    }
                }

                genes.add(cityIndexList.get(0));
                int num = cityIndexList.remove(0);
                cityIndexList.add(num);
            }

            Gene gene = new Gene();
            gene.setRoute(genes);
            tempGenes.add(gene);
        }

        route.setGenes(tempGenes);
        return route;
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    public double getFitness() {
        if (fitness[1] != 0){
            return fitness[0] + 10 * fitness[1];
        }
        else{
            return  fitness[0] - 10;
        }
    }

    public double getDistance(){
        return this.fitness[0];
    }

    public double getTimePenalty(){
        return this.fitness[1];
    }

    public void setFitness(double distance, double timePenalty) {
        this.fitness[0] = distance;
        this.fitness[1] = timePenalty;
    }
}