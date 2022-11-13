import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GeneticAlgorithm extends Thread{
    private List<Route> routes;
    private int countCrossover;
    private int countMutation;

    //public static ArrayList<String> output = new ArrayList<String>();

    public static Vector<String> output = new Vector<>();

    private Configuration Configuration;

    // Reads in all Data
    public GeneticAlgorithm(Configuration Configuration) throws IOException {
        this.Configuration = Configuration;
        DataManagement.readData(Configuration);
    }

    // Runs evolutionary process
    public void execute() {
        routes = buildInitialPopulation();
        evolve(false);
    }

    public void run(){
        routes = buildInitialPopulation();
        evolve(true);
    }
    // Builds initial population
    private List<Route> buildInitialPopulation() {
        List<Route> routes = new ArrayList<>();
        List<Integer> cityIndexList = new ArrayList<>();

        // Count of number of cities
        for (int i = 1; i <= Configuration.countCities; i++) {
            cityIndexList.add(i);
        }

        // Randomly add routes
        for (int i = 0; i < Configuration.populationQuantity; i++) {
            Collections.shuffle(cityIndexList, Configuration.randomGenerator);
            // Adds routes in a list divided by number of cars
            routes.add(Route.build(cityIndexList, Configuration));
        }

        return routes;
    }

    private void evolve(boolean threaded) {
        int currentGeneration = 0;
        int bestFitness = Integer.MAX_VALUE;

        while (Configuration.maximumCountGeneration != currentGeneration) {
            currentGeneration++;
            for (Route route : routes){
                Fitness.evaluate(route, Configuration);
            }
//            ExecutorService executorService = Executors.newFixedThreadPool(8);
//            for (Route route : routes) {
//                executorService.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Fitness.evaluate(route, Configuration);
//                    }
//                });
//            }
//            executorService.shutdown();
//            try {
//                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//            } catch (InterruptedException e) {
//                System.out.println("You ducked up");
//                System.exit(1);
//            }

            sort(routes);

            // Selects 250 best chromosomes. Can actually change

            // Use Roulette Selection
            List<Route> matingPool = tournamentSelect(routes, Configuration.tournamentSize, Configuration.truncationNumber);
            //System.out.println("Mating Pool Size: " + matingPool.size());

            List<Route> children = orderCrossover(matingPool);
            mutate(children);

            // Simply removes the 250 worst chromosomes. Can also change
//            removeLastNChromosomes(routes, (int) (Configuration.randomGenerator.nextDouble() * routes.size() * (1 / 10.0)));
            addChildrenToPopulation(routes, children);
            for (Route route : routes){
                Fitness.evaluate(route, Configuration);
            }
//            ExecutorService executorService2 = Executors.newFixedThreadPool(8);
//            for (Route route : routes) {
//                executorService2.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Fitness.evaluate(route, Configuration);
//                    }
//                });
//            }
//            executorService2.shutdown();
//            try {
//                executorService2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//            } catch (InterruptedException e) {
//                System.out.println("You ducked up");
//                System.exit(1);
//            }


            if ((int) Math.round(getFittestChromosome(routes).getFitness()) < Math.round(bestFitness) || currentGeneration % 1000 == 0) {
                bestFitness = (int) Math.round(getFittestChromosome(routes).getFitness());
                if (!threaded) {
                    System.out.println(currentGeneration + " | bestFitness = " + (int) Math.round(getFittestChromosome(routes).getFitness()) +
                            " | distance = " + (int) Math.round(getFittestChromosome(routes).getDistance()) +
                            " | time penalty = " + (int) Math.round(getFittestChromosome(routes).getTimePenalty()));
                }
            }

            sort(routes);
            removeWithElitism(routes);
            //routes = removeWorst(routes);
        }

        System.out.println();

        System.out.println("[tour management]");
        for (Gene gene : routes.get(0).getGenes()) {
            System.out.print(gene.getRoute() + " ");
        }

        System.out.println();
        System.out.println();
        System.out.println("[best route]");
        Utility.printRoute(routes.get(0), Configuration.cities);

        System.out.println();
        System.out.println("countCrossover | " + countCrossover);
        System.out.println("countMutation  | " + countMutation);
        if (threaded){
            output.add(Configuration.populationQuantity + ", " + Configuration.crossoverRate + ", " +
            Configuration.mutationRate + ", " + Configuration.tournamentSize + ", " +
            bestFitness);
        }
    }

    private List<Route> select(List<Route> routes, int limit) {
        return routes.stream().limit(limit).collect(Collectors.toList());
    }

    private List<Route> tournamentSelect(List<Route> routes, int k, int limit) {
        double bestFitness = 0;
        Route bestIndividual = null;
        List<Route> outRoutes = new ArrayList<>();
        while (outRoutes.size() < limit) {
            for (int i = 0; i < k; i++) {
                int individualIndex = (int) (Configuration.randomGenerator.nextDouble() * 5);
                if (bestFitness == 0 || routes.get(i).getFitness() > bestFitness) {
                    bestFitness = routes.get(individualIndex).getFitness();
                    bestIndividual = routes.get(individualIndex);
                }
            }
            outRoutes.add(bestIndividual);
        }
        return outRoutes;
    }

    private List<Route> rouletteSelection(List<Route> routes, int limit){
        double worstFitness = routes.get(routes.size()-1).getFitness();
        double cumulativeFitness = 0;
        List<Double> inverseFitnesses = new ArrayList<>(routes.size()-1);
        // Work out cumulative  Fitness Score
        for (Route route:routes){
            double inverseFitness = worstFitness - route.getFitness();
            inverseFitnesses.add(inverseFitness);
            cumulativeFitness += worstFitness;
        }

        List<Route> outRoutes = new ArrayList<>();
        int count = 0;
        boolean flag = true;
        while (flag){
            double random = Configuration.randomGenerator.nextDouble();
            for (Route route:routes){
                double value = inverseFitnesses.get(count) / cumulativeFitness;
                if (random <= value){
                    outRoutes.add(route);
                    if (outRoutes.size() >= Configuration.truncationNumber){
                        flag = false;
                        break;
                    }
                }
            }
        }
        return outRoutes;

    }
    // Think it swaps on the outsides of the start and end index. Pretty sure Partially Matched crossover

//    private List<Route> orderedCrossover(List<Route> routes){
//        Collections.shuffle(routes);
//        List<Route> children = new ArrayList<>();
//
//        for (int i = 0; i < routes.size(); i +=2) {
//            if (Configuration.randomGenerator.nextDouble() < Configuration.crossoverRate) {
//                countCrossover++;
//                List<Integer> parent01 = new ArrayList<>();
//                List<Integer> parent02 = new ArrayList<>();
//
//                // Puts all genes back into a big list
//                for (Gene gene : routes.get(i).getGenes()) {
//                    parent01.addAll(gene.getRoute());
//                }
//
//                for (Gene gene : routes.get(i + 1).getGenes()) {
//                    parent02.addAll(gene.getRoute());
//                }
//
//                // Just creates the template for the child with the 10 city buckets
//                List<Integer> tempChild01 = new ArrayList<>(Collections.nCopies(Configuration.countCities, 0));
//                List<Integer> tempChild02 = new ArrayList<>(Collections.nCopies(Configuration.countCities, 0));
//
//                // Two random integers
//                int upperBound = Configuration.randomGenerator.nextInt(parent01.size());
//                int lowerBound = Configuration.randomGenerator.nextInt(parent01.size() - 1);
//
//                // Essentially actually get upper and lower bound
//                int start = Math.min(upperBound, lowerBound);
//                int end = Math.max(upperBound, lowerBound);
//
//                List<Integer> parent01Genes = new ArrayList<>(parent01.subList(start, end));
//                List<Integer> parent02Genes = new ArrayList<>(parent02.subList(start, end));
//
//                // Adds parent 1 and two genes into the middle of the child genes
//                tempChild01.addAll(start, parent01Genes);
//                tempChild02.addAll(start, parent02Genes);
//
//                // Remove parent each parents selected genes from each other
//                for (int j = 0; j <= parent01Genes.size() - 1; j++) {
//                    parent01.remove(parent02Genes.get(j));
//                    parent02.remove(parent01Genes.get(j));
//                }
//
//                int index = 0;
//                for (int z = 0; z < parent01.size(); z++) {
//                    index = (end + z) % (Configuration.countCities);
//                    tempChild01.set(index, parent02.get(z));
//                    tempChild02.set(index, parent01.get(z));
//                }
//
//                Route child01CityRoute = Route.build(tempChild01);
//                Route child02CityRoute = Route.build(tempChild02);
//
//                children.add(child01CityRoute);
//                children.add(child02CityRoute);
//            }
//        }
//        return children;
//    }

    private List<Route> orderCrossover(List<Route> routes){
        Collections.shuffle(routes);
        List<Route> children = new ArrayList<>();

        for (int i = 0; i < routes.size(); i += 2) {
            if (Configuration.randomGenerator.nextDouble() < Configuration.crossoverRate) {
                countCrossover++;

                List<Integer> parent01 = new ArrayList<>();
                List<Integer> parent02 = new ArrayList<>();

                for (Gene gene : routes.get(i).getGenes()) {
                    parent01.addAll(gene.getRoute());
                }

                for (Gene gene : routes.get(i + 1).getGenes()) {
                    parent02.addAll(gene.getRoute());
                }

                List<Integer> child01 = new ArrayList<>();
                List<Integer> child02 = new ArrayList<>();

                //crossover points
                int crossPoint1 = (int) (Configuration.randomGenerator.nextDouble() * Configuration.countCities);
                int crossPoint2 = (int) (Configuration.randomGenerator.nextDouble() * Configuration.countCities);

                //order cross points
                if (crossPoint1 > crossPoint2) {
                    int temp = crossPoint1;
                    crossPoint1 = crossPoint2;
                    crossPoint2 = temp;
                }

                //add first parent genes to each child

                child01.addAll(parent01.subList(crossPoint1, crossPoint2));
                child02.addAll(parent02.subList(crossPoint1, crossPoint2));

                final int size = parent02.size();

                //tracks position of current customer in children
                int currentIndex = 0;
                int currentCustomerInParent1 = 0;
                int currentCustomerInParent2 = 0;

                for (int j = 0; j < size; j++) {
                    currentIndex = (crossPoint2 + j) % size; // wraps around to beginning of list after crossing endpoint

                    // get the customer at the current index in each parent
                    currentCustomerInParent1 = parent01.get(currentIndex);
                    currentCustomerInParent2 = parent02.get(currentIndex);

                    // if child 1 does not already contain the current customer in parent 2, add it
                    if (!child01.contains(currentCustomerInParent2)) {
                        child01.add(currentCustomerInParent2);
                    }

                    // if child 2 does not already contain the current customer in parent 1, add it
                    if (!child02.contains(currentCustomerInParent1)) {
                        child02.add(currentCustomerInParent1);
                    }
                }
                //i rotate the children to allign the segments from the cross over to the original points
                //i.e, the start of the children (the original segment) is shifted to the index of the first cross over
                Collections.rotate(child01, crossPoint1);
                Collections.rotate(child02, crossPoint1);
                Route child01CityRoute = Route.build(child01, Configuration);
                Route child02CityRoute = Route.build(child02, Configuration);
                children.add(child02CityRoute);
                children.add(child01CityRoute);
            }
        }
        return children;
    }
    private List<Route> crossover(List<Route> routes) {
        Collections.shuffle(routes);
        List<Route> children = new ArrayList<>();

        for (int i = 0; i < routes.size(); i += 2) {
            if (Configuration.randomGenerator.nextDouble() < Configuration.crossoverRate) {
                countCrossover++;

                List<Integer> parent01 = new ArrayList<>();
                List<Integer> parent02 = new ArrayList<>();

                // Puts all genes back into a big list
                for (Gene gene : routes.get(i).getGenes()) {
                    parent01.addAll(gene.getRoute());
                }

                for (Gene gene : routes.get(i + 1).getGenes()) {
                    parent02.addAll(gene.getRoute());
                }

                // Just creates the template for the child with the 10 city buckets
                List<Integer> tempChild01 = new ArrayList<>(Collections.nCopies(Configuration.countCities, 0));
                List<Integer> tempChild02 = new ArrayList<>(Collections.nCopies(Configuration.countCities, 0));

                // Two random integers
                int upperBound = Configuration.randomGenerator.nextInt(parent01.size());
                int lowerBound = Configuration.randomGenerator.nextInt(parent01.size() - 1);

                // Essentially actually get upper and lower bound
                int start = Math.min(upperBound, lowerBound);
                int end = Math.max(upperBound, lowerBound);

                List<Integer> parent01Genes = new ArrayList<>(parent01.subList(start, end));
                List<Integer> parent02Genes = new ArrayList<>(parent02.subList(start, end));

                // Adds parent 1 and two genes into the middle of the child genes
                tempChild01.addAll(start, parent01Genes);
                tempChild02.addAll(start, parent02Genes);

                // Remove parent each parents selected genes from each other
                for (int j = 0; j <= parent01Genes.size() - 1; j++) {
                    parent01.remove(parent02Genes.get(j));
                    parent02.remove(parent01Genes.get(j));
                }

                for (int z = 0; z < parent01.size(); z++) {
                    tempChild01.set(tempChild01.indexOf(0), parent02.get(z));
                    tempChild02.set(tempChild02.indexOf(0), parent01.get(z));
                }

                Route child01CityRoute = Route.build(tempChild01, Configuration);
                Route child02CityRoute = Route.build(tempChild02, Configuration);

                children.add(child01CityRoute);
                children.add(child02CityRoute);
            }
        }

        return children;
    }


    //  Swap mutation with one item swapped
    private void mutate(List<Route> children) {
        // All the children routes after
        for (Route child : children) {
            List<Integer> currentChromosome = new ArrayList<>();

            for (Gene gene : child.getGenes()) {
                currentChromosome.addAll(gene.getRoute());
            }

            // All the single city integers in the current chromosomes
            for (Integer city : currentChromosome) {
                if (Configuration.randomGenerator.nextDouble() < Configuration.mutationRate) {
                    countMutation++;
                    int tempIndex = currentChromosome.indexOf(city);
                    int tempValue = city;
                    int indexToSwap = (int) (Configuration.randomGenerator.nextDouble() * Configuration.countCities);
                    int valueToSwap = currentChromosome.get(indexToSwap);
                    currentChromosome.set(tempIndex, valueToSwap);
                    currentChromosome.set(indexToSwap, tempValue);
                }
            }

            Route mutatedRoute = Route.build(currentChromosome, Configuration);
            routes.add(mutatedRoute);
        }
    }


    private void inversionMutate(List<Route> children) {
        // All the children routes after
        for (Route child : children) {
            List<Integer> currentChromosome = new ArrayList<>();

            for (Gene gene : child.getGenes()) {
                currentChromosome.addAll(gene.getRoute());
            }

            // All the single city integers in the current chromosomes
            for (Integer city : currentChromosome) {
                if (Configuration.randomGenerator.nextDouble() < Configuration.mutationRate) {
                    countMutation++;
//                    int moveOverValue = (int) (Configuration.randomGenerator.nextDouble() * 50);
//                    int startIndex = currentChromosome.indexOf(city);
//                    int endIndex = startIndex+moveOverValue;
                    int startIndex = (int) (Configuration.randomGenerator.nextDouble() + currentChromosome.size());
                    int endIndex = (int) (Configuration.randomGenerator.nextDouble() + currentChromosome.size());
                    int tempIndex = 0;
                    if (startIndex > endIndex){
                        tempIndex = startIndex;
                        startIndex = endIndex;
                        endIndex = tempIndex;
                    }
//                    if (endIndex > currentChromosome.size()){
//                        endIndex = currentChromosome.size();
//                    }
                    List<Integer> subset = currentChromosome.subList(startIndex, endIndex);
                    Collections.reverse(subset);
                    for (Integer value : subset){
                        currentChromosome.set(startIndex, value);
                        startIndex++;
                    }
                    break;
                }
            }

            Route mutatedRoute = Route.build(currentChromosome, Configuration);
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
            int indexToRemove = (int) ((population.size() - n) + Configuration.randomGenerator.nextDouble() * n);
            population.remove(indexToRemove);
        }
    }
    // Assumes ordered genomes
    private void removeWithElitism(List<Route> population) {
        int elites = (int) (Configuration.populationQuantity * Configuration.elitism);
        int numberToRemove = population.size() - Configuration.populationQuantity;
        for (int i = 0; i <= numberToRemove; i ++){
            int random = (int)(Configuration.randomGenerator.nextDouble() * (population.size() - numberToRemove) + numberToRemove);
            population.remove(random);
        }
    }

    // Assumes sorted Routes
    private List<Route> removeWorst(List<Route> population){
        return population.subList(0, Configuration.populationQuantity);
    }
    private void sort(List<Route> routes) {
        routes.sort(Comparator.comparing(Route::getFitness));
    }
}