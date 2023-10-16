package com.saidi.book_store.dto;

public class PayPalResponse {
        private String orderId;

        public PayPalResponse() {
            // Default constructor required by Jackson
        }

        public PayPalResponse(String orderId) {
            this.orderId = orderId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }

