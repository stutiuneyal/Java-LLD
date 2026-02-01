import java.util.List;

public class FoordOrder {

    public static void main(String[] args) {
        Helper05 helper = new Helper05();
        Solution solution = new Solution();

        solution.init(helper);
        solution.orderFood("order-0", "food-1", "restaurant-0");

        solution.rateOrder("order-0", 3);

        solution.orderFood("order-1", "food-0", "restaurant-2");

        solution.rateOrder("order-1", 1);

        solution.orderFood("order-2", "food-0", "restaurant-1");

        solution.rateOrder("order-2", 3);

        solution.orderFood("order-3", "food-0", "restaurant-2");

        solution.rateOrder("order-3", 5);

        solution.orderFood("order-4", "food-0", "restaurant-0");

        solution.rateOrder("order-4", 3);

        solution.orderFood("order-5", "food-0", "restaurant-1");

        solution.rateOrder("order-5", 4);

        solution.orderFood("order-6", "food-1", "restaurant-0");

        solution.rateOrder("order-6", 2);

        solution.orderFood("order-7", "food-1", "restaurant-0");

        solution.rateOrder("order-7", 2);

        solution.orderFood("order-8", "food-0", "restaurant-1");

        solution.rateOrder("order-8", 2);

        solution.orderFood("order-9", "food-0", "restaurant-1");

        solution.rateOrder("order-9", 4);

        List<String> topRestrauntsByFood =  solution.getTopRestaurantsByFood("food-0");
        System.out.println(topRestrauntsByFood);

        topRestrauntsByFood = solution.getTopRestaurantsByFood("food-1");
        System.out.println(topRestrauntsByFood);

        List<String> topRatedRestraunts = solution.getTopRatedRestaurants();
        System.out.println(topRatedRestraunts);
    }
}