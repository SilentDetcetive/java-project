import java.io.Serializable;

/**
 * 会员卡实体类
 * 一张卡绑定一个身份证号，充值可解锁 VIP 等级，余额可抵扣房费。
 */
public class MemberCard implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cardId;      // 卡号，格式: VIP + 时间戳
    private String idCard;      // 绑定的身份证号（唯一）
    private String name;        // 持卡人姓名
    private double balance;     // 卡内余额（可消费）
    private double totalTopUp;  // 累计充值金额（决定 VIP 等级）

    public MemberCard(String cardId, String idCard, String name,
                      double balance, double totalTopUp) {
        this.cardId = cardId;
        this.idCard = idCard;
        this.name = name;
        this.balance = balance;
        this.totalTopUp = totalTopUp;
    }

    // ── getter ──
    public String getCardId()     { return cardId; }
    public String getIdCard()     { return idCard; }
    public String getName()       { return name; }
    public double getBalance()    { return balance; }
    public double getTotalTopUp() { return totalTopUp; }

    // ── setter（余额和累计充值会变动）──
    public void setBalance(double balance)       { this.balance = balance; }
    public void setTotalTopUp(double totalTopUp) { this.totalTopUp = totalTopUp; }
    public void setName(String name)             { this.name = name; }

    @Override
    public String toString() {
        return "卡号: " + cardId
             + " | 持卡人: " + name
             + " | 身份证: " + idCard
             + " | 余额: ￥" + String.format("%.2f", balance)
             + " | 累计充值: ￥" + String.format("%.2f", totalTopUp);
    }
}
