import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

public class GeneticAlgorithm {
    private List<Route> routes;
    private int countCrossover;
    private int countMutation;

    public GeneticAlgorithm() throws IOException {
        DataManagement.readData();
    }

    public void execute() {
        routes = buildInitialPopulation();
        evolve();
    }

    private List<Route> buildInitialPopulation() {
        List<Route> routes = new ArrayList<>();
        List<Integer> cityIndexList = new ArrayList<>();

        for (int i = 1; i <= Configuration.INSTANCE.countCities; i++) {
            cityIndexList.add(i);
        }

        for (int i = 0; i < Configuration.INSTANCE.populationQuantity; i++) {
            Collections.shuffle(cityIndexList, Configuration.INSTANCE.randomGenerator);
            routes.add(Route.build(cityIndexList));
        }

        return routes;
    }

    private void evolve() {
        int currentGeneration = 0;
        int bestFitness = Integer.MAX_VALUE;

        while (Configuration.INSTANCE.maximumCountGeneration != currentGeneration) {
            currentGeneration++;

            ExecutorService executorService = Executors.newFixedThreadPool(8);
            for (Route route : routes) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Fitness.evaluate(route);
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                System.out.println("You ducked up");
                System.exit(1);
            }

            sort(routes);

            List<Route> matingPool = select(routes, Configuration.INSTANCE.truncationNumber);

            List<Route> children = crossover(matingPool);
            mutate(children);

            removeLastNChromosomes(routes, (int) (Configuration.INSTANCE.randomGenerator.nextDouble() * routes.size() * (1 / 10.0)));
            addChildrenToPopulation(routes, children);

            ExecutorService executorService2 = Executors.newFixedThreadPool(8);
            for (Route route : routes) {
                executorService2.execute(new Runnable() {
                    @Override
                    public void run() {
                        Fitness.evaluate(route);
                    }
                });
            }
            executorService2.shutdown();
            try {
                executorService2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                System.out.println("You ducked up");
                System.exit(1);
            }

            if ((int) Math.round(getFittestChromosome(routes).getFitness()) < Math.round(bestFitness)) {
                bestFitness = (int) Math.round(getFittestChromosome(routes).getFitness());
                System.out.println(currentGeneration + " | bestFitness = " + (int) Math.round(getFittestChromosome(routes).getFitness()));
            }

            sort(routes);
        }

        System.out.println();

        System.out.println("[tour management]");
        for (Gene gene : routes.get(0).getGenes()) {
            System.out.print(gene.getRoute() + " ");
        }

        System.out.println();
        System.out.println();
        System.out.println("[best route]");
        Utility.printRoute(routes.get(0), Configuration.INSTANCE.cities);

        System.out.println();
        System.out.println("countCrossover | " + countCrossover);
        System.out.println("countMutation  | " + countMutation);
    }

    private List<Route> select(List<Route> routes, int limit) {
        return routes.stream().limit(limit).collect(Collectors.toList());
    }

    private List<Route> crossover(List<Route> routes) {
        Collections.shuffle(routes);
        List<Route> children = new ArrayList<>();

        for (int i = 0; i < routes.size(); i += 2) {
            if (Configuration.INSTANCE.randomGenerator.nextDouble() < Configuration.INSTANCE.crossoverRate) {
                countCrossover++;

                List<Integer> parent01 = new ArrayList<>();
                List<Integer> parent02 = new ArrayList<>();

                for (Gene gene : routes.get(i).getGenes()) {
                    parent01.addAll(gene.getRoute());
                }

                for (Gene gene : routes.get(i + 1).getGenes()) {
                    parent02.addAll(gene.getRoute());
                }

                List<Integer> tempChild01 = new ArrayList<>(Collections.nCopies(Configuration.INSTANCE.countCities, 0));
                List<Integer> tempChild02 = new ArrayList<>(Collections.nCopies(Configuration.INSTANCE.countCities, 0));

                int upperBound = Configuration.INSTANCE.randomGenerator.nextInt(parent01.size());
                int lowerBound = Configuration.INSTANCE.randomGenerator.nextInt(parent01.size() - 1);

                int start = Math.min(upperBound, lowerBound);
                int end = Math.max(upperBound, lowerBound);

                List<Integer> parent01Genes = new ArrayList<>(parent01.subList(start, end));
                List<Integer> parent02Genes = new ArrayList<>(parent02.subList(start, end));

                tempChild01.addAll(start, parent01Genes);
                tempChild02.addAll(start, parent02Genes);

                for (int j = 0; j <= parent01Genes.size() - 1; j++) {
                    parent01.remove(parent02Genes.get(j));
                    parent02.remove(parent01Genes.get(j));
                }

                for (int z = 0; z < parent01.size(); z++) {
                    tempChild01.set(tempChild01.indexOf(0), parent02.get(z));
                    tempChild02.set(tempChild02.indexOf(0), parent01.get(z));
                }

                Route child01CityRoute = Route.build(tempChild01);
                Route child02CityRoute = Route.build(tempChild02);

                children.add(child01CityRoute);
                children.add(child02CityRoute);
            }
        }

        return children;
    }

    private void mutate(List<Route> children) {
        for (Route child : children) {
            List<Integer> currentChromosome = new ArrayList<>();

            for (Gene gene : child.getGenes()) {
                currentChromosome.addAll(gene.getRoute());
            }

            for (Integer city : currentChromosome) {
                if (Configuration.INSTANCE.randomGenerator.nextDouble() < Configuration.INSTANCE.mutationRate) {
                    countMutation++;
                    int tempIndex = currentChromosome.indexOf(city);
                    int tempValue = city;
                    int indexToSwap = (int) (Configuration.INSTANCE.randomGenerator.nextDouble() * Configuration.INSTANCE.countCities);
                    int valueToSwap = currentChromosome.get(indexToSwap);
                    currentChromosome.set(tempIndex, valueToSwap);
                    currentChromosome.set(indexToSwap, tempValue);
                }
            }

            Route mutatedRoute = Route.build(currentChromosome);
            routes.add(mutatedRoute);
        }
    }

    public Route getFittestChromosome(List<Route> routes) {
        return routes.get(0);
    }

    private void addChildrenToPopulation(List<Route> population, List<Route> newChildren) {
        population.addAll(newChildren);
    }

    private void removeLastNChromosomes(List<Route> population, int n) {
        for (int i = 0; i < n; i++) {
            int indexToRemove = (int) ((population.size() - n) + Configuration.INSTANCE.randomGenerator.nextDouble() * n);
            population.remove(indexToRemove);
        }
    }

    private void sort(List<Route> routes) {
        routes.sort(Comparator.comparing(Route::getFitness));
    }
}