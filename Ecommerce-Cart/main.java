import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import Actors.Catalog.Product;
import service.CatalogService;
import service.UserService;

public class main {

    private static Map<String, Product> productMap;

    static {
        buildCatalog();
    }

    public static void main(String[] args) {

        /*
         * 1) User -> Login(create)
         * 2) User -> Add/Modify/Delete Items in a cart
         * Add -> CLI -> Product Category -> Display Products(Product Id: Product Name:
         * Variant: Quantity)
         * Modify -> CartItemId -> Product
         * Delete -> CartItemId
         */

        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();

        while (true) {
            System.out.println("Select Choice:");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Enter User Name:");
                    String name = scanner.nextLine();
                    String response = userService.addUser(name);
                    System.out.println(response);
                    break;
                default:
                    scanner.close();
                    System.exit(0);
                    break;
            }
        }

    }

    public static void buildCatalog() {
        CatalogService catalogService = new CatalogService();

        try {
            productMap = catalogService.loadProductToMap("metadata/products.json");
        } catch (FileNotFoundException e) {
            productMap = new LinkedHashMap<>();
            e.printStackTrace();
        }

        System.out.println(productMap.size());

    }
}
