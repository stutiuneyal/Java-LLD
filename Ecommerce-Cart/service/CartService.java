package service;

import java.util.Map;
import java.util.Scanner;

import Actors.Cart;
import Actors.CartProduct;
import Actors.User;
import Actors.Catalog.Catalog;
import Actors.Catalog.Product;
import Actors.Catalog.Variants;
import utils.PlatformUtilEnum;
import utils.coupons.CouponEnum;

public class CartService {

    /*
     * Display Categories
     */
    public void displayCategories() {

        for (String catgory : Catalog.getCategoriesMap().keySet()) {
            System.out.println(catgory);
        }

    }

    /*
     * Perform Cart Operations
     */
    public void addItemsToCart(User loggedInUser, String productId, String variant, int qty, Scanner scanner,
            Map<String, Product> productMap) {

        /*
         * ADD TO CART (Variant aware + Inventory update)
         *
         * Rules implemented:
         * 1) User must be logged in
         * 2) Product must exist in productMap
         * 3) Variant must exist in product catalog for that product
         * 4) Validate product constraints (min/max/total) using final required qty
         * 5) Validate variant availability using final required qty
         * 6) If same productId+variant already in cart -> increase qty
         * Else -> add new cart line item for that variant
         * 7) Update inventory:
         * - Reduce product.totalQuantity by qty added
         * - Reduce variant.availableQuantity by qty added
         * 8) Recalculate cart costs (tax/shipping/special/coupon) using existing helper
         * 9) Display cart
         */

        // 1) logged-in user check
        if (loggedInUser == null) {
            System.out.println("Please login");
            return;
        }

        // 2) product existence check
        if (productId == null || productId.trim().isEmpty() || !productMap.containsKey(productId)) {
            System.out.println("Product is not present in the map");
            return;
        }

        // 3) qty validation
        if (qty <= 0) {
            System.out.println("Quantity should be greater than 0");
            return;
        }

        Product product = productMap.get(productId);

        // 4) variant existence check in catalog
        Variants requestedVariant = getProductVariants(product, variant);
        if (requestedVariant == null) {
            System.out.println("Variant '" + variant + "' not found for product " + productId);
            return;
        }

        // build the requested variant string once (e.g. "XL#")
        String requestedVariantStr = getVariantsString(requestedVariant);

        Cart cart = loggedInUser.getCart();
        if (cart == null) {
            System.out.println("Cart is not initialized for user.");
            return;
        }
        if (cart.getProducts() == null) {
            System.out.println("Cart products list is not initialized.");
            return;
        }

        // -------------------------------------------------------
        // Find if SAME productId + SAME variant already exists in cart
        // -------------------------------------------------------
        CartProduct matchedCartLine = null;
        for (CartProduct cp : cart.getProducts()) {
            if (cp != null
                    && productId.equals(cp.getProductId())
                    && requestedVariantStr.equals(cp.getVariants())) {
                matchedCartLine = cp;
                break;
            }
        }

        // -------------------------------------------------------
        // Determine final required qty for this product+variant line
        // -------------------------------------------------------
        int finalQtyForThisVariantLine = qty; // if new line
        if (matchedCartLine != null) {
            finalQtyForThisVariantLine = matchedCartLine.getQty() + qty; // if existing line
        }

        // -------------------------------------------------------
        // 5) Validate product constraints and variant availability
        // IMPORTANT:
        // - Product constraints checked against final qty for this variant line
        // - Variant availability checked against qty to be ADDED (inventory delta)
        // -------------------------------------------------------

        // Product-level constraints (min/max per order and total available at product
        // level)
        // NOTE: product.totalQuantity is reduced when cart adds items (inventory update
        // below)
        if (!canAddToCart(product, finalQtyForThisVariantLine)) {
            System.out.println("Cannot add to cart. Quantity violates product constraints "
                    + "(minOrderQty/maxPerOrder/totalQuantity).");
            return;
        }

        // Variant-level inventory check:
        // Only the new qty being added must be <= availableQuantity (because inventory
        // is decremented per add)
        if (!canVariantBeAdded(requestedVariant, qty)) {
            System.out.println("Cannot add to cart. Not enough stock for variant '" + variant + "'.");
            return;
        }

        // -------------------------------------------------------
        // 6) Update cart line (existing or new)
        // -------------------------------------------------------
        if (matchedCartLine != null) {
            // Existing productId+variant: just increase qty
            matchedCartLine.setQty(matchedCartLine.getQty() + qty);

            // Cart total increases by (added qty * unit price)
            cart.setCartTotal(cart.getCartTotal() + (qty * matchedCartLine.getUnitPrice()));
        } else {
            // New line for this productId+variant
            CartProduct newProduct = new CartProduct();
            newProduct.setProductId(productId);
            newProduct.setQty(qty);
            newProduct.setUnitPrice(product.getBasePrice());
            newProduct.setVariants(requestedVariantStr);

            cart.getProducts().add(newProduct);

            // Cart total increases by (added qty * base price)
            cart.setCartTotal(cart.getCartTotal() + (qty * product.getBasePrice()));
        }

        // -------------------------------------------------------
        // 7) INVENTORY UPDATE (Catalog mutation)
        // - Reduce product.totalQuantity by qty added
        // - Reduce variant.availableQuantity by qty added
        // -------------------------------------------------------
        product.setTotalQuantity(product.getTotalQuantity() - qty);
        requestedVariant.setAvailableQuantity(requestedVariant.getAvailableQuantity() - qty);

        // -------------------------------------------------------
        // 8) Recalculate cart costs + coupon using existing helper
        // (This avoids duplicate TAX/SHIPPING entries and considers ALL products in
        // cart)
        // -------------------------------------------------------
        recalculateCartAmounts(cart, scanner, productMap);

        // 9) Display cart
        displayCart(loggedInUser);
    }

