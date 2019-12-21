import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class VisualizationPanel extends JPanel {
    protected WorldMap worldMap;
    protected final int width;
    protected final int height;
    protected final int step;

    public VisualizationPanel(WorldMap worldMap, int width, int height) {
        this.worldMap = worldMap;
        this.width = width;
        this.height = height;
        step = Math.min(width, height) / Math.min(worldMap.heightOfMap, worldMap.widthOfMap);
    }

    @Override
    protected void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);
        } catch (NullPointerException e) {}
        this.setSize(width, height);

        this.setBackground(Color.ORANGE);

        g.setColor(Color.GREEN);
        for (Plant plant : worldMap.plantHashMap.values())
            g.fillRect(plant.position.x * step,plant.position.y * step, step, step);

        for (ArrayList<Animal> animals : worldMap.animalHashMap.values()) {
            g.setColor(getColor(animals));
            g.fillRect(animals.get(0).getPosition().x * step, animals.get(0).getPosition().y * step, step, step);
        }
        g.setColor(Color.BLACK);
        g.drawString("year: " + worldMap.year, 1000, 70);
        g.drawString("number of animals: " + worldMap.animalList.size(), 1000, 100);
        g.drawString("highest energy: " + worldMap.getStrongest(worldMap.animalList).get(0).getEnergy(), 1000, 130);

        g.drawString("colors: ", 1000, 160);
        g.drawString("energy < sqrt(average energy)", 1000 + 2 * step, 190);
        g.drawString("sqrt(average energy) < energy < average energy", 1000 + 2 * step, 220);
        g.drawString("average energy < energy < 2 * average energy", 1000 + 2 * step, 250);
        g.drawString("2 * average energy < energy", 1000 + 2 * step, 280);

        g.setColor(Color.BLACK);
        g.fillRect(1000, 190 - step / 2, step, step);
        g.setColor(new Color(221, 0, 0));
        g.fillRect(1000, 220 - step / 2, step, step);
        g.setColor(new Color(104, 0, 144));
        g.fillRect(1000, 250 - step / 2, step, step);
        g.setColor(new Color(0, 166, 255));
        g.fillRect(1000, 280 - step / 2, step, step);
    }

    private Color getColor(ArrayList<Animal> animals) {
        ArrayList<Animal> listOfStrongest = worldMap.getStrongest(animals);
        int energyOfStrongest = listOfStrongest.get(0).getEnergy();
        if (energyOfStrongest < worldMap.avgEnergy){
            if (energyOfStrongest < Math.sqrt(worldMap.avgEnergy))
                return Color.BLACK;
            return new Color(221, 0, 0);
        }
        if (energyOfStrongest < worldMap.avgEnergy * 2)
            return new Color(104, 0, 144);
        return new Color(0, 166, 255);
    }
}
