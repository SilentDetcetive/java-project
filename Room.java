import java.io.Serializable;

/**
 * 房间实体类
 * 存储房间的基本信息：房号、类型、价格、状态
 */
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    // ──────────── 房间状态常量 ────────────
    /** 可入住 */
    public static final String STATUS_FREE = "空闲";
    /** 已有人入住 */
    public static final String STATUS_OCCUPIED = "已入住";
    /** 退房后等待清洁，不可入住 */
    public static final String STATUS_CLEANING = "待清洗";
    /** 维修中，不可用 */
    public static final String STATUS_MAINTENANCE = "维修中";

    private String roomNo;   // 房间号，如 801
    private String type;     // 房间类型：单人间 / 双人间 / 套房
    private double price;    // 每晚价格
    private String status;   // 当前状态，取值为上面的常量

    public Room(String roomNo, String type, double price, String status) {
        this.roomNo = roomNo;
        this.type = type;
        this.price = price;
        this.status = status;
    }

    // ── getter ──
    public String getRoomNo() { return roomNo; }
    public String getType()   { return type; }
    public double getPrice()  { return price; }
    public String getStatus() { return status; }

    // ── setter（只有状态在运行时会变）──
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "房间号: " + roomNo
             + " | 类型: " + type
             + " | 价格: ￥" + price
             + " | 状态: " + status;
    }
}
