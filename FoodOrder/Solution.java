import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution implements Q05RestaurantRatingInterface {

    private Helper05 helper;
    Map<String, Order> orderDetailsMap;
    Map<String, List<Integer>> restrauntDetailsMap;
    Map<String, Map<String, List<Integer>>> foodDetailsMap;

    public Solution() {
    }

    @Override
    public void init(Helper05 helper) {
        this.helper = helper;
        this.orderDetailsMap = new LinkedHashMap<>();
        this.restrauntDetailsMap = new LinkedHashMap<>();
        this.foodDetailsMap = new LinkedHashMap<>();
    }

    @Override
    public void orderFood(String orderId, String foodItemId, String restaurantId) {
        this.orderDetailsMap.put(orderId, new Order(restaurantId, foodItemId));

        if (!this.restrauntDetailsMap.containsKey(restaurantId)) {
            this.restrauntDetailsMap.put(restaurantId, new ArrayList<>());
        }

        if (!this.foodDetailsMap.containsKey(foodItemId)) {
            Map<String, List<Integer>> restrauntMap = new LinkedHashMap<>();
            restrauntMap.put(restaurantId, new ArrayList<>());
            this.foodDetailsMap.put(foodItemId, restrauntMap);
        }
    }

    /**
     * when you(customer) are rating an order e.g giving 4 stars to an orders
     * then it means you are assigning 4 stars to both the food item
     * in that restaurant as well as 4 stars to the overall restaurant rating.
     * - rating ranges from 1 to 5, 5 is best, 1 is worst
     */
    @Override
    public void rateOrder(String orderId, int rating) {
        Order order = this.orderDetailsMap.get(orderId);

        String restrauntId = order.getRestrauntId();
        String foodItemId = order.getFoodItemtId();

        // Add restraunt rating
        if (!restrauntDetailsMap.containsKey(restrauntId)) {
            this.restrauntDetailsMap.put(restrauntId, new ArrayList<>(Arrays.asList(rating)));
        } else {
            List<Integer> list = this.restrauntDetailsMap.get(restrauntId);
            list.add(rating);
            this.restrauntDetailsMap.put(restrauntId, list);
        }

        // Add Food Item Rating
        if (!this.foodDetailsMap.containsKey(foodItemId)) {
            Map<String, List<Integer>> restrauntMap = new LinkedHashMap<>();
            restrauntMap.put(restrauntId, new ArrayList<>(Arrays.asList(rating)));
            this.foodDetailsMap.put(foodItemId, restrauntMap);
        } else {
            Map<String, List<Integer>> restrauntMap = this.foodDetailsMap.get(foodItemId);

            if (!restrauntMap.containsKey(restrauntId)) {
                restrauntMap.put(restrauntId, new ArrayList<>(Arrays.asList(rating)));
            } else {
                List<Integer> ratings = restrauntMap.get(restrauntId);
                ratings.add(rating);
                restrauntMap.put(restrauntId, ratings);
            }

            this.foodDetailsMap.put(foodItemId, restrauntMap);
        }
    }

    /**
     * - Fetches a list of top 20 restaurants
     * - unrated restaurants will be at the bottom of list.
     * - restaurants are sorted in descending order on average ratings
     * of the food item and then based on restaurant id lexicographically
     * - ratings are rounded down to 1 decimal point,
     * i.e. 4.05, 4.08, 4.11, 4.12, 4.14 all become 4.1,
     * 4.15, 4.19, 4.22, 4.24 all become 4.2
     * - e.g. 'food-item-1': veg burger is rated 4.3 in restaurant-4
     * and 4.6 in restaurant-6 then we will return ['restaurant-6', 'restaurant-4']
     */
    @Override
    public List<String> getTopRestaurantsByFood(String foodItemId) {

        Map<String, List<Integer>> restrauntMap = this.foodDetailsMap.getOrDefault(foodItemId, new LinkedHashMap<>());

        Map<String, Double> restrauntRatingsMap = calculateRestrauntRatings(restrauntMap);

        return restrauntRatingsMap.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder())
                                .thenComparing(Map.Entry.comparingByKey()))
                .limit(20)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

    }

    /**
     * - Here we are talking about restaurant's overall rating and NOT food item's
     * rating.
     */
    @Override
    public List<String> getTopRatedRestaurants() {

        Map<String, Double> ratingsMap = calculateRestrauntRatings(this.restrauntDetailsMap);

        return ratingsMap.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder())
                                .thenComparing(Map.Entry.comparingByKey()))
                .limit(20)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

    }

    private Map<String, Double> calculateRestrauntRatings(Map<String, List<Integer>> restrauntMap) {

        Map<String, Double> ratingMap = new LinkedHashMap<>();

        for (String restrauntId : restrauntMap.keySet()) {
            int total = 0, count = 0;
            for (Integer rating : restrauntMap.get(restrauntId)) {
                total += rating;
                count++;
            }
            double rating = (double) total / (double) count;
            ratingMap.put(restrauntId, roundRating(rating));
        }

        return ratingMap;
    }

    private double roundRating(double rating) {
        return (double) ((int) ((rating + 0.05) * 10)) / 10.0;
    }

    class Order {
        private String restrauntId;
        private String foodItemId;

        public Order(String restrauntId, String foodItemId) {
            this.restrauntId = restrauntId;
            this.foodItemId = foodItemId;
        }

        public String getRestrauntId() {
            return this.restrauntId;
        }

        public String getFoodItemtId() {
            return this.foodItemId;
        }
    }

}
