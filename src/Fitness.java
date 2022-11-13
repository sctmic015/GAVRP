// Class to calculate fitness
public class Fitness {
    private Fitness() {
    }
    // Get fitness
    public static void evaluate(Route route, Configuration Configuration) {
        double currentDistance = 0;
        int currentDemand;
        int currentStartTime;
        int currentEndTime;
        int currentServiceTime;
        int currentTime;
        double orderPenalty = 0;
        double capacityPenalty;

        for (Gene gene : route.getGenes()) {
            int tempVehicleCapacity = Configuration.vehicleCapacity;
            int listSize = gene.getRoute().size()-1;
            currentTime=0;
            for (int i = 0; i <= listSize; i++) {
                if (i == 0) {
                    currentDistance += Configuration.distanceMatrix.get(0).get(gene.getRoute().get(0));
                } else {
                    currentDistance += Configuration.distanceMatrix.get(gene.getRoute().get(i - 1)).get(gene.getRoute().get(i));
                }

                currentDemand = Configuration.cities.get(gene.getRoute().get(i)).getDemand();
                currentStartTime = Configuration.cities.get(gene.getRoute().get(i)).readyTime();
                currentEndTime = Configuration.cities.get(gene.getRoute().get(i)).dueDate();
                currentServiceTime = Configuration.cities.get(gene.getRoute().get(i)).serviceTime();
                if (currentTime < currentStartTime){
                    currentTime = currentStartTime;
                }
                else if (currentTime >= currentStartTime && currentTime < currentEndTime){
                    orderPenalty += 0;
                }
                else {
                    orderPenalty += currentTime - currentEndTime;
                }

                while (currentDemand > 0) {
                    if (tempVehicleCapacity - currentDemand < 0) {
                        currentDemand -= tempVehicleCapacity;
                        currentDistance += Configuration.distanceMatrix.get(gene.getRoute().get(i)).get(0);
                        currentDistance += Configuration.distanceMatrix.get(0).get(gene.getRoute().get(i));
                        tempVehicleCapacity = Configuration.vehicleCapacity;
                    } else {
                        tempVehicleCapacity -= currentDemand;
                        currentDemand = 0;
                    }
                }

            }

            currentDistance += Configuration.distanceMatrix.get(gene.getRoute().get(listSize)).get(0);
        }

        route.setFitness(currentDistance, orderPenalty);
    }
}