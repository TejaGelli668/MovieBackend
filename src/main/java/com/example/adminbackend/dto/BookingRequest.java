package com.example.adminbackend.dto;

import java.util.List;

public class BookingRequest {
    private Long showId;
    private List<String> seatNumbers;

    private String paymentIntentId;
    private String paymentMethod;
    private Double totalAmount;


    // NEW: Add food items support
    private List<FoodItemRequest> foodItems;

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    // Existing getters/setters
    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }

    // NEW: Food items getters/setters
    public List<FoodItemRequest> getFoodItems() { return foodItems; }
    public void setFoodItems(List<FoodItemRequest> foodItems) { this.foodItems = foodItems; }

    // Inner class for food item requests
    public static class FoodItemRequest {
        private Long foodItemId;
        private Integer quantity;

        public Long getFoodItemId() { return foodItemId; }
        public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}