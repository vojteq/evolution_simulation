import java.util.Arrays;


public class Animal {

    private Position position;
    private int energy;
    private WorldMap worldMap;
    private Orientation orientation;
    private static final int sizeOfGenome = 32;
    private int[] genome;

    public Animal(WorldMap worldMap){
        this.worldMap = worldMap;
        this.energy = worldMap.initialEnergy;
        this.genome = makeGenome();
        this.orientation = Orientation.NORTH.turn(genome[Math.abs(worldMap.random.nextInt()) % sizeOfGenome]);
        Position pos;
        do {
            pos = new Position(Math.abs(worldMap.random.nextInt()) % worldMap.widthOfMap, Math.abs(worldMap.random.nextInt()) % worldMap.heightOfMap);
        } while (worldMap.isOccupied(pos));
        this.position = pos;
    }

    public Animal(Animal parent1, Animal parent2, Position position){
        this.worldMap = parent1.worldMap;
        this.energy = parent1.energy / 4 + parent2.energy / 4;
        parent1.energy = parent1.energy - parent1.energy / 4;
        parent2.energy = parent2.energy - parent2.energy / 4;
        this.genome = takeGenesFromParents(parent1, parent2);
        this.orientation = Orientation.NORTH.turn(genome[Math.abs(worldMap.random.nextInt()) % sizeOfGenome]);
        this.position = position;
    }

    private int[] makeGenome(){
        int[] newGenome = new int[sizeOfGenome];
        int i = 0;
        while (i < 8)
            newGenome[i] = i++;
        while (i < sizeOfGenome)
            newGenome[i++] = Math.abs(worldMap.random.nextInt()) % 8;
        Arrays.sort(newGenome);
        return newGenome;
    }

    private int[] takeGenesFromParents(Animal parent1, Animal parent2) {
        int first = Math.abs(worldMap.random.nextInt()) % (sizeOfGenome - 2) + 1;          //indexes in range 1..30
        int second;
        do {
            second = Math.abs(worldMap.random.nextInt()) % (sizeOfGenome - 2) + 1;
        } while(first == second);

        if (first > second) {
            int tmp = first;
            first = second;
            second = tmp;
        }
        int[] result;
        if (Math.abs(parent1.worldMap.random.nextInt()) % 2 == 0 )
            result = copyGenes(first, second, parent1, parent2);
        else
            result = copyGenes(first, second, parent2, parent1);
        Arrays.sort(result);
        while(!allMovesPossible(result)){
            Arrays.sort(result);
            tryToFix(result);
        }
        return result;
    }

    private int[] copyGenes(int first, int second, Animal parent1, Animal parent2){
        int[] result = new int[sizeOfGenome];
        int index = 0;
        for (int i = 0; i < first; i++)
            result[index++] = parent1.genome[i];
        for (int i = first; i < second; i++)
            result[index++] = parent2.genome[i];
        for (int i = second; i < sizeOfGenome; i++)
            result[index++] = parent1.genome[i];
        return result;
    }

    private boolean allMovesPossible(int[] array){
        int j = 0;
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] < array[i+1])
                j++;
        }
        return j == 7;
    }

    private void tryToFix(int[] genesArray) {           //to make all moves possible
        boolean[] moves = new boolean[8];
        for (int i = 0; i < moves.length; i++) {
            moves[i] = false;
        }
        for (int i : genesArray) {
            moves[i] = true;
        }
        for (int i = 0; i < moves.length; i++) {
            if (!moves[i])
                genesArray[Math.abs(this.worldMap.random.nextInt()) % genesArray.length] = i;
        }
    }

    protected void move() {
        position = position.addWithModulo(orientation.toPosition(), worldMap.heightOfMap, worldMap.widthOfMap);
        energy -= worldMap.moveEnergy;
    }

    protected void changeOrientation(){
        orientation = orientation.turn(genome[Math.abs(worldMap.random.nextInt()) % sizeOfGenome]);
    }

    protected void eat(int energy){
        this.energy += energy;
    }

    public Position getPosition(){
        return position;
    }

    public int getEnergy(){
        return energy;
    }

    @Override
    public String toString() {
        return  String.valueOf(energy);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Animal))
            return false;
        return this == (Animal) obj;
    }

    public void showAnimalInfo() {
        for (int i = 0; i < sizeOfGenome; i++) {
            System.out.println(i +"   " + genome[i]);
        }
        System.out.println(energy);
        System.out.println(orientation);
        System.out.println(position);
    }
}