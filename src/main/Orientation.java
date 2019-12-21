public enum Orientation {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

    public Orientation turn(int volumeOfTurn) {
        return intToOrierntation(this.orientationToInt() + volumeOfTurn);
    }

    @Override
    public String toString() {
        switch (this){
            case NORTH:
                return "N ";
            case NORTHEAST:
                return "NE";
            case EAST:
                return "E ";
            case SOUTHEAST:
                return "SE";
            case SOUTH:
                return "S ";
            case SOUTHWEST:
                return "SW";
            case WEST:
                return "W ";
            case NORTHWEST:
                return "NW";
        }
        return null;        //unreachable
    }

    private int orientationToInt() {
        switch (this) {
            case NORTH:
                return 0;
            case NORTHEAST:
                return 1;
            case EAST:
                return 2;
            case SOUTHEAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTHWEST:
                return 5;
            case WEST:
                return 6;
            case NORTHWEST:
                return 7;
        }
        return Integer.parseInt(null);      //unreachable
    }

    public Orientation intToOrierntation(int number){
        switch (number % 8) {
            case 0:
                return NORTH;
            case 1:
                return NORTHEAST;
            case 2:
                return EAST;
            case 3:
                return SOUTHEAST;
            case 4:
                return SOUTH;
            case 5:
                return SOUTHWEST;
            case 6:
                return WEST;
            case 7:
                return NORTHWEST;
        }
        return null;            //unreachable
    }

    public Position toPosition(){
        switch (this) {
            case NORTH:
                return new Position(0,1);
            case NORTHEAST:
                return new Position(1,1);
            case EAST:
                return new Position(1,0);
            case SOUTHEAST:
                return new Position(1,-1);
            case SOUTH:
                return new Position(0,-1);
            case SOUTHWEST:
                return new Position(-1,-1);
            case WEST:
                return new Position(-1,0);
            case NORTHWEST:
                return new Position(-1,1);
        }
        return null;        //unreachable
    }
}