package service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import Actors.Catalog.Catalog;
import Actors.Catalog.Product;
import Actors.Catalog.Variants;
import utils.Utils;

public class CatalogService {

    public Map<String, Product> loadProductToMap(String jsonFilePath) throws FileNotFoundException {
        Gson gson = new Gson();
        Catalog catalog = gson.fromJson(new FileReader(jsonFilePath), Catalog.class);

        Map<String, Product> productMap = new LinkedHashMap<>();
        if (catalog != null && catalog.getProducts() != null) {
            for (Product p : catalog.getProducts()) {
                if (p != null && p.getId() != null && !p.getId().trim().isEmpty()) {
                    productMap.put(p.getId(), p);
                }
            }
        }

        populateCategoriesMap(productMap);

        return productMap;
    }

    public void populateCategoriesMap(Map<String, Product> productsMap) {

        if (productsMap != null && !productsMap.isEmpty()) {
            for (String key : productsMap.keySet()) {
                Product p = productsMap.get(key);
                if (p != null && !Catalog.categoriesMap.containsKey(p.getCategory())) {
                    List<Product> prodList = new ArrayList<>();
                    prodList.add(p);
                    Catalog.categoriesMap.put(p.getCategory(), prodList);
                } else if (p != null && Catalog.categoriesMap.containsKey(p.getCategory())) {
                    List<Product> prodList = Catalog.categoriesMap.get(p.getCategory());
                    prodList.add(p);
                    Catalog.categoriesMap.put(p.getCategory(), prodList);
                }
            }
        }

        // System.out.println("Category Filled: "+Catalog.categoriesMap.size()+"
        // "+Catalog.categoriesMap.values().size());
    }

    public void displayProductsByCategoryName(String categoryName) {

        List<Product> products = Catalog.getCategoriesMap().get(categoryName);
        if (products == null || products.isEmpty()) {
            System.out.println("No products found for category: " + categoryName);
            return;
        }

        for (Product p : products) {
            if (p != null) {
                if (p.getTotalQuantity() > 0 && p.isActive()
                        && p.getTotalQuantity() >= p.getMinOrderQty()
                        && !Utils.isExpired(p.getConstraints())) {

                    System.out.printf(
                            "Id: %s, Name: %s, Base Price: %.2f, Returnable: %b, Special Handling: %s, Shipping Required: %s\n",
                            p.getId(), p.getName(), p.getBasePrice(), p.isReturnable(),
                            p.getConstraints() != null ? (p.getConstraints().isRequiresSpecialHandling() ? "yes" : "no")
                                    : "no",
                            p.getConstraints() != null ? (p.getConstraints().isShippingRequired() ? "yes" : "no")
                                    : "no");

                    System.out.print("Variants: ");
                    printVariants(p.getVariants());
                    System.out.println();
                }
            }
        }
    }

    private void printVariants(List<Variants> variants) {
        if (variants == null || variants.isEmpty()) {
            System.out.println("None");
            return;
        }

        boolean printed = false;
        for (Variants variant : variants) {
            if (variant != null && variant.isActive() && variant.getAvailableQuantity() > 0) {
                System.out.print(variant.getCode() + " "+variant.getAvailableQuantity()+", ");
                printed = true;
            }
        }
        if (!printed) {
            System.out.print("None");
        }
    }

}
