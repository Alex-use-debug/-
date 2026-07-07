package entity;

import java.sql.Timestamp;

/** 停车记录实体 */
public class ParkingRecord {
    private int id;
    private String carNumber;
    private String carType;
    private Timestamp entryTime;
    private Timestamp exitTime;
    private String spotNumber;
    private double fee;

    public ParkingRecord() {}

    public ParkingRecord(int id, String carNumber, String carType, Timestamp entryTime,
                         Timestamp exitTime, String spotNumber, double fee) {
        this.id = id;
        this.carNumber = carNumber;
        this.carType = carType;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.spotNumber = spotNumber;
        this.fee = fee;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCarNumber() { return carNumber; }
    public void setCarNumber(String carNumber) { this.carNumber = carNumber; }
    public String getCarType() { return carType; }
    public void setCarType(String carType) { this.carType = carType; }
    public Timestamp getEntryTime() { return entryTime; }
    public void setEntryTime(Timestamp entryTime) { this.entryTime = entryTime; }
    public Timestamp getExitTime() { return exitTime; }
    public void setExitTime(Timestamp exitTime) { this.exitTime = exitTime; }
    public String getSpotNumber() { return spotNumber; }
    public void setSpotNumber(String spotNumber) { this.spotNumber = spotNumber; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
}
