import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        // Stream Source
        List<Integer> number = Arrays.asList(10, 20, 30, 40,20,30,20,67,58,100);
        Stream<Integer> stream = number.stream();

        /*
        Intermediate Operations
        1. filter -> filters elements based on condition
        2. map -> transforms each element in the stream to another value
        3. sorted -> sorts the elements of the stream
        4. distinct -> remove duplicates
        5. skip -> skip first n elements
        */

        stream = stream.filter(n -> n>10)
        .map(n -> n*2)
        .distinct()
        .sorted(Comparator.comparingInt(Integer::intValue).reversed())
        .skip(2);

        /*
        Terminal Operations
        1. forEach -> iterates all the elements of the stream
        2. collect(Collectors) -> collects the output into the collecion
        3. reduce -> it reduces stream elements into a single aggregated result(sum/avg etc)
        4. count -> returns total number of elements in the stream
        5. anyMatch/allMatch/noneMatch -> they check whether elements match a given condition
        6. findFirst/findAny -> they return the first or any element from a stream
        */

        stream.forEach(System.out::println);

        // combining everything
        number.stream()
        .filter(n -> n > 20)
        .map(n -> n^2)
        .sorted(Comparator.comparingInt(Integer::intValue).reversed())
        .skip(2)
        .distinct()
        .forEach(System.out::println);

    }
}