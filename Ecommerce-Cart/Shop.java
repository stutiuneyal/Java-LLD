import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import Actors.User;
import Actors.Catalog.Product;
import service.CartService;
import service.CatalogService;
import service.UserService;

public class Shop {

    private static Map<String, Product> productMap;
    private static CatalogService catalogService;
    private static User loggedInUser;

    static {
        catalogService = new CatalogService();
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
        CartService cartService = new CartService();

        while (true) {

            int choice = 0;
            while (true) {
                printMenu();
                System.out.println("Select Choice:");
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Choice should be valid integer");
                    scanner.nextLine();
                }
            }

            switch (choice) {
                case 1:
                    System.out.println("Enter User Name:");
                    String name = scanner.nextLine();
                    String response = userService.addUser(name);
                    System.out.println(response);
                    break;
                case 2:
                    System.out.println("Enter User Name to Login:");
                    name = scanner.nextLine();
                    loggedInUser = checkUser(name);
                    if (loggedInUser == null) {
                        System.out.println("User with name: " + name + " does not exist please signup");
                        break;
                    }
                    System.out.println("Current LoggedIn User: " + loggedInUser.getName());
                    break;
                case 3:
                    if (loggedInUser == null) {
                        System.out.println("Please logon first");
                        break;
                    }

                    while (true) {
                        System.out.println("------ Welcome to Stuti Store( " + loggedInUser.getName() + " ) ------");
                        printShopMenu();

                        System.out.println("Select the option:");
                        int shopChoice = scanner.nextInt();
                        scanner.nextLine();

                        // store the loggedIn User Category
                        String categoryName = "";

                        switch (shopChoice) {
                            case 1:
                                System.out.println();
                                cartService.displayCategories();

                                System.out.println("Enter the category Name:");
                                categoryName = scanner.nextLine();
                                catalogService.displayProductsByCategoryName(categoryName);
                                break;

                            case 2:
                                System.out.println("Please Enter the Product Id:");
                                String productId = scanner.nextLine();
                                System.out.println("PLease Enter the Variant:");
                                String variant = scanner.nextLine();
                                System.out.println("Please Enter the quantity:");
                                int qty = scanner.nextInt();
                                scanner.nextLine();

                                cartService.addItemsToCart(loggedInUser, productId, variant, qty, scanner,productMap);
                                break;
                            case 3:
                                while (true) {
                                    System.out.println("----- Your Cart -------");
                                    cartService.displayCart(loggedInUser);

                                    System.out.println();
                                    printCartModificationMenu();
                                    int cartModifyChoice = scanner.nextInt();
                                    scanner.nextLine();

                                    switch (cartModifyChoice) {
                                        case 1:
                                            System.out.println("Please Enter the Product Id:");
                                            productId = scanner.nextLine();
                                            System.out.println("Enter the Variant Name:");
                                            String variantName = scanner.nextLine();
                                            System.out.println("Enter the Edited Quantity Name:");
                                            int variantQty = scanner.nextInt();
                                            scanner.nextLine();

                                            cartService.editCartVariant(loggedInUser, productId, variantName,
                                                    variantQty, scanner,productMap);
                                            break;
                                        case 2:
                                            System.out.println("Please Enter the Product Id:");
                                            productId = scanner.nextLine();
                                            System.out.println("Enter the Variant Name:");
                                            variantName = scanner.nextLine();
                                            variantQty = -1;

                                            cartService.editCartVariant(loggedInUser, productId, variantName,
                                                    variantQty, scanner,productMap);
                                            break;
                                        case 3:
                                            System.out.println("Modifying completed");
                                            break;
                                    }

                                    if (cartModifyChoice == 3) {
                                        break;
                                    }

                                }
                                break;
                            case 4:
                                System.out.println("Please Enter the Product Id:");
                                productId = scanner.nextLine();

                                cartService.deleteProductFromCart(loggedInUser, productId, scanner,productMap);
                                System.out.println("----Cart After Deletion-----");
                                cartService.displayCart(loggedInUser);
                                break;
                            case 5:
                                cartService.displayCart(loggedInUser);
                                break;
                            case 6:
                                cartService.checkout(loggedInUser);
                                break;
                            case 7:
                                break;
                        }

                        if (shopChoice == 7) {
                            break;
                        }

                    }

                    break;
                case 4:
                    cartService.displayCart(loggedInUser);
                    break;
                default:
                    scanner.close();
                    System.exit(0);
                    break;
            }
        }

    }

    public static void buildCatalog() {

        try {
            productMap = catalogService.loadProductToMap("metadata/products.json");
        } catch (FileNotFoundException e) {
            productMap = new LinkedHashMap<>();
            e.printStackTrace();
        }

        System.out.println(productMap.size());

    }

    private static void printMenu() {
        System.out.println("1. Signup User");
        System.out.println("2. Login User");
        System.out.println("3. Shop");
        System.out.println("4. View Cart");
        System.out.println("5. Exit");

    }

    private static void printShopMenu() {
        System.out.println("1. Select Category");
        System.out.println("2. Add Product to Cart");
        System.out.println("3. Modify Product in Cart");
        System.out.println("4. Delete Product from Cart");
        System.out.println("5. View Cart");
        System.out.println("6. Checkout Cart");
        System.out.println("7. Exit Shop");
    }

    private static void printCartModificationMenu() {
        System.out.println("1. Edit Variant");
        System.out.println("2. Delete Variant");
        System.out.println("3. Exit");
    }

    private static User checkUser(String name) {
        for (User user : User.getUsers()) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }
}
