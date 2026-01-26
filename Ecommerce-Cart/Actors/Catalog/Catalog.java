package Actors.Catalog;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Catalog {

    private List<Product> products;
    public static final Map<String,List<Product>> categoriesMap = new LinkedHashMap<>(); // can make it final because we wont be deleting the products from the catalog, but will not display products with quantity 0

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public static Map<String,List<Product>> getCategoriesMap() {
        return categoriesMap;
    }

}
