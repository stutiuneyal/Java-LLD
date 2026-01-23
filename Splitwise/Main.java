import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("Welcome to Split Wise");

        Scanner scanner = new Scanner(System.in);

        ExpenseManagement exp = new ExpenseManagement();

        while (true) {

            printMenu();

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Paid By: ");
                    String paidByName = scanner.nextLine();

                    System.out.println("Amount Paid: ");
                    Double amountPaid = validateAmountPaid(scanner);

                    System.out.println("Paid For(comma{,} separated names): ");
                    String paidForNameString = scanner.nextLine();

                    User paidBy = User.getuserByName(paidByName);
                    if (User.getuserByName(paidByName) == null) {
                        paidBy = createUsers(paidByName);
                    }

                    List<User> paidFor = new ArrayList<>();
                    String[] paidForNames = paidForNameString.trim().split(",");
                    for (String name : paidForNames) {
                        User user = createUsers(name);
                        paidFor.add(user);

                    }

                    exp.add(amountPaid, paidBy, paidFor);

                    break;
                case 2:
                    System.out.println("Paying User: ");
                    String payingUserName = scanner.nextLine();

                    System.out.println("Paying To User: ");
                    String payingToUserName = scanner.nextLine();

                    User payingUser = User.getuserByName(payingUserName);
                    if (payingUser == null) {
                        System.out.println(payingUserName + " does not exist/ has never owed anything");
                        printMenu();
                        continue;
                    }

                    User payingToUser = User.getuserByName(payingToUserName);
                    if (payingToUser == null) {
                        System.out.println(payingToUserName + " does not exist");
                        printMenu();
                        continue;
                    }

                    exp.settle(payingUser, payingToUser);

                    break;

                case 3:
                    exp.printOweDetails();
                    break;

                case 4:
                    exp.simplifyDebt();
                    break;

                case 5:
                    System.out.println("Thanks for using SplitWise!!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Please select a valid option from the menu");
                    printMenu();
                    break;
            }

        }

    }

    private static void printMenu() {
        System.out.println("Menu");
        System.out.println("1. Add Expenses");
        System.out.println("2. Settle Expenses");
        System.out.println("3. View Expenses");
        System.out.println("4. Simplify Expenses");
        System.out.println("5. Exit");
    }

    private static Double validateAmountPaid(Scanner scanner) {

        while (true) {

            String amount = scanner.nextLine();

            try {
                return Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                System.out.println("Amount paid should be an Integer/Double value. Please re-enter the amount");
            }

        }

    }

    private static User createUsers(String name) {

        User listUser = User.getuserByName(name);

        if (listUser == null) {
            User user = new User(name);
            User.users.add(user);
            return user;
        }

        return listUser;
    }

}
