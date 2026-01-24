package service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;

import Actors.Catalog.Catalog;
import Actors.Catalog.Product;

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
                if (p != null && !Catalog.categoriesList.contains(p.getCategory())) {
                    Catalog.categoriesList.add(p.getCategory());
                }
            }
        }

        System.out.println("Category Filled: "+Catalog.categoriesList.size());
    }

}
