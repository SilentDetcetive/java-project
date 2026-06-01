import java.time.LocalDate;
import java.util.List;

/**
 * 酒店业务管理核心类
 * 负责登录验证、房间管理、入住退房、订单管理、员工管理
 *
 * 所有数据在启动时从 .dat 文件加载到内存，每次修改后立即写回文件。
 */
public class HotelManager {

    private List<Room> rooms;
    private List<Customer> customers;
    private List<Order> orders;
    private List<Staff> staffs;
    private List<User> users;
    private List<MemberCard> memberCards;

    // ──────────── getter（供 UI 层遍历数据）────────────
    public List<Room> getRooms()         { return rooms; }
    public List<Order> getOrders()       { return orders; }
    public List<Staff> getStaffs()       { return staffs; }
    public List<MemberCard> getMemberCards() { return memberCards; }

    // ──────────── 构造函数：从文件加载数据 ────────────
    public HotelManager() {
        rooms     = FileUtil.loadData("rooms.dat");
        customers = FileUtil.loadData("customers.dat");
        orders    = FileUtil.loadData("orders.dat");
        staffs    = FileUtil.loadData("staffs.dat");
        users        = FileUtil.loadData("users.dat");
        memberCards  = FileUtil.loadData("memberCards.dat");

        // 首次启动：创建默认管理员账号
        if (users.isEmpty()) {
            users.add(new User("admin", "123456"));
            FileUtil.saveData(users, "users.dat");
        }
    }

    // ════════════════════ 登录与注册 ════════════════════

