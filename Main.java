import java.util.List;
import java.util.Scanner;

/**
 * StayHub 酒店管理系统 —— 命令行界面（CLI）
 *
 * 编译 & 运行：
 *   javac *.java -encoding UTF-8
 *   java -Dfile.encoding=UTF-8 Main
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static HotelManager hotelManager = new HotelManager();

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║    StayHub 酒店管理系统      ║");
        System.out.println("╚══════════════════════════════╝");

        while (true) {
            System.out.println("\n请选择操作：");
            System.out.println("  1. 登录");
            System.out.println("  2. 注册新账号");
            System.out.println("  0. 退出");
            System.out.print(">>> ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    if (doLogin()) {
                        showMainMenu();
                    }
                    break;
                case "2":
                    doRegister();
                    break;
                case "0":
                    System.out.println("感谢使用，再见！");
                    System.exit(0);
                    break;
                default:
                    System.out.println("无效输入，请重新选择！");
            }
        }
    }

    // ──────────── 登录 ────────────

    private static boolean doLogin() {
        System.out.print("用户名: ");
        String username = scanner.nextLine();
        System.out.print("密  码: ");
        String password = scanner.nextLine();

        if (hotelManager.login(username, password)) {
            System.out.println("登录成功！");
            return true;
        } else {
            System.out.println("用户名或密码错误！");
            return false;
        }
    }

    // ──────────── 注册 ────────────

    private static void doRegister() {
        System.out.println("\n--- 注册新账号 ---");
        System.out.print("用户名 (≥3位): ");
        String username = scanner.nextLine();
        System.out.print("密  码 (≥4位): ");
        String password = scanner.nextLine();

        String result = hotelManager.register(username, password);
        if (result == null) {
            System.out.println("注册成功！请使用新账号登录。");
        } else {
            System.out.println("注册失败：" + result);
        }
    }

    // ──────────── 主菜单 ────────────

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n┌─────────── StayHub 工作台 ───────────┐");
            System.out.println("│  1. 查看所有房间                      │");
            System.out.println("│  2. 添加房间                          │");
            System.out.println("│  3. 办理入住                          │");
            System.out.println("│  4. 办理退房                          │");
            System.out.println("│  5. 查看所有订单                      │");
            System.out.println("│  6. 搜索订单                          │");
            System.out.println("│  7. 营业统计                          │");
            System.out.println("│  8. 清洁管理                          │");
            System.out.println("│  9. 员工管理                          │");
            System.out.println("│  0. 退出系统                          │");
            System.out.println("└───────────────────────────────────────┘");
            System.out.print(">>> ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": hotelManager.listRooms();          break;
                case "2": addRoomUI();                       break;
                case "3": checkInUI();                       break;
                case "4": checkOutUI();                      break;
                case "5": hotelManager.listOrders();         break;
                case "6": searchOrdersUI();                  break;
                case "7": System.out.println(hotelManager.getStatistics()); break;
                case "8": showCleaningMenu();                break;
                case "9": showStaffMenu();                   break;
                case "0":
                    System.out.println("感谢使用，系统已退出！数据已安全保存。");
                    System.exit(0);
                    break;
                default:
                    System.out.println("无效输入，请重新选择！");
            }
        }
    }

    // ──────────── 添加房间 ────────────

    private static void addRoomUI() {
        System.out.println("\n--- 添加新房间 ---");
        System.out.print("房间号 (如 801): ");
        String roomNo = scanner.nextLine();
        System.out.print("类  型 (单人间/双人间/套房): ");
        String type = scanner.nextLine();
        System.out.print("价  格 (每晚): ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("错误：价格格式不正确，请输入数字！");
            return;
        }

        Room room = new Room(roomNo, type, price, Room.STATUS_FREE);
        String result = hotelManager.addRoom(room);
        if (result == null) {
            System.out.println("房间添加成功！");
        } else {
            System.out.println("添加失败：" + result);
        }
    }

    // ──────────── 办理入住 ────────────

    private static void checkInUI() {
        System.out.println("\n--- 登记客户信息 ---");
        System.out.print("姓  名: ");
        String name = scanner.nextLine();
        System.out.print("身份证号: ");
        String idCard = scanner.nextLine();
        System.out.print("手机号: ");
        String phone = scanner.nextLine();
        System.out.print("性  别: ");
        String gender = scanner.nextLine();
        System.out.print("年  龄: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("错误：年龄格式不正确！");
            return;
        }

        Customer customer = new Customer(name, idCard, phone, gender, age);

        System.out.print("入住房号: ");
        String roomNo = scanner.nextLine();
        System.out.print("入住天数: ");
        int days;
        try {
            days = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("错误：天数格式不正确！");
            return;
        }

        String result = hotelManager.checkIn(customer, roomNo, days);
        if (result == null) {
            String discountMsg = days >= 7 ? "（已享满7天9折优惠）" : "";
            System.out.println("入住办理成功！订单号: " + hotelManager.getLastOrderId() + " " + discountMsg);
        } else {
            System.out.println("办理失败：" + result);
        }
    }

    // ──────────── 办理退房 ────────────

    private static void checkOutUI() {
        System.out.print("请输入要退房的订单号: ");
        String orderId = scanner.nextLine();
        Order order = hotelManager.getOrderById(orderId);

        if (order == null) {
            System.out.println("退房失败：未找到订单号 " + orderId + "！");
            return;
        }
        if (!Order.STATUS_CHECKED_IN.equals(order.getStatus())) {
            System.out.println("退房失败：该订单状态为「" + order.getStatus() + "」，无法退房！");
            return;
        }

        // 展示账单
        System.out.println("\n═══════ 退房账单确认 ═══════");
        System.out.println("订单号: " + order.getOrderId());
        System.out.println("客  户: " + order.getCustomer().getName());
        System.out.println("身份证: " + order.getCustomer().getIdCard());
        System.out.println("房  间: " + order.getRoom().getRoomNo() + " (" + order.getRoom().getType() + ")");
        System.out.println("入住日: " + order.getCheckInDate());
        System.out.println("天  数: " + order.getDays() + " 晚");
        System.out.println("单  价: ￥" + String.format("%.0f", order.getRoom().getPrice()) + " / 晚");
        if (order.getDays() >= 7) {
            System.out.println("折  扣: 满7天享9折");
        }
        System.out.println("总  计: ￥" + String.format("%.2f", order.getTotalAmount()));

        System.out.print("\n确认退房？(y/n): ");
        String confirm = scanner.nextLine();
        if (!"y".equalsIgnoreCase(confirm.trim())) {
            System.out.println("已取消退房操作。");
            return;
        }

        String result = hotelManager.checkOut(orderId);
        if (result == null) {
            System.out.println("退房成功！房间 " + order.getRoom().getRoomNo() + " 已标记为待清洗。");
        } else {
            System.out.println(result);
        }
    }

    // ──────────── 搜索订单 ────────────

    private static void searchOrdersUI() {
        System.out.print("请输入搜索关键词（客户姓名 / 房间号 / 订单号）: ");
        String keyword = scanner.nextLine();
        List<Order> result = hotelManager.searchOrders(keyword);
        System.out.println("\n══════ 搜索结果：「" + keyword + "」══════");
        if (result.isEmpty()) {
            System.out.println("未找到匹配的订单。");
        } else {
            for (Order order : result) {
                System.out.println(order);
                System.out.println();
            }
            System.out.println("──────────────────────────────");
            System.out.println("共找到 " + result.size() + " 条记录");
        }
    }

    // ──────────── 清洁管理 ────────────

    private static void showCleaningMenu() {
        while (true) {
            System.out.println("\n┌─────────── 清洁管理 ───────────┐");
            System.out.println("│  1. 查看所有房间清洁状态        │");
            System.out.println("│  2. 标记房间为待清洗            │");
            System.out.println("│  3. 标记房间为已清洁            │");
            System.out.println("│  0. 返回主菜单                  │");
            System.out.println("└────────────────────────────────┘");
            System.out.print(">>> ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": listRoomsWithCleaning();  break;
                case "2": markRoomForCleaningUI();  break;
                case "3": markRoomCleanedUI();      break;
                case "0": return;
                default:
                    System.out.println("无效输入，请重新选择！");
            }
        }
    }

    /** 展示所有房间（含待清洗统计） */
    private static void listRoomsWithCleaning() {
        List<Room> rooms = hotelManager.getRooms();
        if (rooms.isEmpty()) {
            System.out.println("暂无房间信息。");
            return;
        }
        int freeCount = 0, occupiedCount = 0, cleaningCount = 0;
        for (Room room : rooms) {
            System.out.println(room);
            if (Room.STATUS_FREE.equals(room.getStatus())) freeCount++;
            else if (Room.STATUS_OCCUPIED.equals(room.getStatus())) occupiedCount++;
            else if (Room.STATUS_CLEANING.equals(room.getStatus())) cleaningCount++;
        }
        System.out.println("──────────────────────────────");
        System.out.println("总计: " + rooms.size() + " 间 | 空闲: " + freeCount
                + " 间 | 已入住: " + occupiedCount + " 间 | 待清洗: " + cleaningCount + " 间");
    }

    /** 手动标记房间为待清洗 */
    private static void markRoomForCleaningUI() {
        System.out.print("请输入要标记为待清洗的房间号: ");
        String roomNo = scanner.nextLine();
        String result = hotelManager.markRoomForCleaning(roomNo);
        if (result == null) {
            System.out.println("房间 " + roomNo + " 已标记为待清洗！");
        } else {
            System.out.println("操作失败：" + result);
        }
    }

    /** 标记房间已清洁完成 */
    private static void markRoomCleanedUI() {
        System.out.print("请输入已清洁完毕的房间号: ");
        String roomNo = scanner.nextLine();
        String result = hotelManager.markRoomCleaned(roomNo);
        if (result == null) {
            System.out.println("房间 " + roomNo + " 清洁完毕，已恢复空闲状态！");
        } else {
            System.out.println("操作失败：" + result);
        }
    }

    // ──────────── 员工管理 ────────────

    private static void showStaffMenu() {
        while (true) {
            System.out.println("\n┌─────────── 员工管理 ───────────┐");
            System.out.println("│  1. 查看所有员工               │");
            System.out.println("│  2. 添加员工                   │");
            System.out.println("│  3. 编辑员工                   │");
            System.out.println("│  4. 删除员工                   │");
            System.out.println("│  0. 返回主菜单                 │");
            System.out.println("└────────────────────────────────┘");
            System.out.print(">>> ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": listStaffUI();    break;
                case "2": addStaffUI();     break;
                case "3": editStaffUI();    break;
                case "4": removeStaffUI();  break;
                case "0": return;
                default:
                    System.out.println("无效输入，请重新选择！");
            }
        }
    }

    private static void listStaffUI() {
        List<Staff> staffs = hotelManager.getStaffs();
        if (staffs.isEmpty()) {
            System.out.println("暂无员工记录。");
            return;
        }
        for (Staff s : staffs) {
            System.out.println(s);
        }
    }

    private static void addStaffUI() {
        System.out.println("\n--- 添加新员工 ---");
        System.out.print("工号: ");
        String staffId = scanner.nextLine();
        System.out.print("姓名: ");
        String name = scanner.nextLine();
        System.out.print("职位: ");
        String position = scanner.nextLine();
        System.out.print("薪资: ");
        double salary;
        try {
            salary = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("错误：薪资格式不正确！");
            return;
        }
        hotelManager.addStaff(new Staff(staffId, name, position, salary));
        System.out.println("员工添加成功！");
    }

    private static void editStaffUI() {
        System.out.print("请输入要编辑的员工工号: ");
        String staffId = scanner.nextLine();
        System.out.print("新职位: ");
        String position = scanner.nextLine();
        System.out.print("新薪资: ");
        double salary;
        try {
            salary = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("错误：薪资格式不正确！");
            return;
        }
        if (hotelManager.updateStaff(staffId, position, salary)) {
            System.out.println("员工信息更新成功！");
        } else {
            System.out.println("未找到该工号的员工！");
        }
    }

    private static void removeStaffUI() {
        System.out.print("请输入要删除的员工工号: ");
        String staffId = scanner.nextLine();
        if (hotelManager.removeStaff(staffId)) {
            System.out.println("员工删除成功！");
        } else {
            System.out.println("未找到该工号的员工！");
        }
    }
}
