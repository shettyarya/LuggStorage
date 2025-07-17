package com.example.login_second;

import java.io.Serializable;
import java.time.LocalDate;

public class BookingDetails implements Serializable {
    private int bookingId;
    private LocalDate bookingDate;
    private int days;
    private int bags;
    private int paymentStatus;
    private int totalAmount;

    // Default constructor for JavaFX TableView and serialization
    public BookingDetails() {}

    public BookingDetails(int bookingId, LocalDate bookingDate, int days, int bags, int paymentStatus, int totalAmount) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.days = days;
        this.bags = bags;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getBags() {
        return bags;
    }

    public void setBags(int bags) {
        this.bags = bags;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
}
