package entity;

/** 停车位实体 */
public class ParkingSpot {
    private int id;
    private String spotNumber;
    private int status;       // 0=空闲, 1=已占用
    private String carNumber; // 占用该车位的车牌号

    public ParkingSpot() {}

    public ParkingSpot(int id, String spotNumber, int status, String carNumber) {
        this.id = id;
        this.spotNumber = spotNumber;
        this.status = status;
        this.carNumber = carNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSpotNumber() { return spotNumber; }
    public void setSpotNumber(String spotNumber) { this.spotNumber = spotNumber; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getCarNumber() { return carNumber; }
    public void setCarNumber(String carNumber) { this.carNumber = carNumber; }

    /** 是否空闲 */
    public boolean isFree() { return status == 0; }
}