    public void editCartVariant(User loggedInUser, String productId, String variantName, int variantQty,
            Scanner scanner, Map<String, Product> productMap) {

        /*
         * EDIT CART VARIANT (Variant aware + Inventory update)
         *
         * What this function supports:
         * - Update quantity of an existing (productId + variant) line
         * - Add a new (productId + variant) line if not present
         * - Delete a (productId + variant) line if variantQty == 0 or -1
         *
         * Inventory Update rules:
         * - If qty increases: decrease inventory by (newQty - oldQty)
         * - If qty decreases: increase inventory by (oldQty - newQty)
         * - If deleted: restore inventory by (oldQty)
         */

        // 1) Check logged in user
        if (loggedInUser == null) {
            System.out.println("Please login");
            return;
        }

        Cart cart = loggedInUser.getCart();
        if (cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        // 2) Resolve product from catalog
        if (productId == null || productId.trim().isEmpty()) {
            System.out.println("Invalid productId.");
            return;
        }

        Product catalogProduct = productMap.get(productId);
        if (catalogProduct == null) {
            System.out.println("Product not found in catalog.");
            return;
        }

        // 3) Validate requested variant exists in catalog
        Variants requestedVariant = getProductVariants(catalogProduct, variantName);
        if (requestedVariant == null) {
            System.out.println("Variant '" + variantName + "' not found for product " + productId);
            return;
        }
        String requestedVariantStr = getVariantsString(requestedVariant); // e.g. "XL#"

        // 4) Find matching cart line (productId + variant)
        CartProduct matchedCartLine = null;
        for (CartProduct cp : cart.getProducts()) {
            if (cp != null
                    && productId.equals(cp.getProductId())
                    && requestedVariantStr.equals(cp.getVariants())) {
                matchedCartLine = cp;
                break;
            }
        }

        // 5) Deletion (qty == 0 or -1)
        if (variantQty == 0 || variantQty == -1) {

            if (matchedCartLine == null) {
                System.out.println("No such product+variant found in cart to delete.");
                return;
            }

            int oldQty = matchedCartLine.getQty();

            // ---- INVENTORY RESTORE (because we are removing this variant line completely)
            // ----
            catalogProduct.setTotalQuantity(catalogProduct.getTotalQuantity() + oldQty);
            requestedVariant.setAvailableQuantity(requestedVariant.getAvailableQuantity() + oldQty);

            // Remove from cart and update totals
            double lineTotal = oldQty * matchedCartLine.getUnitPrice();
            cart.getProducts().remove(matchedCartLine);
            cart.setCartTotal(cart.getCartTotal() - lineTotal);

            System.out.println("Item removed from cart: " + productId + " (" + requestedVariantStr + ")");

            // Recalculate costs + coupon based on updated cart
            if (cart.getProducts().isEmpty()) {
                // if cart became empty, reset coupon and costs cleanly
                cart.setCoupon(null);
                cart.setCartTotal(0);
                cart.setCartAmountAfterDeductions(0);
                if (cart.getPlatformCosts() != null) {
                    cart.getPlatformCosts().clear();
                }
                System.out.println("Cart is now empty.");
            } else {
                recalculateCartAmounts(cart, scanner, productMap);
            }

            displayCart(loggedInUser);
            return;
        }

        // 6) Modification / Add (qty must be > 0)
        if (variantQty <= 0) {
            System.out.println("Invalid quantity. Quantity should be greater than 0.");
            return;
        }

        // --------------------------
        // CASE A: Variant line exists -> update qty (and inventory delta)
        // --------------------------
        if (matchedCartLine != null) {

            int oldQty = matchedCartLine.getQty();
            int newQty = variantQty;

            if (newQty == oldQty) {
                System.out.println("No change in quantity. Nothing to update.");
                displayCart(loggedInUser);
                return;
            }

            // Product-level constraints check for the FINAL qty
            if (!canAddToCart(catalogProduct, newQty)) {
                System.out.println("Quantity violates product constraints (min/max/total).");
                return;
            }

            // Calculate inventory delta
            int delta = newQty - oldQty;

            if (delta > 0) {
                // Increasing qty: need extra inventory
                if (!canVariantBeAdded(requestedVariant, delta)) {
                    System.out
                            .println("Not enough availability for variant " + variantName + " to increase by " + delta);
                    return;
                }
                // ---- INVENTORY DECREASE (we are adding more units into cart) ----
                catalogProduct.setTotalQuantity(catalogProduct.getTotalQuantity() - delta);
                requestedVariant.setAvailableQuantity(requestedVariant.getAvailableQuantity() - delta);
            } else {
                // Decreasing qty: restore inventory
                int restore = (-delta);
                // ---- INVENTORY INCREASE (we are removing units from cart) ----
                catalogProduct.setTotalQuantity(catalogProduct.getTotalQuantity() + restore);
                requestedVariant.setAvailableQuantity(requestedVariant.getAvailableQuantity() + restore);
            }

            // Update cart line qty
            matchedCartLine.setQty(newQty);

            // Update cart total using difference
            double diff = (newQty - oldQty) * matchedCartLine.getUnitPrice();
            cart.setCartTotal(cart.getCartTotal() + diff);

            System.out.println("Updated quantity for " + productId + " (" + requestedVariantStr + ") to " + newQty);

        } else {
            // --------------------------
            // CASE B: Variant line does not exist -> add new cart line (and inventory
            // decrease)
            // --------------------------

            // Product-level constraints for the new line qty
            if (!canAddToCart(catalogProduct, variantQty)) {
                System.out.println("Quantity violates product constraints (min/max/total).");
                return;
            }

            // Variant inventory check for qty being added
            if (!canVariantBeAdded(requestedVariant, variantQty)) {
                System.out.println("Not enough availability for variant " + variantName);
                return;
            }

            CartProduct newLine = new CartProduct();
            newLine.setProductId(productId);
            newLine.setVariants(requestedVariantStr);
            newLine.setQty(variantQty);
            newLine.setUnitPrice(catalogProduct.getBasePrice());

            cart.getProducts().add(newLine);
            cart.setCartTotal(cart.getCartTotal() + (variantQty * newLine.getUnitPrice()));

            // ---- INVENTORY DECREASE (we are putting these units into cart) ----
            catalogProduct.setTotalQuantity(catalogProduct.getTotalQuantity() - variantQty);
            requestedVariant.setAvailableQuantity(requestedVariant.getAvailableQuantity() - variantQty);

            System.out.println(
                    "Added new variant to cart: " + productId + " (" + requestedVariantStr + "), qty=" + variantQty);
        }

        // 7) Recalculate totals + coupon after update
        recalculateCartAmounts(cart, scanner, productMap);

        displayCart(loggedInUser);
    }

    public void deleteProductFromCart(User loggedInUser, String productId, Scanner scanner,
            Map<String, Product> productMap) {

        /*
         * DELETE PRODUCT FROM CART (All variants) + Inventory restore
         *
         * What this does:
         * 1) Validates user + cart
         * 2) Removes ALL cart lines having the given productId (i.e., all variants)
         * 3) Restores inventory back to catalog:
         * - product.totalQuantity += removedQty
         * - variant.availableQuantity += removedQty (for that removed variant)
         * 4) Updates cart total
         * 5) Recalculates charges/coupon OR resets cart values if cart becomes empty
         * 6) Displays cart
         */

        if (loggedInUser == null) {
            System.out.println("Please login");
            return;
        }

        Cart cart = loggedInUser.getCart();
        if (cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        if (productId == null || productId.trim().isEmpty()) {
            System.out.println("Invalid productId.");
            return;
        }

        // Get catalog product for inventory restore
        Product catalogProduct = productMap.get(productId);
        if (catalogProduct == null) {
            System.out.println("Product not found in catalog.");
            return;
        }

        boolean found = false;
        double removedTotal = 0;

        // Iterate backwards so removal is safe
        for (int i = cart.getProducts().size() - 1; i >= 0; i--) {

            CartProduct cp = cart.getProducts().get(i);

            if (cp != null && productId.equals(cp.getProductId())) {
                found = true;

                int removedQty = cp.getQty();
                removedTotal += (removedQty * cp.getUnitPrice());

                // ---------------- INVENTORY RESTORE ----------------
                // Restore product total quantity
                catalogProduct.setTotalQuantity(catalogProduct.getTotalQuantity() + removedQty);

                // Restore variant availability based on the variant string stored in cart
                // Example: cp.getVariants() = "XL#"
                String cartVariantStr = cp.getVariants(); // "XL#"
                String cartVariantCode = cartVariantStr;

                // extract code from "XL#" -> "XL"
                if (cartVariantCode != null && cartVariantCode.endsWith("#")) {
                    cartVariantCode = cartVariantCode.substring(0, cartVariantCode.length() - 1);
                }

                Variants catalogVariant = getProductVariants(catalogProduct, cartVariantCode);
                if (catalogVariant != null) {
                    catalogVariant.setAvailableQuantity(catalogVariant.getAvailableQuantity() + removedQty);
                }
                // ---------------------------------------------------

                // Remove this cart line
                cart.getProducts().remove(i);
            }
        }

        if (!found) {
            System.out.println("Product not found in cart: " + productId);
            return;
        }

        // Update cart total
        cart.setCartTotal(cart.getCartTotal() - removedTotal);

        // If cart is empty after removal, reset coupon/costs/amount
        if (cart.getProducts().isEmpty()) {
            cart.setCoupon(null);
            cart.setCartTotal(0);
            cart.setCartAmountAfterDeductions(0);

            if (cart.getPlatformCosts() != null) {
                cart.getPlatformCosts().clear();
            }

            System.out.println("Removed product " + productId + " from cart. Cart is now empty.");
            return;
        }

        System.out.println("Removed product " + productId + " (all variants) from cart.");

        // Recalculate totals + coupon (existing helper)
        recalculateCartAmounts(cart, scanner, productMap);

        displayCart(loggedInUser);
    }

    /*
     * Display Cart(UserId)
     */
    public void displayCart(User loggedInUser) {

        if (loggedInUser == null) {
            System.out.println("Please login to view cart.");
            return;
        }

        Cart cart = loggedInUser.getCart();

        if (cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("\n==================== YOUR CART ====================");
        System.out.println("User : " + loggedInUser.getName());
        System.out.println("---------------------------------------------------");

        System.out.printf("%-12s %-10s %-6s %-10s %-10s%n",
                "Product ID", "Variant", "Qty", "Unit Price", "Total");

        System.out.println("---------------------------------------------------");

        for (CartProduct cartProduct : cart.getProducts()) {
            double lineTotal = cartProduct.getQty() * cartProduct.getUnitPrice();

            System.out.printf("%-12s %-10s %-6d %-10.2f %-10.2f%n",
                    cartProduct.getProductId(),
                    cartProduct.getVariants(),
                    cartProduct.getQty(),
                    cartProduct.getUnitPrice(),
                    lineTotal);
        }

        System.out.println("---------------------------------------------------");
        System.out.printf("Cart Total                : %.2f%n", cart.getCartTotal());

        if (cart.getPlatformCosts() != null && !cart.getPlatformCosts().isEmpty()) {
            System.out.println("Applied Charges           : " + cart.getPlatformCosts());
        }

        if (cart.getCoupon() != null && !cart.getCoupon().isEmpty()) {
            System.out.println("Applied Coupon            : " + cart.getCoupon());
        }

        System.out.printf("Amount Payable             : %.2f%n",
                cart.getCartAmountAfterDeductions());

        System.out.println("===================================================\n");
    }

    /*
     * Checkout Cart
     */
    public void checkout(User loggedIUser) {
        if (loggedIUser == null) {
            System.out.println("Please Login");
            return;
        }

        System.out.println("Thanks for shopping, your order of amount Rs. "
                + loggedIUser.getCart().getCartAmountAfterDeductions() + " has been placed succssfully.");
        loggedIUser.setCart(new Cart());
        return;
    }

    private Variants getProductVariants(Product product, String variants) {
        for (Variants variant : product.getVariants()) {
            if (variant.getCode().equalsIgnoreCase(variants)) {
                return variant;
            }
        }

        return null;
    }

    private boolean canAddToCart(Product product, int qty) {
        return product.getTotalQuantity() >= qty && qty >= product.getMinOrderQty() && qty <= product.getMaxPerOrder();
    }

    private boolean canVariantBeAdded(Variants productVariant, int qty) {
        return productVariant.getAvailableQuantity() >= qty;
    }

    private String getVariantsString(Variants variants) {
        StringBuilder sb = new StringBuilder();

        sb.append(variants.getCode()).append("#");

        return sb.toString();
    }

    private void recalculateCartAmounts(Cart cart, Scanner scanner, Map<String, Product> productMap) {
        // reset platform costs fresh to avoid duplicates
        if (cart.getPlatformCosts() == null) {
            cart.setPlatformCosts(new java.util.ArrayList<>());
        } else {
            cart.getPlatformCosts().clear();
        }

        // base tax
        double taxPercentage = 5;
        cart.getPlatformCosts().add(PlatformUtilEnum.TAX);

        // if ANY product in cart requires special/shipping, apply (simple CLI rule)
        boolean anySpecial = false;
        boolean anyShipping = false;

        for (CartProduct cp : cart.getProducts()) {
            if (cp == null)
                continue;
            Product p = productMap.get(cp.getProductId());
            if (p == null || p.getConstraints() == null)
                continue;

            if (p.getConstraints().isRequiresSpecialHandling())
                anySpecial = true;
            if (p.getConstraints().isShippingRequired())
                anyShipping = true;
        }

        if (anySpecial) {
            cart.getPlatformCosts().add(PlatformUtilEnum.SPECIAL_HANDLING);
            taxPercentage += 5;
        }
        if (anyShipping) {
            cart.getPlatformCosts().add(PlatformUtilEnum.SHIPPING_FEES);
            taxPercentage += 7.5;
        }

        double cartAmount = cart.getCartTotal() + (cart.getCartTotal() * taxPercentage) / 100d;

        // Apply coupon if already present, else ask
        if (cart.getCoupon() != null && !cart.getCoupon().trim().isEmpty()) {
            try {
                CouponEnum coupon = CouponEnum.valueOf(cart.getCoupon().trim());
                System.out.println("Applying Coupon: " + coupon.name());
                cartAmount = coupon.applyDiscount(cartAmount);
                cart.setCartAmountAfterDeductions(cartAmount);
            } catch (Exception e) {
                System.out.println("Stored coupon is invalid. Removing coupon.");
                cart.setCoupon(null);
                cart.setCartAmountAfterDeductions(cartAmount);
            }
        } else {
            while (true) {
                System.out.println("Enter Coupon Name: (Type quit if you don't want to apply coupon)");
                String coupon = scanner.nextLine();

                if (coupon == null)
                    coupon = "";
                coupon = coupon.trim();

                if (coupon.equalsIgnoreCase("quit")) {
                    break;
                }

                try {
                    CouponEnum couponValue = CouponEnum.valueOf(coupon);
                    cartAmount = couponValue.applyDiscount(cartAmount);
                    cart.setCoupon(couponValue.name());
                    cart.getPlatformCosts().add(PlatformUtilEnum.COUPON);
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid Coupon");
                }
            }
            cart.setCartAmountAfterDeductions(cartAmount);
        }
    }
}
