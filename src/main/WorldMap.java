
import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.*;

public class WorldMap {
    protected Random random = new Random();
    protected final Position upperRight;
    protected final int startOfJungleX;
    protected final int startOfJungleY;
    protected final int widthOfMap;
    protected final int heightOfMap;
    protected final int widthOfJungle;
    protected final int heightOfJungle;
    protected final int plantEnergy;
    protected final int initialEnergy;
    protected final int moveEnergy;
    protected final int reproduceEnergy;
    protected final int pairsOfPlantsSpawnedPerYear;
    protected final int sleep;
    protected int avgEnergy;
    protected ArrayList<Animal> animalList;
    protected Map<Position, ArrayList<Animal>> animalHashMap;
    protected Map<Position, Plant> plantHashMap;
    protected int year = 0;
    private static final Position[] nearby = {  new Position(0,1), new Position(1,1), new Position(1,0), new Position(1,-1),
                                                 new Position(0,-1), new Position(-1,-1), new Position(-1,0), new Position(-1,1)};

    public WorldMap(int heightOfMap, int widthOfMap, double jungleRatio, int initialEnergy, int moveEnergy, int plantEnergy, int reproduceEnergy, int pairsOfPlantsSpawnedPerYear, int sleep) {
        this.widthOfMap = widthOfMap;
        this.heightOfMap = heightOfMap;
        this.upperRight = new Position(widthOfMap - 1, heightOfMap - 1);
        widthOfJungle = (int) (widthOfMap * jungleRatio);
        heightOfJungle = (int) (heightOfMap * jungleRatio);
        this.startOfJungleX = (widthOfMap - widthOfJungle) / 2;
        this.startOfJungleY = (heightOfMap - heightOfJungle) / 2;
        this.sleep = sleep;
        this.initialEnergy = initialEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.reproduceEnergy = reproduceEnergy;
        this.pairsOfPlantsSpawnedPerYear = pairsOfPlantsSpawnedPerYear;
        animalList = new ArrayList<>();
        animalHashMap = new HashMap<>();
        plantHashMap = new HashMap<>();
    }

    private void placeAnimal() {
        Animal animal = new Animal(this);
        addAnimal(animal);
    }

    private Animal tryToPlaceChild(Animal parent1, Animal parent2) {
        if (parent1.getEnergy() > reproduceEnergy && parent2.getEnergy() > reproduceEnergy) {
            Position position = findPlaceForBabyAnimal(parent1);
            if (position != null) {         //null -> theres no place around parents so they wont reproduce
                return new Animal(parent1, parent2, position);
            }
        }
        return null;
    }

    private void addToHashMap(Animal animal) {
        ArrayList<Animal> animals = animalHashMap.get(animal.getPosition());
        if (animals != null)
            animals.add(animal);
        else{
            animals = new ArrayList<>();
            animals.add(animal);
            animalHashMap.put(animal.getPosition(), animals);
        }
    }

    private void removeFromHashMap(Animal animal) {
        ArrayList<Animal> animals = animalHashMap.get(animal.getPosition());
        animals.remove(animal);
        if (animals.isEmpty())
            animalHashMap.remove(animal.getPosition());
    }

    private void addAnimal(Animal animal) {
        animalList.add(animal);
        addToHashMap(animal);
    }

    private Position findPlaceForBabyAnimal(Animal parent1) {
        int numberOfAttempts = 0, i;
        Position position;
        while(numberOfAttempts < 10){
            i = Math.abs(random.nextInt()) % 8;
            position = parent1.getPosition().addWithModulo(nearby[i], heightOfMap, widthOfMap);
            if (!isOccupied(position))
                return position;
            numberOfAttempts++;
        }
        for (i = 0; i < 8; i++){
            position = parent1.getPosition().addWithModulo(nearby[i], heightOfMap, widthOfMap);
            if (!isOccupied(position))
                return position;
        }
        return null;        //parents wont reproduce
    }

