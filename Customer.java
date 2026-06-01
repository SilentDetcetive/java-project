import java.io.Serializable;

/**
 * 客户实体类
 * 存储入住客户的个人信息
 */
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;    // 姓名
    private String idCard;  // 身份证号
    private String phone;   // 手机号
    private String gender;  // 性别
    private int age;        // 年龄

    public Customer(String name, String idCard, String phone, String gender, int age) {
        this.name = name;
        this.idCard = idCard;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
    }

    // ── 所有字段的 getter ──
    public String getName()   { return name; }
    public String getIdCard() { return idCard; }
    public String getPhone()  { return phone; }
    public String getGender() { return gender; }
    public int getAge()       { return age; }

    @Override
    public String toString() {
        return "姓名: " + name
             + " | 身份证: " + idCard
             + " | 手机: " + phone
             + " | 性别: " + gender
             + " | 年龄: " + age;
    }
}
