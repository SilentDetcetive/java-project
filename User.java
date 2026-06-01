import java.io.Serializable;

/**
 * 用户实体类
 * 存储系统登录账号信息
 *
 * 注意：当前密码以明文存储，仅适用于课程演示。
 * 真实系统中应使用哈希（如 BCrypt / SHA-256 + 盐值）存储密码。
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username; // 登录用户名
    private String password; // 登录密码（明文，仅演示用途）

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    /**
     * 验证用户名和密码是否匹配
     * @return true 表示验证通过
     */
    public boolean login(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername)
            && this.password.equals(inputPassword);
    }
}
