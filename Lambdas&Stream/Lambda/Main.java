import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {

        // Lambda expression
        Add add = (a,b) -> a+b;

        int result = add.addition(10, 20);
        System.out.println(result);

        // Zero Parameter
        ZeroParameter zeroParameter = () -> System.out.println("This is zero param lambda");
        zeroParameter.display();

        // Single Parameter
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        System.out.println("All Elements:");
        list.forEach(n -> System.out.println(n));

        System.out.println("Even Elements:");
        list.forEach(n -> {
            if(n%2==0){
                System.out.println(n);
            }
        });

        // Multiple Paramters
        Functional addElems = (a,b) -> a+b;
        Functional multiplyElems = (a,b) -> a*b;

        System.out.println(addElems.operation(10, 20));
        System.out.println(multiplyElems.operation(10, 20));

        // Valid/Invalid Lambda Expressions

        // () -> {}: valid
        // () -> {return "Hello";}: valid
        // () -> {return "hi"}: invalid
        // x -> {return x+1;}: invalid -> if type inference is not possible
        // (int x,y) -> x+y: invalid -> type should be there for all the parameters

        // Common inbuilt Functional Interfaces

        /*
        a) Predicate (boolean test(T t)) -> Tests a given condition and returns true/false
        b) Consumer (void accept(T t)) -> Performs an action on given argument without returning a result
        c) Supplier (T get()) -> Supplies/Generated result without taking any input -> UseCase: lazy object creation
        d) Comparator<T> (int compare(T o1, T o2))
        e) Comparable<T> (int compareTo(T o))
        */

        Predicate<Integer> isEven = n -> n%2==0;
        System.out.println(isEven.test(20));
        System.out.println(isEven.test(21));

        Supplier<Integer> randomNumber = () -> new Random().nextInt(100);

        System.out.println(randomNumber.get());
        System.out.println(randomNumber.get());

    }
}
