package invertedIndex;

public class Position {
    public Position next = null;
    public Position last = null; // Pointer to the last node
    int value;

    public Position(int pos){
        value=pos;
        last = this;
    }
}
