package com.foodordersystem.ui.common;

import com.foodordersystem.model.entities.Order;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A utility class for generating a formatted receipt.
 */
public class Receipt {

    /**
     * Generates a formatted receipt string.
     *
     * @param order The order object containing total calculations.
     * @return A formatted string representing the receipt.
     */
    public static String generateReceipt(Order order) {
        int ref = 1234 + (int) (Math.random() * 5678);
        Calendar timer = Calendar.getInstance();

        SimpleDateFormat tTime = new SimpleDateFormat("HH:mm:ss");
        String time = tTime.format(timer.getTime());

        SimpleDateFormat Tdate = new SimpleDateFormat("dd-MM-yyyy");
        String date = Tdate.format(timer.getTime());

        StringBuilder receiptText = new StringBuilder();
        receiptText.append("Food Ordering System\n");
        receiptText.append("================================\n");
        receiptText.append("Ref: \t\t").append(ref).append("\n");
        receiptText.append("Date: ").append(date).append("\tTime: ").append(time).append("\n\n");
        receiptText.append("SubTotal: \t").append(String.format("Rs %.2f", order.getSubTotal())).append("\n");
        receiptText.append("Tax: \t\t").append(String.format("Rs %.2f", order.getDelivaryCharge())).append("\n");
        receiptText.append("--------------------------------\n");
        receiptText.append("Total: \t\t").append(String.format("Rs %.2f", order.getTotal())).append("\n");
        receiptText.append("================================\n");
        receiptText.append("Thanks for your order!");

        return receiptText.toString();
    }
}
