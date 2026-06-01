import java.io.Serializable;
import java.time.LocalDate;

/**
 * 订单实体类
 * 记录每次入住 / 退房的完整信息
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    // ──────────── 订单状态常量 ────────────
    /** 客人当前在住 */
    public static final String STATUS_CHECKED_IN = "入住中";
    /** 客人已退房 */
    public static final String STATUS_CHECKED_OUT = "已退房";

    private String orderId;        // 订单编号，格式: ORD + 时间戳
    private Customer customer;     // 入住客户
    private Room room;             // 入住的房间
    private LocalDate checkInDate; // 入住日期
    private int days;              // 入住天数
    private double totalAmount;    // 总金额 = 单价 × 天数
    private String status;         // 订单状态，取值为上面的常量

    public Order(String orderId, Customer customer, Room room,
                 LocalDate checkInDate, int days, double totalAmount, String status) {
        this.orderId = orderId;
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.days = days;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // ── getter ──
    public String getOrderId()       { return orderId; }
    public Customer getCustomer()    { return customer; }
    public Room getRoom()            { return room; }
    public LocalDate getCheckInDate(){ return checkInDate; }
    public int getDays()             { return days; }
    public double getTotalAmount()   { return totalAmount; }
    public String getStatus()        { return status; }

    // ── setter（只有状态在运行时会变）──
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "订单号: " + orderId
             + " | 客户: " + customer.getName()
             + " | 房间: " + room.getRoomNo()
             + " | 入住日期: " + checkInDate
             + " | 天数: " + days
             + " | 总价: ￥" + totalAmount
             + " | 状态: " + status;
    }
}
