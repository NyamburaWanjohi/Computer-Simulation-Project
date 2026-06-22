public class Customer {
    // ---(Initialization of variables that will be generated)---
    private int id;
    private double interArrivalTime;
    private double serviceTime;

    // ---(Placehoders for AT, SST, WT,SET,TimeinSystem)---
    private double arrivalTime;
    private double serviceStartTime;    
    private double waitingTime;
    private double serviceEndTime;
    private double timeInSystem;

    // ---(Constructor to initialize the variables)---
    public Customer(int id, double interArrivalTime, double serviceTime) {
        this.id = id;
        this.interArrivalTime = interArrivalTime;
        this.serviceTime = serviceTime;
    }

    // ---(Getters and Setters for the variables)---
    public int getId() {
        return id;
    }
    public double getInterArrivalTime() {
        return interArrivalTime;
    }
    public double getServiceTime() {
        return serviceTime;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public double getServiceStartTime() {
        return serviceStartTime;
    }
    public void setServiceStartTime(double serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }
    public double getWaitingTime() {
        return waitingTime;
    }
    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }
    public double getServiceEndTime() {
        return serviceEndTime;
    }
    public void setServiceEndTime(double serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }
    public double getTimeInSystem() {
        return timeInSystem;
    }
    public void setTimeInSystem(double timeInSystem) {
        this.timeInSystem = timeInSystem;
    }
}