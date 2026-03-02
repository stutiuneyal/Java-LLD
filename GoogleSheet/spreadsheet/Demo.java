package spreadsheet;

public class Demo {
    public static void main(String[] args) {
        Spreadsheet sheet = new Spreadsheet();

        sheet.setValue("A1", 10);
        sheet.setValue("B1", 5);

        sheet.setFormula("C1", "A1 + B1 * 2");
        System.out.println("C1 = " + sheet.getValue("C1")); // 20

        sheet.setValue("B1", 7);
        System.out.println("C1 = " + sheet.getValue("C1")); // 24

        sheet.setFormula("D1", "C1 + 1");
        System.out.println("D1 = " + sheet.getValue("D1")); // 25

        try {
            sheet.setFormula("A1", "D1 + 1"); // likely cycle via A1 -> ... -> A1
        } catch (Exception e) {
            System.out.println("Expected cycle error: " + e.getMessage());
        }
    }
}
