// Class to calculate fitness
public class Fitness {
    private Fitness() {
    }
    // Get fitness
    public static void evaluate(Route route) {
        double currentDistance = 0;
        int currentDemand;
        int currentStartTime;
        int currentEndTime;
        int currentServiceTime;
        int currentTime;
        double orderPenalty;
        double capacityPenalty;

        for (Gene gene : route.getGenes()) {
            int tempVehicleCapacity = Configuration.INSTANCE.vehicleCapacity;
            int listSize = gene.getRoute().size() - 1;
            currentTime=0;
            for (int i = 0; i < listSize; i++) {
                if (i == 0) {
                    currentDistance += Configuration.INSTANCE.distanceMatrix.get(0).get(gene.getRoute().get(0));
                } else {
                    currentDistance += Configuration.INSTANCE.distanceMatrix.get(gene.getRoute().get(i - 1)).get(gene.getRoute().get(i));
                }

                currentDemand = Configuration.INSTANCE.cities.get(gene.getRoute().get(i)).getDemand();
                currentStartTime = Configuration.INSTANCE.cities.get(gene.getRoute().get(i)).readyTime();
                currentEndTime = Configuration.INSTANCE.cities.get(gene.getRoute().get(i)).dueDate();
                currentServiceTime = Configuration.INSTANCE.cities.get(gene.getRoute().get(i)).serviceTime();
                if (currentTime < currentStartTime){
                    currentTime = currentStartTime + currentServiceTime;
                }
                else {
                    currentDistance += 0.15 * (currentTime - currentStartTime);
                    currentTime +=10;
                }

                while (currentDemand > 0) {
                    if (tempVehicleCapacity - currentDemand < 0) {
                        currentDemand -= tempVehicleCapacity;
                        currentDistance += Configuration.INSTANCE.distanceMatrix.get(gene.getRoute().get(i)).get(0);
                        currentDistance += Configuration.INSTANCE.distanceMatrix.get(0).get(gene.getRoute().get(i));
                        tempVehicleCapacity = Configuration.INSTANCE.vehicleCapacity;
                    } else {
                        tempVehicleCapacity -= currentDemand;
                        currentDemand = 0;
                    }
                }

            }

            currentDistance += Configuration.INSTANCE.distanceMatrix.get(gene.getRoute().get(listSize)).get(0);
        }

        route.setFitness(currentDistance);
    }
}