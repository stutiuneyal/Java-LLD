import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<BoardEntity> boardEntities = List.of(
                new Snake(17, 7),
                new Snake(54, 34),
                new Snake(62, 19),
                new Snake(98, 79),
                new Ladder(3, 38),
                new Ladder(24, 33),
                new Ladder(42, 93),
                new Ladder(72, 84));

        List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");

        Board board = new Board(100, boardEntities);
        Dice dice = new Dice(1, 6);
        List<Player> players = playerNames.stream().map(n -> new Player(n)).collect(Collectors.toList());

        Game game = new Game(board, dice, players);

        game.play();
    }
}