    private void removeDeadAnimals() {
        ArrayList<Animal> deadAnimals = new ArrayList<>();
        for (Animal animal : animalList) {
            if (animal.getEnergy() <= 0) {
                deadAnimals.add(animal);
                removeFromHashMap(animal);
            }
        }
        animalList.removeAll(deadAnimals);
    }

    private void placePlants() {
        for (int i = 0; i < pairsOfPlantsSpawnedPerYear; i++) {
            placePlantInsideJungle();
            placePlantOutsideJungle();
        }
    }

    private void placePlantOutsideJungle() {
        int x, y, counter = 0;
        Position position;
        do {
            x = Math.abs(random.nextInt()) % widthOfMap;
            y = Math.abs(random.nextInt()) % heightOfMap;
            position = new Position(x,y);
            counter++;
        } while (inJungle(position) && isOccupied(position) && counter < 30);
        if(counter < 20)
            plantHashMap.put(position, new Plant(position));
    }

    private void placePlantInsideJungle(){
        int x, y, counter = 0;
        Position position;
        do {
            x = Math.abs(random.nextInt()) % widthOfJungle + startOfJungleX;
            y = Math.abs(random.nextInt()) % heightOfJungle + startOfJungleY;
            position = new Position(x,y);
            counter++;
        } while (isOccupied(position) && counter < 20);
        if (counter < 20)
            plantHashMap.put(position, new Plant(position));
    }

    protected Object objectAt(Position position) {
        if (animalHashMap.get(position) != null)
            return animalHashMap.get(position).get(0);
        return plantHashMap.get(position);
    }

    protected boolean isOccupied(Position position) {
        return objectAt(position) != null;
    }

    private boolean inJungle(Position position) {
        return position.x >= startOfJungleX && position.x <= startOfJungleX + widthOfJungle &&
                position.y >= startOfJungleY && position.y <= startOfJungleY + heightOfJungle;
    }

    protected ArrayList<Animal> getStrongest(ArrayList<Animal> animals) {
        if (animals.size() == 1)
            return animals;
        int maxEnergy = -moveEnergy;            //can move if have any energy, its last chance
        for (Animal a : animals)
            if (a.getEnergy() > maxEnergy)
                maxEnergy = a.getEnergy();
        ArrayList<Animal> strongest = new ArrayList<>();
        for (Animal a : animals)
            if (a.getEnergy() == maxEnergy)
                strongest.add(a);
        return strongest;
    }

    private void feedAll() {
        for (Map.Entry<Position, ArrayList<Animal>> entry : animalHashMap.entrySet()) {
            ArrayList<Animal> animals = entry.getValue();
            if (plantHashMap.get(animals.get(0).getPosition()) != null) {
                ArrayList<Animal> strongest = getStrongest(animals);
                int foodForEach = plantEnergy / strongest.size();
                plantHashMap.remove(strongest.get(0).getPosition());
                if (foodForEach > 0)
                    for (Animal a : strongest)
                        a.eat(foodForEach);
            }
        }
    }

    private void procreateAllPossible(){
        ArrayList<Animal> animals;
        ArrayList<Animal> animalsToPlace = new ArrayList<>();
        for (Map.Entry<Position, ArrayList<Animal>> entry : animalHashMap.entrySet()) {
            animals = entry.getValue();
            if (animals.size() == 2) {
                Animal animalToAdd = tryToPlaceChild(animals.get(0), animals.get(1));
                if (animalToAdd != null)
                    animalsToPlace.add(animalToAdd);
            }
            if (animals.size() > 2) {
                ArrayList<Animal> strongest = getStrongest(animals);
                if (strongest.size() == 1) {
                    animals.removeAll(strongest);
                    ArrayList<Animal> almostStrongest = getStrongest(animals);
                    animals.addAll(strongest);
                    strongest.add(almostStrongest.get(Math.abs(random.nextInt()) % almostStrongest.size()));
                }
                int parent1, parent2;
                parent1 = Math.abs(random.nextInt()) % strongest.size();
                do {
                    parent2 = Math.abs(random.nextInt()) % strongest.size();
                } while (parent1 == parent2);
                Animal animalToAdd = tryToPlaceChild(strongest.get(parent1), strongest.get(parent2));
                if (animalToAdd != null)
                    animalsToPlace.add(animalToAdd);
            }
        }
        for (Animal animal : animalsToPlace)
            addAnimal(animal);
    }

