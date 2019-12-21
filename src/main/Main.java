import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException {


        int height, width, initialEnergy, moveEnergy, plantEnergy, reproduceEnergy, pairsOfPlantsSpawnedPerYear, sleep, numberOfAnimals;
        double jungleRatio;
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader("startingData.json"));
            JSONObject jsonObject = (JSONObject) obj;
            height = (int) (long) jsonObject.get("height");
            width = (int) (long) jsonObject.get("width");
            initialEnergy = (int) (long) jsonObject.get("initialEnergy");
            moveEnergy = (int) (long) jsonObject.get("moveEnergy");
            plantEnergy = (int) (long) jsonObject.get("plantEnergy");
            reproduceEnergy = (int) (long) jsonObject.get("reproduceEnergy");
            pairsOfPlantsSpawnedPerYear = (int) (long) jsonObject.get("pairsOfPlantsSpawnedPerYear");
            sleep = (int) (long) jsonObject.get("sleep");
            numberOfAnimals = (int) (long) jsonObject.get("numberOfAnimals");
            jungleRatio = (double) jsonObject.get("jungleRatio");

            WorldMap worldMap = new WorldMap(height, width, jungleRatio, initialEnergy, moveEnergy, plantEnergy, reproduceEnergy, pairsOfPlantsSpawnedPerYear, sleep);
            worldMap.startSimulation(numberOfAnimals);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}
