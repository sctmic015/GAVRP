import java.util.ArrayList;
import java.util.List;

public class Route {
    private List<Gene> genes;
    private double fitness;

    public Route() {
    }

    public static Route build(List<Integer> cityIndexList) {
        List<Gene> tempGenes = new ArrayList<>();
        Route route = new Route();
        int n = Configuration.INSTANCE.countCities / Configuration.INSTANCE.vehicleQuantity;

        for (int k = 0; k < Configuration.INSTANCE.vehicleQuantity; k++) {
            List<Integer> genes = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (j == n - 1 && k == Configuration.INSTANCE.vehicleQuantity - 1) {
                    for (int l = 0; l < Configuration.INSTANCE.countCities % Configuration.INSTANCE.vehicleQuantity; l++) {
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
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}