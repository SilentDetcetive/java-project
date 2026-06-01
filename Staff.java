import java.io.Serializable;

/**
 * 员工实体类
 * 存储酒店员工的基本信息
 */
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    private String staffId;   // 工号
    private String name;      // 姓名
    private String position;  // 职位
    private double salary;    // 薪资

    public Staff(String staffId, String name, String position, double salary) {
        this.staffId = staffId;
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    // ── getter ──
    public String getStaffId()  { return staffId; }
    public String getName()     { return name; }
    public String getPosition() { return position; }
    public double getSalary()   { return salary; }

    // ── setter（职位和薪资可能会变动）──
    public void setPosition(String position) { this.position = position; }
    public void setSalary(double salary)     { this.salary = salary; }

    @Override
    public String toString() {
        return "工号: " + staffId
             + " | 姓名: " + name
             + " | 职位: " + position
             + " | 薪资: ￥" + salary;
    }
}
