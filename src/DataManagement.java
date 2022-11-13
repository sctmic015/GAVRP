import java.io.IOException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Class that simply loads in the data from the text file
public class DataManagement {

    private DataManagement() {
    }

    public static void readData(Configuration Configuration) throws IOException {
        File file = new File(Configuration.path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        br.readLine();
        String line;
        int count = 0;
        String cityName = "depot";
        double demandSum = 0;
        while ((line = br.readLine())!= null){
            String[] info = line.split("\s+");
            System.out.println(Arrays.toString(info));
            List<Double> coordinatesXY = new ArrayList<>();
            coordinatesXY.add(Double.parseDouble(info[2]));
            coordinatesXY.add(Double.parseDouble(info[3]));
            demandSum += Double.parseDouble(info[4]);
            if (count > 0){
                cityName = "c" + count;
            }
            City city = new City(coordinatesXY, (int) Double.parseDouble(info[4]), cityName, (int) Double.parseDouble(info[5]), (int) (Double.parseDouble(info[6])), (int) (Double.parseDouble(info[7])));
            Configuration.cities.put(count, city);
            count++;
        }
        System.out.println(demandSum);
        Configuration.countCities = Configuration.cities.size() - 1;
        Configuration.initDistanceMatrix();
    }

//    public static void readData() {
//        try {
//            Files.lines(Path.of(Configuration.path)).forEach(line ->
//            {
//                List<Double> coordinatesXY = new ArrayList<>();
//                String[] tempStringArray = line.split((";"));
//                coordinatesXY.add(Double.parseDouble(tempStringArray[2]));
//                coordinatesXY.add(Double.parseDouble(tempStringArray[3]));
//                City city = new City(coordinatesXY, Integer.parseInt(tempStringArray[4]), tempStringArray[1]);
//                Configuration.cities.put(Integer.parseInt(tempStringArray[0]), city);
//            });
//
//            Configuration.countCities = Configuration.cities.size() - 1;
//            Configuration.initDistanceMatrix();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }

}