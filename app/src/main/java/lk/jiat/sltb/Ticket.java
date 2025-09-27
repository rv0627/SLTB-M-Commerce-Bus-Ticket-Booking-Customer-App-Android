package lk.jiat.sltb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Ticket {

    private int bookingNumber;
    private String passengerFirstName;
    private String passengerLastName;
    private String email;
    private String mobileNumber;
    private String travelDate; // Format: yyyy-MM-dd
    private String departureTime; // Format: HH:mm
    private String busName;
    private String departureStation;
    private String arrivalStation;
    private int baseFare;
    private int stationFee;
    private int serviceCharge;
    private String status; // e.g., "Confirmed", "Pending", "Completed"
    private String seatNumber; // New field for seat selection
    private String route; // New field for bus route

    // Primary constructor with all fields
    public Ticket(int bookingNumber, String passengerFirstName, String passengerLastName, String email, String mobileNumber,
                  String travelDate, String departureTime, String busName, String departureStation, String arrivalStation,
                  int baseFare, int stationFee, int serviceCharge, String status, String seatNumber, String route) {
        this.bookingNumber = bookingNumber;
        this.passengerFirstName = passengerFirstName;
        this.passengerLastName = passengerLastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.travelDate = travelDate;
        this.departureTime = departureTime;
        this.busName = busName;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.baseFare = baseFare;
        this.stationFee = stationFee;
        this.serviceCharge = serviceCharge;
        this.status = status != null ? status : "Pending"; // Default status if not provided
        this.seatNumber = seatNumber;
        this.route = route;
    }

    // Overloaded constructor without the status field
    public Ticket(int bookingNumber, String passengerFirstName, String passengerLastName, String email, String mobileNumber,
                  String travelDate, String departureTime, String busName, String departureStation, String arrivalStation,
                  int baseFare, int stationFee, int serviceCharge, String seatNumber, String route) {
        this(bookingNumber, passengerFirstName, passengerLastName, email, mobileNumber, travelDate, departureTime,
                busName, departureStation, arrivalStation, baseFare, stationFee, serviceCharge, "Pending", seatNumber, route); // Default status
    }

    // Method to check if the travel date is today
    public boolean isToday() {
        if (travelDate == null || travelDate.isEmpty()) {
            return false; // Return false if the date is null or empty
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        return travelDate.equals(todayDate);
    }

    // Method to calculate total fare
    public int getTotalFare() {
        return baseFare + stationFee + serviceCharge;
    }

    // Getters and Setters
    public int getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(int bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public String getPassengerFirstName() {
        return passengerFirstName;
    }

    public void setPassengerFirstName(String passengerFirstName) {
        this.passengerFirstName = passengerFirstName;
    }

    public String getPassengerLastName() {
        return passengerLastName;
    }

    public void setPassengerLastName(String passengerLastName) {
        this.passengerLastName = passengerLastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public int getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(int baseFare) {
        this.baseFare = baseFare;
    }

    public int getStationFee() {
        return stationFee;
    }

    public void setStationFee(int stationFee) {
        this.stationFee = stationFee;
    }

    public int getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(int serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}