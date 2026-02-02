import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

    private int size;
    private Map<Integer, Integer> snakeAndLadders;

    public Board(int size, List<BoardEntity> boardEntities) {
        this.size = size;
        this.snakeAndLadders = new HashMap<>();

        for (BoardEntity entity : boardEntities) {
            this.snakeAndLadders.put(entity.getStart(), entity.getEnd());
        }
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, Integer> getSnakeAndLadders() {
        return snakeAndLadders;
    }

    // function to return the final position when given the current position
    public int getFinalPosition(int startPosition) {
        return this.snakeAndLadders.getOrDefault(startPosition, startPosition);
    }
}
