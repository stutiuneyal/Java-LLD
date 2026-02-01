import java.util.List;

public interface Q05RestaurantRatingInterface {
    void init(Helper05 helper);

    void orderFood(String orderId, String restaurantId, String foodItemId);

    void rateOrder(String orderId, int rating);

    List<String> getTopRestaurantsByFood(String foodItemId);

    List<String> getTopRatedRestaurants();
}