    private void rotateAndMoveAll() {
        for (Animal animal : animalList) {
            removeFromHashMap(animal);
            animal.changeOrientation();
            animal.move();
            addToHashMap(animal);
        }
    }

    public void startSimulation (int numberOfAnimals) throws InterruptedException {
        VisualizationPanel visualizationPanel = new VisualizationPanel(this, 1400, 940);
        Graphics g = new Graphics() {
            @Override
            public Graphics create() {
                return null;
            }

            @Override
            public void translate(int x, int y) {

            }

            @Override
            public Color getColor() {
                return null;
            }

            @Override
            public void setColor(Color c) {

            }

            @Override
            public void setPaintMode() {

            }

            @Override
            public void setXORMode(Color c1) {

            }

            @Override
            public Font getFont() {
                return null;
            }

            @Override
            public void setFont(Font font) {

            }

            @Override
            public FontMetrics getFontMetrics(Font f) {
                return null;
            }

            @Override
            public Rectangle getClipBounds() {
                return null;
            }

            @Override
            public void clipRect(int x, int y, int width, int height) {

            }

            @Override
            public void setClip(int x, int y, int width, int height) {

            }

            @Override
            public Shape getClip() {
                return null;
            }

            @Override
            public void setClip(Shape clip) {

            }

            @Override
            public void copyArea(int x, int y, int width, int height, int dx, int dy) {

            }

            @Override
            public void drawLine(int x1, int y1, int x2, int y2) {

            }

            @Override
            public void fillRect(int x, int y, int width, int height) {

            }

            @Override
            public void clearRect(int x, int y, int width, int height) {

            }

            @Override
            public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {

            }

            @Override
            public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {

            }

            @Override
            public void drawOval(int x, int y, int width, int height) {

            }

            @Override
            public void fillOval(int x, int y, int width, int height) {

            }

            @Override
            public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {

            }

            @Override
            public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {

            }

            @Override
            public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {

            }

            @Override
            public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {

            }

            @Override
            public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {

            }

            @Override
            public void drawString(String str, int x, int y) {

            }

            @Override
            public void drawString(AttributedCharacterIterator iterator, int x, int y) {

            }

            @Override
            public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
                return false;
            }

            @Override
            public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
                return false;
            }

            @Override
            public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
                return false;
            }

            @Override
            public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
                return false;
            }

            @Override
            public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
                return false;
            }

            @Override
            public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
                return false;
            }

            @Override
            public void dispose() {

            }
        };
        while (numberOfAnimals > 0){
            placeAnimal();
            numberOfAnimals--;
            placePlants();
        }
        JFrame frame = new JFrame("Simulation");
        frame.setSize(1400,940);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.add(visualizationPanel);
        visualizationPanel.paintComponent(g);
//                System.out.println(this.toString());
        while (animalList.size() > 1) {
            removeDeadAnimals();
            if (animalList.size() == 0)
                break;
            rotateAndMoveAll();
            feedAll();
            procreateAllPossible();
            placePlants();
            year++;
            calculateAvgEnergy();
            visualizationPanel.repaint();
//                System.out.println(this.toString());                     // uncomment to see as strings in console
            Thread.sleep(sleep);
        }
//        if (animalList.size() == 1)                                       //same
//            System.out.println("year " + year + ", only one animal alive");
//        else
//            System.out.println("year " + year + ", all animals are dead");
    }

    @Override
    public String toString() {
        MapVisualizer mapVisualizer = new MapVisualizer(this);
        return mapVisualizer.draw(new Position(0,0), upperRight);
    }

    private void calculateAvgEnergy() {
        int sum = 0;
        for (Animal animal : animalList)
            sum += animal.getEnergy();
        avgEnergy = sum / animalList.size();
    }
}