    /** 验证用户名密码，成功返回 true */
    public boolean login(String username, String password) {
        for (User u : users) {
            if (u.login(username, password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注册新账号
     * @return null 表示注册成功；非 null 的字符串为错误原因
     */
    public String register(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空！";
        }
        if (password == null || password.trim().isEmpty()) {
            return "密码不能为空！";
        }
        if (username.trim().length() < 3) {
            return "用户名至少需要 3 个字符！";
        }
        if (password.trim().length() < 4) {
            return "密码至少需要 4 个字符！";
        }
        // 检查用户名是否已存在
        for (User u : users) {
            if (u.getUsername().equals(username.trim())) {
                return "该用户名已被注册！";
            }
        }
        users.add(new User(username.trim(), password.trim()));
        FileUtil.saveData(users, "users.dat");
        return null; // null 表示成功
    }

    // ════════════════════ 房间管理 ════════════════════

    /**
     * 添加新房间
     * @return null 表示成功；非 null 为错误原因
     */
    public String addRoom(Room room) {
        // 验证价格
        if (room.getPrice() <= 0) {
            return "价格必须大于 0！";
        }
        // 检查房号是否重复
        for (Room r : rooms) {
            if (r.getRoomNo().equals(room.getRoomNo())) {
                return "房间号 " + room.getRoomNo() + " 已存在，请勿重复添加！";
            }
        }
        rooms.add(room);
        FileUtil.saveData(rooms, "rooms.dat");
        return null; // null 表示成功
    }

    /** 在控制台打印所有房间信息 */
    public void listRooms() {
        if (rooms.isEmpty()) {
            System.out.println("暂无房间信息。");
            return;
        }
        for (Room room : rooms) {
            System.out.println(room);
        }
    }

    // ════════════════════ 入住与退房 ════════════════════

    /**
     * 办理入住
     * @param customer 客户信息
     * @param roomNo   要入住的房间号
     * @param days     入住天数
     * @return null 表示成功；非 null 为错误原因
     */
    public String checkIn(Customer customer, String roomNo, int days) {
        // 验证入住天数
        if (days <= 0) {
            return "入住天数必须大于 0！";
        }

        // 查找空闲房间
        Room targetRoom = null;
        for (Room room : rooms) {
            if (room.getRoomNo().equals(roomNo)
                    && room.getStatus().equals(Room.STATUS_FREE)) {
                targetRoom = room;
                break;
            }
        }

        if (targetRoom == null) {
            return "入住失败：房间 " + roomNo + " 不存在或非空闲状态！";
        }

        // 记录客户信息
        customers.add(customer);

        // 更新房间状态
        targetRoom.setStatus(Room.STATUS_OCCUPIED);

        // 生成订单
        String orderId = "ORD" + System.currentTimeMillis();
        double totalAmount = targetRoom.getPrice() * days;

        // VIP 客户折扣（历史订单消费 + 会员卡充值，取较高者定级）
        double combinedSpending = getCombinedSpending(customer.getIdCard());
        if (combinedSpending >= 10000) {
            totalAmount *= 0.85;      // 钻石会员 8.5折
        } else if (combinedSpending >= 5000) {
            totalAmount *= 0.90;      // 金卡会员 9折
        } else if (combinedSpending >= 1000) {
            totalAmount *= 0.95;      // 银卡会员 9.5折
        }

        // 满7天额外9折（可与VIP折扣叠加）
        if (days >= 7) {
            totalAmount *= 0.9;
        }
        Order order = new Order(orderId, customer, targetRoom,
                LocalDate.now(), days, totalAmount, Order.STATUS_CHECKED_IN);
        orders.add(order);

        // 保存所有变更
        FileUtil.saveData(customers, "customers.dat");
        FileUtil.saveData(rooms, "rooms.dat");
        FileUtil.saveData(orders, "orders.dat");

        return null; // null 表示成功，调用方可取订单号：order.getOrderId()
    }

    /**
     * 根据订单号和房间号生成的最新订单来获取订单号（供外部调用）
     * 由于 checkIn 返回 null 表示成功，UI 可通过此方法获取最近一笔订单
     */
    public String getLastOrderId() {
        if (orders.isEmpty()) return null;
        return orders.get(orders.size() - 1).getOrderId();
    }

    /**
     * 办理退房
     * @param orderId 订单号
     * @return null 表示成功；非 null 为错误原因
     */
    public String checkOut(String orderId) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)
                    && order.getStatus().equals(Order.STATUS_CHECKED_IN)) {
                order.setStatus(Order.STATUS_CHECKED_OUT);
                order.getRoom().setStatus(Room.STATUS_CLEANING); // 退房后房间需要清洁

                FileUtil.saveData(rooms, "rooms.dat");
                FileUtil.saveData(orders, "orders.dat");
                return null; // null 表示成功
            }
        }
        return "退房失败：未找到该入住中的订单，请检查订单号！";
    }

    // ════════════════════ 清洁管理 ════════════════════

    /**
     * 手动标记房间为"待清洗"
     * @param roomNo 房间号
     * @return null 表示成功；非 null 为错误原因
     */
    public String markRoomForCleaning(String roomNo) {
        for (Room room : rooms) {
            if (room.getRoomNo().equals(roomNo)) {
                if (Room.STATUS_FREE.equals(room.getStatus())) {
                    room.setStatus(Room.STATUS_CLEANING);
                    FileUtil.saveData(rooms, "rooms.dat");
                    return null;
                } else {
                    return "只有空闲状态的房间才能标记为待清洗！";
                }
            }
        }
        return "未找到房间号 " + roomNo + "！";
    }

    /**
     * 将房间标记为已清洁（恢复为空闲）
     * @param roomNo 房间号
     * @return null 表示成功；非 null 为错误原因
     */
    public String markRoomCleaned(String roomNo) {
        for (Room room : rooms) {
            if (room.getRoomNo().equals(roomNo)) {
                if (Room.STATUS_CLEANING.equals(room.getStatus())) {
                    room.setStatus(Room.STATUS_FREE);
                    FileUtil.saveData(rooms, "rooms.dat");
                    return null;
                } else {
                    return "只有待清洗状态的房间才能标记为已清洁！";
                }
            }
        }
        return "未找到房间号 " + roomNo + "！";
    }

    /** 在控制台打印所有订单 */
    public void listOrders() {
        if (orders.isEmpty()) {
            System.out.println("暂无订单记录。");
            return;
        }
        for (Order order : orders) {
            System.out.println(order);
        }
    }

    // ════════════════════ 订单查询 ════════════════════

    /** 根据订单号查找订单，找不到返回 null */
    public Order getOrderById(String orderId) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    /**
     * 按关键字搜索订单（匹配客户姓名或房间号）
     * @param keyword 搜索关键字
     * @return 匹配的订单列表（不区分大小写）
     */
    public List<Order> searchOrders(String keyword) {
        List<Order> result = new java.util.ArrayList<>();
        String kw = keyword.toLowerCase();
        for (Order order : orders) {
            if (order.getCustomer().getName().contains(kw)
                    || order.getRoom().getRoomNo().toLowerCase().contains(kw)
                    || order.getOrderId().toLowerCase().contains(kw)) {
                result.add(order);
            }
        }
        return result;
    }

    // ════════════════════ VIP 客户识别 ════════════════════

    /**
     * 按身份证号统计该客户历史累计消费金额
     */
    public double getCustomerTotalSpending(String idCard) {
        double total = 0;
        for (Order order : orders) {
            if (order.getCustomer().getIdCard().equals(idCard)) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }

    /**
     * 根据累计消费金额返回 VIP 等级
     */
    public String getVIPLevel(double totalSpending) {
        if (totalSpending >= 10000) return "钻石会员";
        if (totalSpending >= 5000)  return "金卡会员";
        if (totalSpending >= 1000)  return "银卡会员";
        return "新客户";
    }

    // ════════════════════ 会员卡管理 ════════════════════

    /**
     * 按身份证号查找会员卡，没有则返回 null
     */
    public MemberCard getMemberCardByIdCard(String idCard) {
        for (MemberCard card : memberCards) {
            if (card.getIdCard().equals(idCard)) {
                return card;
            }
        }
        return null;
    }

    /**
     * 获取或创建会员卡（一人一卡，按身份证绑定）
     */
    public MemberCard getOrCreateCard(String idCard, String name) {
        MemberCard card = getMemberCardByIdCard(idCard);
        if (card != null) {
            // 更新姓名（可能改名）
            if (!card.getName().equals(name)) {
                card.setName(name);
                FileUtil.saveData(memberCards, "memberCards.dat");
            }
            return card;
        }
        // 新开卡
        String cardId = "VIP" + System.currentTimeMillis();
        card = new MemberCard(cardId, idCard, name, 0, 0);
        memberCards.add(card);
        FileUtil.saveData(memberCards, "memberCards.dat");
        return card;
    }

    /**
     * 会员卡充值
     * @return null 表示成功；非 null 为错误信息
     */
    public String topUp(String idCard, double amount) {
        if (amount <= 0) {
            return "充值金额必须大于 0！";
        }
        MemberCard card = getMemberCardByIdCard(idCard);
        if (card == null) {
            return "未找到该身份证对应的会员卡，请先开卡！";
        }
        card.setBalance(card.getBalance() + amount);
        card.setTotalTopUp(card.getTotalTopUp() + amount);
        FileUtil.saveData(memberCards, "memberCards.dat");
        return null;
    }

    /**
     * 获取组合 VIP 等级（取历史订单消费 与 会员卡累计充值的较大者）
     */
    public String getCombinedVIPLevel(String idCard) {
        double orderSpending = getCustomerTotalSpending(idCard);
        MemberCard card = getMemberCardByIdCard(idCard);
        double cardTopUp = (card != null) ? card.getTotalTopUp() : 0;
        return getVIPLevel(Math.max(orderSpending, cardTopUp));
    }

    /**
     * 获取组合累计金额（用于展示）
     */
    public double getCombinedSpending(String idCard) {
        double orderSpending = getCustomerTotalSpending(idCard);
        MemberCard card = getMemberCardByIdCard(idCard);
        double cardTopUp = (card != null) ? card.getTotalTopUp() : 0;
        return Math.max(orderSpending, cardTopUp);
    }

    /**
     * 使用会员卡余额支付
     * @return null 表示成功；非 null 为错误信息
     */
    public String deductBalance(String idCard, double amount) {
        MemberCard card = getMemberCardByIdCard(idCard);
        if (card == null) {
            return "未找到该身份证对应的会员卡！";
        }
        if (card.getBalance() < amount) {
            return "卡内余额不足！当前余额 ￥" + String.format("%.2f", card.getBalance());
        }
        card.setBalance(card.getBalance() - amount);
        FileUtil.saveData(memberCards, "memberCards.dat");
        return null;
    }

    // ════════════════════ 营业统计 ════════════════════

    /**
     * 生成营业统计信息（供 UI 展示）
     * @return 格式化的统计字符串
     */
    public String getStatistics() {
        int totalRooms = rooms.size();
        int occupied = 0, free = 0, cleaning = 0, maintenance = 0;
        for (Room r : rooms) {
            switch (r.getStatus()) {
                case Room.STATUS_OCCUPIED:    occupied++;    break;
                case Room.STATUS_FREE:        free++;        break;
                case Room.STATUS_CLEANING:    cleaning++;    break;
                default:                      maintenance++; break;
            }
        }

        int totalOrders = orders.size();
        int activeOrders = 0;
        double todayRevenue = 0;
        double totalRevenue = 0;
        int totalCustomers = customers.size();

        for (Order o : orders) {
            totalRevenue += o.getTotalAmount();
            if (Order.STATUS_CHECKED_IN.equals(o.getStatus())) {
                activeOrders++;
                todayRevenue += o.getTotalAmount();
            }
        }

        double occupancyRate = totalRooms > 0 ? (occupied * 100.0 / totalRooms) : 0;

        StringBuilder sb = new StringBuilder();
        sb.append("═══════ StayHub 营业统计 ═══════\n\n");
        sb.append("  ▶ 房间概况\n");
        sb.append("    总房间: ").append(totalRooms).append(" 间\n");
        sb.append("    已入住: ").append(occupied).append(" 间");
        if (totalRooms > 0) sb.append(String.format("  (入住率 %.1f%%)", occupancyRate));
        sb.append("\n");
        sb.append("    空  闲: ").append(free).append(" 间\n");
        sb.append("    待清洗: ").append(cleaning).append(" 间\n");
        sb.append("    维修中: ").append(maintenance).append(" 间\n\n");
        sb.append("  ▶ 营收概况\n");
        sb.append("    在住订单: ").append(activeOrders).append(" 笔\n");
        sb.append("    在住营收: ￥").append(String.format("%.2f", todayRevenue)).append("\n");
        sb.append("    累计订单: ").append(totalOrders).append(" 笔\n");
        sb.append("    累计营收: ￥").append(String.format("%.2f", totalRevenue)).append("\n\n");
        sb.append("  ▶ 客户概况\n");
        sb.append("    累计客户: ").append(totalCustomers).append(" 人\n");

        return sb.toString();
    }

    // ════════════════════ 员工管理 ════════════════════

    /** 新增员工 */
    public void addStaff(Staff staff) {
        staffs.add(staff);
        FileUtil.saveData(staffs, "staffs.dat");
    }

    /** 根据工号查找员工 */
    private Staff findStaffById(String staffId) {
        for (Staff s : staffs) {
            if (s.getStaffId().equals(staffId)) return s;
        }
        return null;
    }

    /**
     * 更新员工职位和薪资
     * @return true 表示更新成功
     */
    public boolean updateStaff(String staffId, String newPosition, double newSalary) {
        Staff s = findStaffById(staffId);
        if (s == null) return false;
        s.setPosition(newPosition);
        s.setSalary(newSalary);
        FileUtil.saveData(staffs, "staffs.dat");
        return true;
    }

    /**
     * 删除员工
     * @return true 表示删除成功
     */
    public boolean removeStaff(String staffId) {
        Staff s = findStaffById(staffId);
        if (s == null) return false;
        staffs.remove(s);
        FileUtil.saveData(staffs, "staffs.dat");
        return true;
    }
}
