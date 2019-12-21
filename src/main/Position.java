public class Position {
    public final int x;
    public final int y;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "(" + this.x + "," + this.y + ")";
    }

    public Position addWithModulo(Position position, int height, int width){
        return new Position(Math.abs(this.x + position.x) % width, Math.abs(this.y + position.y) % height);
    }

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Position))
            return false;
        Position that = (Position) other;
        return (this.x == that.x && this.y == that.y);
    }

    @Override
    public int hashCode() {
        return 2137 + this.x * 21 + this.y * 37;
    }
}
