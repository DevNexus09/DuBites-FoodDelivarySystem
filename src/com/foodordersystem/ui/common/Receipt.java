package com.foodordersystem.ui.common;

import com.foodordersystem.model.entities.Order;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Receipt {

    public static String generateReceipt(Order order) {
        int ref = 1325 + (int) (Math.random() * 4238);
        Calendar timer = Calendar.getInstance();
        Date currentTime = timer.getTime();

        SimpleDateFormat tTime = new SimpleDateFormat("HH:mm:ss");
        String time = tTime.format(currentTime);

        SimpleDateFormat Tdate = new SimpleDateFormat("dd-MM-yyyy");
        String date = Tdate.format(currentTime);

        StringBuilder receiptText = new StringBuilder();
        receiptText.append("Receipt\n");
        receiptText.append("========================================\n");
        receiptText.append("Ref:\t\t\t").append(ref).append("\n");
        receiptText.append("Date: ").append(date).append("\tTime: ").append(time).append("\n");
        receiptText.append("========================================\n");
        receiptText.append("SubTotal:\t\t").append(String.format("Bdt %.2f", order.getSubTotal())).append("\n");
        receiptText.append("Delivery Charge:\t").append(String.format("Bdt %.2f", order.getDeliveryCharge())).append("\n");
        receiptText.append("----------------------------------------\n");
        receiptText.append("Total:\t\t\t").append(String.format("Bdt %.2f", order.getTotal())).append("\n");
        receiptText.append("========================================\n");
        receiptText.append("\nThanks for your order!\n");

        return receiptText.toString();
    }
}