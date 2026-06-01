import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * StayHub 酒店管理系统 —— 图形界面（Swing GUI）
 *
 * 编译 & 运行：
 *   javac *.java -encoding UTF-8
 *   java -Dfile.encoding=UTF-8 StayHubUI
 */
public class StayHubUI {

    private HotelManager hotelManager;
    private JFrame mainFrame;
    private JEditorPane displayArea;  // HTML 渲染展示区
    private JLabel statusBarLabel;    // 底部状态栏
    private javax.swing.Timer statusBarTimer;  // 状态栏呼吸动画
    private javax.swing.Timer flashTimer;      // 闪屏反馈动画
    private int flashCount;                    // 闪屏剩余帧数

    // ──────────── 呼吸动画帧 ────────────
    private static final String[] BREATHING_DOTS = {
        "系统就绪", "系统就绪.", "系统就绪..", "系统就绪..."
    };
    private int breathingFrame = 0;

    // ──────────── 配色方案 ────────────
    private static final Color COLOR_PRIMARY   = new Color(21, 101, 192);  // 深蓝主色
    private static final Color COLOR_GOLD      = new Color(185, 150, 106); // 暖金点缀
    private static final Color COLOR_SUCCESS   = new Color(46, 125, 50);   // 绿色
    private static final Color COLOR_DANGER    = new Color(198, 40, 40);   // 红色
    private static final Color COLOR_CLEANING  = new Color(230, 126, 34);  // 橙色（待清洗）
    private static final Color COLOR_BG        = new Color(245, 245, 245); // 浅灰背景
    private static final Color COLOR_MENU_BG   = new Color(38, 50, 64);    // 深蓝灰侧栏
    private static final Color COLOR_DISPLAY_BG = new Color(252, 251, 248);// 暖白展示区背景
    private static final Font  FONT_TITLE      = new Font("微软雅黑", Font.BOLD, 22);
    private static final Font  FONT_DISPLAY    = new Font("微软雅黑", Font.PLAIN, 14);
    private static final Font  FONT_BUTTON     = new Font("微软雅黑", Font.PLAIN, 13);

    // ──────────── 启动入口 ────────────

    public StayHubUI() {
        hotelManager = new HotelManager();
        // 使用操作系统原生风格
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        showLoginWindow();
    }

    // ════════════════════ 1. 登录窗口 ════════════════════

    private void showLoginWindow() {
        JFrame loginFrame = new JFrame("StayHub - 酒店管理系统");
        loginFrame.setSize(420, 340);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(COLOR_BG);

        // 标题区
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(COLOR_BG);

        JLabel brandLabel = new JLabel("STAYHUB", SwingConstants.CENTER);
        brandLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        brandLabel.setForeground(COLOR_PRIMARY);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("酒店管理系统", SwingConstants.CENTER);
        subLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        subLabel.setForeground(COLOR_GOLD);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleBox.add(brandLabel);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subLabel);
        titleBox.add(Box.createVerticalStrut(10));

        // 金色分隔线
        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_GOLD);
        sep.setMaximumSize(new Dimension(300, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleBox.add(sep);
        titleBox.add(Box.createVerticalStrut(15));

        mainPanel.add(titleBox, BorderLayout.NORTH);

        // 输入区
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(COLOR_BG);

        JPanel userRow = new JPanel(new BorderLayout(8, 0));
        userRow.setBackground(COLOR_BG);
        userRow.setMaximumSize(new Dimension(300, 30));
        JLabel userLabel = new JLabel("账  号");
        userLabel.setFont(FONT_BUTTON);
        userLabel.setPreferredSize(new Dimension(50, 25));
        userRow.add(userLabel, BorderLayout.WEST);
        JTextField userField = new JTextField(15);
        userRow.add(userField, BorderLayout.CENTER);

        JPanel passRow = new JPanel(new BorderLayout(8, 0));
        passRow.setBackground(COLOR_BG);
        passRow.setMaximumSize(new Dimension(300, 30));
        JLabel passLabel = new JLabel("密  码");
        passLabel.setFont(FONT_BUTTON);
        passLabel.setPreferredSize(new Dimension(50, 25));
        passRow.add(passLabel, BorderLayout.WEST);
        JPasswordField passField = new JPasswordField(15);
        passRow.add(passField, BorderLayout.CENTER);

        inputPanel.add(userRow);
        inputPanel.add(Box.createVerticalStrut(8));
        inputPanel.add(passRow);

        JPanel inputWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        inputWrapper.setBackground(COLOR_BG);
        inputWrapper.add(inputPanel);
        mainPanel.add(inputWrapper, BorderLayout.CENTER);

        // 按钮区
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(COLOR_BG);

        JButton loginBtn = createButton("登  录", COLOR_PRIMARY);
        JButton regBtn   = createButton("注册账号", new Color(100, 120, 140));

        btnPanel.add(loginBtn);
        btnPanel.add(regBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        // 按钮事件
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "账号和密码不能为空！", "提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (hotelManager.login(username, password)) {
                loginFrame.dispose();
                showMainWindow();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "账号或密码错误！", "登录失败",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        regBtn.addActionListener(e -> showRegisterDialog(loginFrame));

        // 回车键触发登录
        loginFrame.getRootPane().setDefaultButton(loginBtn);

        loginFrame.add(mainPanel);
        loginFrame.setVisible(true);
    }

    // ──────────── 注册对话框 ────────────

    private void showRegisterDialog(JFrame parent) {
        JTextField userField   = new JTextField(15);
        JPasswordField passField   = new JPasswordField(15);
        JPasswordField confirmField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 8));
        panel.add(createInputRow("用户名 (≥3位):", userField));
        panel.add(createInputRow("密  码 (≥4位):", passField));
        panel.add(createInputRow("确认密码:", confirmField));

        int option = JOptionPane.showConfirmDialog(parent, panel,
                "注册新账号", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return;

        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String confirm  = new String(confirmField.getPassword()).trim();

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(parent, "两次输入的密码不一致！", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = hotelManager.register(username, password);
        if (result == null) {
            JOptionPane.showMessageDialog(parent, "注册成功！请使用新账号登录。");
        } else {
            JOptionPane.showMessageDialog(parent, result, "注册失败",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ════════════════════ 2. 主工作台窗口 ════════════════════

    private void showMainWindow() {
        mainFrame = new JFrame("StayHub 酒店管理终端");
        mainFrame.setSize(820, 550);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        // 关闭确认
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(mainFrame,
                        "确定要退出 StayHub 系统吗？\n所有数据已自动保存。",
                        "退出确认", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opt == JOptionPane.YES_OPTION) {
                    mainFrame.dispose();
                    System.exit(0);
                }
            }
        });

        // ── 顶部标题栏 ──
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(COLOR_PRIMARY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 20));

        JPanel titleLeft = new JPanel();
        titleLeft.setLayout(new BoxLayout(titleLeft, BoxLayout.Y_AXIS));
        titleLeft.setBackground(COLOR_PRIMARY);

        JLabel titleLabel = new JLabel("STAYHUB 工作台");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLeft.add(titleLabel);

        JLabel subTitle = new JLabel("Hospitality Management System");
        subTitle.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        subTitle.setForeground(new Color(180, 200, 220));
        titleLeft.add(subTitle);

        titlePanel.add(titleLeft, BorderLayout.WEST);

        JLabel versionLabel = new JLabel("v1.5");
        versionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(180, 200, 220));
        titlePanel.add(versionLabel, BorderLayout.EAST);

        mainFrame.add(titlePanel, BorderLayout.NORTH);

        // ── 中间数据展示区（HTML 渲染）──
        displayArea = new JEditorPane();
        displayArea.setContentType("text/html");
        displayArea.setEditable(false);
        displayArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        displayArea.setBackground(COLOR_DISPLAY_BG);

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(210, 210, 210)));
        mainFrame.add(scrollPane, BorderLayout.CENTER);

        // ── 左侧导航菜单 ──
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(10, 1, 0, 1));
        menuPanel.setPreferredSize(new Dimension(185, 0));
        menuPanel.setBackground(COLOR_MENU_BG);

        JButton btnRooms    = createMenuButton("实时房态");
        JButton btnStats    = createMenuButton("营业统计");
        JButton btnAddRoom  = createMenuButton("录入新房间");
        JButton btnCheckIn  = createMenuButton("办理入住");
        JButton btnCheckOut = createMenuButton("办理退房");
        JButton btnOrders   = createMenuButton("历史订单");
        JButton btnSearch   = createMenuButton("搜索订单");
        JButton btnCleaning = createMenuButton("清洁管理");
        JButton btnStaff    = createMenuButton("人事管理");
        JButton btnMember   = createMenuButton("会员中心");

        // tooltip 提示
        btnRooms.setToolTipText("查看所有房间的实时状态");
        btnStats.setToolTipText("查看入住率、营收等经营数据");
        btnAddRoom.setToolTipText("录入新房源信息");
        btnCheckIn.setToolTipText("为客户办理入住登记");
        btnCheckOut.setToolTipText("办理退房并生成账单");
        btnOrders.setToolTipText("浏览所有历史订单");
        btnSearch.setToolTipText("按姓名/房号/订单号查找订单");
        btnCleaning.setToolTipText("管理房间清洁状态，标记待清洗或已清洁");
        btnStaff.setToolTipText("管理员工信息：增删改查");
        btnMember.setToolTipText("会员卡充值、余额查询、VIP等级查看");

        menuPanel.add(btnRooms);
        menuPanel.add(btnStats);
        menuPanel.add(btnAddRoom);
        menuPanel.add(btnCheckIn);
        menuPanel.add(btnCheckOut);
        menuPanel.add(btnOrders);
        menuPanel.add(btnSearch);
        menuPanel.add(btnCleaning);
        menuPanel.add(btnStaff);
        menuPanel.add(btnMember);
        mainFrame.add(menuPanel, BorderLayout.WEST);

        // ── 绑定按钮事件 ──

        btnRooms.addActionListener(e -> { refreshRoomDisplay(); refreshStatusBar(); });

        btnStats.addActionListener(e -> displayText(hotelManager.getStatistics()));

        btnAddRoom.addActionListener(e -> showAddRoomDialog());

        btnCheckIn.addActionListener(e -> showCheckInDialog());

        btnCheckOut.addActionListener(e -> showCheckOutDialog());

        btnOrders.addActionListener(e -> refreshOrderDisplay());

        btnSearch.addActionListener(e -> showSearchOrderDialog());

        btnCleaning.addActionListener(e -> showCleaningDialog());

        btnStaff.addActionListener(e -> showStaffDialog());

        btnMember.addActionListener(e -> showMemberCenterDialog());

        // ── 底部状态栏 ──
        statusBarLabel = new JLabel(" ");
        statusBarLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusBarLabel.setForeground(new Color(120, 120, 120));
        statusBarLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(6, 15, 6, 15)));
        mainFrame.add(statusBarLabel, BorderLayout.SOUTH);

        // 默认显示房态
        refreshRoomDisplay();
        refreshStatusBar();
        mainFrame.setVisible(true);

        // 启动状态栏呼吸动画
        startStatusBarAnimation();

        // 登录欢迎
        JOptionPane.showMessageDialog(mainFrame,
                "欢迎使用 StayHub 酒店管理系统！\n祝您工作愉快 ~",
                "登录成功", JOptionPane.INFORMATION_MESSAGE);
    }

    // ════════════════════ 3. 业务功能弹窗 ════════════════════

    // ──────────── 录入房间 ────────────

    private void showAddRoomDialog() {
        JTextField noField    = new JTextField(10);
        JTextField typeField  = new JTextField(10);
        JTextField priceField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 8));
        panel.add(createInputRow("房间号 (如 801):", noField));
        panel.add(createInputRow("房型 (单人间/双人间/套房):", typeField));
        panel.add(createInputRow("价格 (每晚):", priceField));

        int option = JOptionPane.showConfirmDialog(mainFrame, panel,
                "录入新房间", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return;

        String roomNo  = noField.getText().trim();
        String type    = typeField.getText().trim();
        String priceStr = priceField.getText().trim();

        if (roomNo.isEmpty() || type.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "所有字段均为必填！", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "价格格式错误，请输入数字！", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Room room = new Room(roomNo, type, price, Room.STATUS_FREE);
        String result = hotelManager.addRoom(room);
        if (result == null) {
            JOptionPane.showMessageDialog(mainFrame, "房间录入成功！");
            refreshRoomDisplay();
            refreshStatusBar();
            triggerSuccessFlash();
        } else {
            JOptionPane.showMessageDialog(mainFrame, result, "录入失败",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ──────────── 办理入住 ────────────

    private void showCheckInDialog() {
        JTextField nameField    = new JTextField(10);
        JTextField idField      = new JTextField(10);
        JTextField phoneField   = new JTextField(10);
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"男", "女"});
        JTextField ageField     = new JTextField(10);
        JTextField roomNoField  = new JTextField(10);
        JTextField daysField    = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(7, 1, 5, 6));
        panel.add(createInputRow("客户姓名:", nameField));
        panel.add(createInputRow("身份证号:", idField));
        panel.add(createInputRow("手机号码:", phoneField));
        panel.add(createInputRow("性    别:", genderBox));
        panel.add(createInputRow("年    龄:", ageField));
        panel.add(createInputRow("入住房号:", roomNoField));
        panel.add(createInputRow("入住天数:", daysField));

        int option = JOptionPane.showConfirmDialog(mainFrame, panel,
                "办理入住登记", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return;

        String name    = nameField.getText().trim();
        String idCard  = idField.getText().trim();
        String phone   = phoneField.getText().trim();
        String gender  = (String) genderBox.getSelectedItem();
        String ageStr  = ageField.getText().trim();
        String roomNo  = roomNoField.getText().trim();
        String daysStr = daysField.getText().trim();

        // 空值检查
        if (name.isEmpty() || idCard.isEmpty() || phone.isEmpty()
                || ageStr.isEmpty() || roomNo.isEmpty() || daysStr.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "所有字段均为必填！", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int age, days;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "年龄格式错误，请输入整数！", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            days = Integer.parseInt(daysStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "入住天数格式错误，请输入整数！", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = new Customer(name, idCard, phone, gender, age);

        // ── VIP 客户识别（订单消费 + 会员卡充值，取较高者）──
        double combinedSpending = hotelManager.getCombinedSpending(idCard);
        String vipLevel = hotelManager.getCombinedVIPLevel(idCard);
        MemberCard card = hotelManager.getMemberCardByIdCard(idCard);

        if (combinedSpending > 0) {
            // 判断等级来源
            String source = (card != null && card.getTotalTopUp() > 0)
                    ? "（订单消费 + 会员充值综合评定）" : "（历史订单消费累计）";
            String cardInfo = (card != null && card.getBalance() > 0)
                    ? "\n  卡内余额: ￥" + String.format("%.2f", card.getBalance()) : "";

            String vipConfirm = String.format(
                    "识别到 VIP 客户！\n\n"
                  + "  客  户: %s\n"
                  + "  VIP 等级: %s %s\n"
                  + "  累计金额: ￥%.2f\n"
                  + "%s"
                  + "  本次折扣: %s\n"
                  + "  %s\n"
                  + "\n确认办理入住？",
                    name, vipLevel, source, combinedSpending, cardInfo,
                    combinedSpending >= 10000 ? "8.5折（钻石会员）"
                  : combinedSpending >= 5000  ? "9.0折（金卡会员）"
                  :                              "9.5折（银卡会员）",
                    days >= 7 ? "（可叠加满7天9折优惠）" : ""
            );
            int confirm = JOptionPane.showConfirmDialog(mainFrame, vipConfirm,
                    "VIP 客户识别", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.OK_OPTION) return;
        }

        String result = hotelManager.checkIn(customer, roomNo, days);
        if (result == null) {
            // 组装折扣说明
            String discountMsg = "";
            if (combinedSpending > 0 && days >= 7) {
                discountMsg = "\n（VIP " + vipLevel + "优惠 + 满7天9折）";
            } else if (combinedSpending > 0) {
                discountMsg = "\n（VIP " + vipLevel + "优惠）";
            } else if (days >= 7) {
                discountMsg = "\n（已享满7天9折优惠）";
            }
            JOptionPane.showMessageDialog(mainFrame,
                    "入住办理成功！\n订单号: " + hotelManager.getLastOrderId() + discountMsg);

            // ── 会员卡余额支付 ──
            if (card != null && card.getBalance() > 0) {
                // 从最新订单获取金额
                Order lastOrder = hotelManager.getOrders().get(hotelManager.getOrders().size() - 1);
                double toPay = lastOrder.getTotalAmount();
                int payOpt = JOptionPane.showConfirmDialog(mainFrame,
                        "是否使用会员卡余额支付？\n\n"
                      + "  应付金额: ￥" + String.format("%.2f", toPay) + "\n"
                      + "  卡内余额: ￥" + String.format("%.2f", card.getBalance()),
                        "余额支付", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (payOpt == JOptionPane.YES_OPTION) {
                    String payResult = hotelManager.deductBalance(idCard, toPay);
                    if (payResult == null) {
                        JOptionPane.showMessageDialog(mainFrame,
                                "余额支付成功！卡内剩余: ￥"
                                + String.format("%.2f", hotelManager.getMemberCardByIdCard(idCard).getBalance()));
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, payResult,
                                "支付失败", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            refreshRoomDisplay();
            refreshStatusBar();
            triggerSuccessFlash();
        } else {
            JOptionPane.showMessageDialog(mainFrame, result, "入住失败",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ──────────── 办理退房（含账单确认）────────────

    private void showCheckOutDialog() {
        String orderId = JOptionPane.showInputDialog(mainFrame,
                "请输入要退房的订单号:", "办理退房", JOptionPane.PLAIN_MESSAGE);
        if (orderId == null || orderId.trim().isEmpty()) return;

        orderId = orderId.trim();
        Order order = hotelManager.getOrderById(orderId);

        if (order == null) {
            JOptionPane.showMessageDialog(mainFrame,
                    "未找到订单号: " + orderId, "退房失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!Order.STATUS_CHECKED_IN.equals(order.getStatus())) {
            JOptionPane.showMessageDialog(mainFrame,
                    "该订单当前状态为「" + order.getStatus() + "」，无法退房！", "退房失败",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 组装账单
        String bill = String.format(
                "═══════ 退房账单确认 ═══════\n\n"
              + "  订单号: %s\n"
              + "  客  户: %s\n"
              + "  身份证: %s\n"
              + "  房  间: %s (%s)\n"
              + "  入住日: %s\n"
              + "  天  数: %d 晚\n"
              + "  单  价: ￥%.0f / 晚\n"
              + "  ─────────────────\n"
              + "  总  计: ￥%.2f\n"
              + "  %s\n"
              + "\n══════════════════════════\n"
              + "  确认退房后将标记为待清洗",
                order.getOrderId(),
                order.getCustomer().getName(),
                order.getCustomer().getIdCard(),
                order.getRoom().getRoomNo(),
                order.getRoom().getType(),
                order.getCheckInDate(),
                order.getDays(),
                order.getRoom().getPrice(),
                order.getTotalAmount(),
                order.getDays() >= 7 ? "（已享满7天9折优惠）" : ""
        );

        int confirm = JOptionPane.showConfirmDialog(mainFrame, bill,
                "退房账单确认", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION) return;

        String result = hotelManager.checkOut(orderId);
        if (result == null) {
            String[] funMsgs = {
                "感谢入住，期待再次光临！",
                "祝您一路顺风，生活愉快！",
                "房间已退，欢迎下次再来 StayHub ~",
                "退房完成，保洁小分队已收到通知！",
                "期待与您的下一次相遇！",
            };
            String randomMsg = funMsgs[(int) (System.currentTimeMillis() % funMsgs.length)];
            JOptionPane.showMessageDialog(mainFrame,
                    "退房成功！\n房间 " + order.getRoom().getRoomNo() + " 已标记为待清洗。\n\n" + randomMsg);
            refreshRoomDisplay();
            refreshStatusBar();
            triggerSuccessFlash();
        } else {
            JOptionPane.showMessageDialog(mainFrame, result, "退房失败",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ──────────── 搜索订单 ────────────

    private void showSearchOrderDialog() {
        String keyword = JOptionPane.showInputDialog(mainFrame,
                "请输入客户姓名 / 房间号 / 订单号:", "搜索订单", JOptionPane.PLAIN_MESSAGE);
        if (keyword == null || keyword.trim().isEmpty()) return;

        List<Order> result = hotelManager.searchOrders(keyword.trim());
        StringBuilder body = new StringBuilder();

        if (result.isEmpty()) {
            body.append("<div class='stat-card'><p style='color:#999;text-align:center;'>"
                      + "未找到匹配「<b>").append(escHtml(keyword.trim())).append("</b>」的订单</p></div>");
        } else {
            body.append("<table><tr><th>订单号</th><th>客户</th><th>房间</th><th>日期</th>"
                      + "<th>天数</th><th>金额</th><th>状态</th></tr>");
            for (Order o : result) {
                String sc = Order.STATUS_CHECKED_IN.equals(o.getStatus()) ? "occupied" : "maintenance";
                body.append("<tr class='order-row'>")
                    .append("<td><b>").append(o.getOrderId()).append("</b></td>")
                    .append("<td>").append(o.getCustomer().getName()).append("</td>")
                    .append("<td>").append(o.getRoom().getRoomNo()).append("</td>")
                    .append("<td>").append(o.getCheckInDate()).append("</td>")
                    .append("<td>").append(o.getDays()).append(" 晚</td>")
                    .append("<td>￥").append(String.format("%.2f", o.getTotalAmount())).append("</td>")
                    .append("<td class='").append(sc).append("'>").append(o.getStatus()).append("</td>")
                    .append("</tr>");
            }
            body.append("</table>");
            body.append("<div class='summary' style='margin-top:10px;'><b>共找到 ")
                .append(result.size()).append(" 条记录</b></div>");
        }
        displayArea.setText(wrapHtml("&#9670; 搜索:「" + escHtml(keyword.trim()) + "」", body.toString()));
        displayArea.setCaretPosition(0);
    }

    /** HTML 转义工具 */
    private String escHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ──────────── 清洁管理 ────────────

    private void showCleaningDialog() {
        JDialog dialog = new JDialog(mainFrame, "清洁管理 - 房间清洁状态", true);
        dialog.setSize(600, 460);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("  房间清洁状态列表");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        titleLabel.setForeground(COLOR_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // 房间列表区
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scrollPane, BorderLayout.CENTER);

        refreshCleaningList(listPanel, dialog);
        dialog.setVisible(true);
    }

    /**
     * 重新绘制清洁管理列表
     */
    private void refreshCleaningList(JPanel listPanel, JDialog dialog) {
        listPanel.removeAll();
        List<Room> rooms = hotelManager.getRooms();

        if (rooms.isEmpty()) {
            JLabel emptyLabel = new JLabel("  暂无房间记录。");
            emptyLabel.setFont(FONT_DISPLAY);
            emptyLabel.setForeground(Color.GRAY);
            listPanel.add(emptyLabel);
        } else {
            for (Room room : rooms) {
                JPanel row = createCleaningRow(room, listPanel, dialog);
                listPanel.add(row);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    /**
     * 创建单个房间的清洁操作行
     */
    private JPanel createCleaningRow(Room room, JPanel listPanel, JDialog dialog) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        // 状态标签颜色
        Color statusColor;
        switch (room.getStatus()) {
            case Room.STATUS_FREE:
                statusColor = COLOR_SUCCESS;
                break;
            case Room.STATUS_OCCUPIED:
                statusColor = COLOR_PRIMARY;
                break;
            case Room.STATUS_CLEANING:
                statusColor = new Color(230, 126, 34); // 橙色
                break;
            default:
                statusColor = Color.GRAY;
        }

        // 房间信息
        JLabel infoLabel = new JLabel(String.format(
                "  %s  |  类型: %s  |  价格: ￥%.0f  |  状态: ",
                room.getRoomNo(), room.getType(), room.getPrice()));
        infoLabel.setFont(FONT_DISPLAY);
        row.add(infoLabel, BorderLayout.WEST);

        // 状态标签（带颜色）
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        centerPanel.setBackground(Color.WHITE);
        JLabel statusLabel = new JLabel(room.getStatus());
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
        statusLabel.setForeground(statusColor);
        centerPanel.add(statusLabel);
        row.add(centerPanel, BorderLayout.CENTER);

        // 操作按钮
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setBackground(Color.WHITE);

        if (Room.STATUS_CLEANING.equals(room.getStatus())) {
            JButton cleanedBtn = createButton("标记已清洁", COLOR_SUCCESS);
            cleanedBtn.addActionListener(ev -> {
                String result = hotelManager.markRoomCleaned(room.getRoomNo());
                if (result == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "房间 " + room.getRoomNo() + " 已标记为清洁完毕，恢复空闲状态！");
                    refreshCleaningList(listPanel, dialog);
                    refreshRoomDisplay();
                    refreshStatusBar();
                    triggerSuccessFlash();
                } else {
                    JOptionPane.showMessageDialog(dialog, result, "操作失败",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            btnGroup.add(cleanedBtn);
        } else if (Room.STATUS_FREE.equals(room.getStatus())) {
            JButton dirtyBtn = createButton("标记待清洗", new Color(230, 126, 34));
            dirtyBtn.addActionListener(ev -> {
                String result = hotelManager.markRoomForCleaning(room.getRoomNo());
                if (result == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "房间 " + room.getRoomNo() + " 已标记为待清洗！");
                    refreshCleaningList(listPanel, dialog);
                    refreshRoomDisplay();
                    refreshStatusBar();
                    triggerSuccessFlash();
                } else {
                    JOptionPane.showMessageDialog(dialog, result, "操作失败",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            btnGroup.add(dirtyBtn);
        }

        row.add(btnGroup, BorderLayout.EAST);
        return row;
    }

    // ════════════════════ 4. 人事管理弹窗 ════════════════════

    private void showStaffDialog() {
        JDialog dialog = new JDialog(mainFrame, "人事管理 - 员工列表", true);
        dialog.setSize(620, 460);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("  在职员工列表", SwingConstants.LEFT);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        titleLabel.setForeground(COLOR_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // 员工列表区
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JButton btnAddStaff = createButton("添加新员工", COLOR_SUCCESS);
        bottomPanel.add(btnAddStaff);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        btnAddStaff.addActionListener(ev -> {
            JTextField idField   = new JTextField(12);
            JTextField nameField = new JTextField(12);
            JTextField posField  = new JTextField(12);
            JTextField salField  = new JTextField(12);

            JPanel panel = new JPanel(new GridLayout(4, 1, 5, 6));
            panel.add(createInputRow("工号:", idField));
            panel.add(createInputRow("姓名:", nameField));
            panel.add(createInputRow("职位:", posField));
            panel.add(createInputRow("薪资:", salField));

            int opt = JOptionPane.showConfirmDialog(dialog, panel,
                    "添加新员工", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opt != JOptionPane.OK_OPTION) return;

            String id   = idField.getText().trim();
            String name = nameField.getText().trim();
            String pos  = posField.getText().trim();
            String sal  = salField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || pos.isEmpty() || sal.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "所有字段均为必填！", "提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                hotelManager.addStaff(new Staff(id, name, pos, Double.parseDouble(sal)));
                refreshStaffList(listPanel, dialog);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "薪资格式错误，请输入数字！", "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshStaffList(listPanel, dialog);
        dialog.setVisible(true);
    }

    /**
     * 重新绘制员工列表（增删改后调用）
     */
    private void refreshStaffList(JPanel listPanel, JDialog dialog) {
        listPanel.removeAll();
        List<Staff> staffs = hotelManager.getStaffs();

        if (staffs.isEmpty()) {
            JLabel emptyLabel = new JLabel("  暂无员工记录。");
            emptyLabel.setFont(FONT_DISPLAY);
            emptyLabel.setForeground(Color.GRAY);
            listPanel.add(emptyLabel);
        } else {
            for (Staff s : staffs) {
                JPanel row = createStaffRow(s, listPanel, dialog);
                listPanel.add(row);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    /**
     * 创建单个员工行（信息 + 编辑/删除按钮）
     */
    private JPanel createStaffRow(Staff s, JPanel listPanel, JDialog dialog) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        // 员工信息
        JLabel infoLabel = new JLabel(s.toString());
        infoLabel.setFont(FONT_DISPLAY);
        row.add(infoLabel, BorderLayout.CENTER);

        // 操作按钮组
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setBackground(Color.WHITE);

        JButton editBtn = createButton("编辑", COLOR_PRIMARY);
        editBtn.addActionListener(ev -> {
            JTextField posField = new JTextField(s.getPosition(), 12);
            JTextField salField = new JTextField(String.valueOf(s.getSalary()), 12);

            JPanel panel = new JPanel(new GridLayout(2, 1, 5, 6));
            panel.add(createInputRow("职位:", posField));
            panel.add(createInputRow("薪资:", salField));

            int opt = JOptionPane.showConfirmDialog(dialog, panel,
                    "编辑 - " + s.getName(), JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (opt != JOptionPane.OK_OPTION) return;

            try {
                double newSal = Double.parseDouble(salField.getText().trim());
                hotelManager.updateStaff(s.getStaffId(), posField.getText().trim(), newSal);
                refreshStaffList(listPanel, dialog);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "薪资格式错误，请输入数字！", "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        btnGroup.add(editBtn);

        JButton delBtn = createButton("删除", COLOR_DANGER);
        delBtn.addActionListener(ev -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "确定要删除员工 " + s.getName() + " 吗？",
                    "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                hotelManager.removeStaff(s.getStaffId());
                refreshStaffList(listPanel, dialog);
            }
        });
        btnGroup.add(delBtn);

        row.add(btnGroup, BorderLayout.EAST);
        return row;
    }

    // ════════════════════ 5. 会员中心 ════════════════════

    private void showMemberCenterDialog() {
        // 输入身份证号查卡
        String idCard = JOptionPane.showInputDialog(mainFrame,
                "请输入客户身份证号:", "会员中心", JOptionPane.PLAIN_MESSAGE);
        if (idCard == null || idCard.trim().isEmpty()) return;
        idCard = idCard.trim();

        MemberCard card = hotelManager.getMemberCardByIdCard(idCard);
        String vipLevel = hotelManager.getCombinedVIPLevel(idCard);
        double combined = hotelManager.getCombinedSpending(idCard);

        if (card == null) {
            // 未开卡 → 询问是否开卡
            String name = JOptionPane.showInputDialog(mainFrame,
                    "该客户尚未办理会员卡。\n\n请输入持卡人姓名以开卡:",
                    "开通会员卡", JOptionPane.PLAIN_MESSAGE);
            if (name == null || name.trim().isEmpty()) return;
            card = hotelManager.getOrCreateCard(idCard, name.trim());
            JOptionPane.showMessageDialog(mainFrame,
                    "开卡成功！\n\n"
                  + "卡号: " + card.getCardId() + "\n"
                  + "持卡人: " + card.getName() + "\n"
                  + "余额: ￥0.00\n"
                  + "累计充值: ￥0.00\n\n"
                  + "请先充值以激活会员等级！");
            // 递归调用，刷新显示
            showMemberCenterDialog();
            return;
        }

        // 已开卡 → 显示会员信息 + 操作
        showCardInfoDialog(card, vipLevel, combined);
    }

    /**
     * 展示会员卡信息弹窗，提供充值操作
     */
    private void showCardInfoDialog(MemberCard card, String vipLevel, double combined) {
        // 计算下一等级还需多少
        String nextLevel;
        double need;
        if (combined >= 10000) {
            nextLevel = "已达最高等级";
            need = 0;
        } else if (combined >= 5000) {
            nextLevel = "钻石会员";
            need = 10000 - combined;
        } else if (combined >= 1000) {
            nextLevel = "金卡会员";
            need = 5000 - combined;
        } else {
            nextLevel = "银卡会员";
            need = 1000 - combined;
        }

        String needStr = need > 0
                ? "\n  升级「" + nextLevel + "」还需: ￥" + String.format("%.0f", need)
                : "\n  ★ " + nextLevel + " ★";

        String info = String.format(
                "═══════ 会员卡信息 ═══════\n\n"
              + "  卡    号: %s\n"
              + "  持 卡 人: %s\n"
              + "  身份证号: %s\n"
              + "  当前等级: %s\n"
              + "  卡内余额: ￥%.2f\n"
              + "  累计充值: ￥%.2f\n"
              + "  综合评定: ￥%.2f\n"
              + "%s\n"
              + "\n══════════════════════════",
                card.getCardId(), card.getName(), card.getIdCard(),
                vipLevel, card.getBalance(), card.getTotalTopUp(),
                combined, needStr);

        String[] options = {"充  值", "关  闭"};
        int choice = JOptionPane.showOptionDialog(mainFrame, info,
                "会员中心 - " + card.getName(),
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            // 充值
            String amountStr = JOptionPane.showInputDialog(mainFrame,
                    "请输入充值金额:\n\n"
                  + "当前余额: ￥" + String.format("%.2f", card.getBalance()) + "\n"
                  + "当前等级: " + vipLevel
                  + (need > 0 ? "\n升级" + nextLevel + "还需: ￥" + String.format("%.0f", need) : ""),
                    "会员卡充值", JOptionPane.PLAIN_MESSAGE);
            if (amountStr == null || amountStr.trim().isEmpty()) {
                showCardInfoDialog(card, vipLevel, combined);
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountStr.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(mainFrame, "金额格式错误，请输入数字！",
                        "错误", JOptionPane.ERROR_MESSAGE);
                showCardInfoDialog(card, vipLevel, combined);
                return;
            }

            String result = hotelManager.topUp(card.getIdCard(), amount);
            if (result == null) {
                // 刷新 card 引用（充值后数据已更新）
                MemberCard updatedCard = hotelManager.getMemberCardByIdCard(card.getIdCard());
                String newLevel = hotelManager.getCombinedVIPLevel(card.getIdCard());
                double newCombined = hotelManager.getCombinedSpending(card.getIdCard());

                String newLevelMsg = !newLevel.equals(vipLevel)
                        ? "\n\n🎉 恭喜升级为 " + newLevel + "！" : "";

                JOptionPane.showMessageDialog(mainFrame,
                        "充值成功！\n\n"
                      + "充值金额: ￥" + String.format("%.2f", amount) + "\n"
                      + "当前余额: ￥" + String.format("%.2f", updatedCard.getBalance()) + "\n"
                      + "当前等级: " + newLevel
                      + newLevelMsg);
                triggerSuccessFlash();
                refreshStatusBar();
                // 刷新显示
                showCardInfoDialog(updatedCard, newLevel, newCombined);
            } else {
                JOptionPane.showMessageDialog(mainFrame, result, "充值失败",
                        JOptionPane.ERROR_MESSAGE);
                showCardInfoDialog(card, vipLevel, combined);
            }
        }
    }

    // ════════════════════ 6. 刷新显示区 ════════════════════

    /** HTML 外壳：统一字体、边距、滚动条样式 */
    private String wrapHtml(String title, String body) {
        return "<html><head><style>"
            + "body { font-family: 'Microsoft YaHei', sans-serif; font-size: 13px; "
            + "       margin: 16px; color: #333; background: #FCFBF8; }"
            + "h2 { color: #1565C0; border-bottom: 2px solid #" + String.format("%02X%02X%02X", COLOR_GOLD.getRed(), COLOR_GOLD.getGreen(), COLOR_GOLD.getBlue())
            + "; padding-bottom: 6px; margin: 0 0 14px 0; font-size: 16px; }"
            + "table { border-collapse: collapse; width: 100%; }"
            + "th { background: #F0EDE8; padding: 8px 10px; text-align: left; "
            + "     font-size: 12px; color: #777; border-bottom: 2px solid #DDD; }"
            + "td { padding: 8px 10px; border-bottom: 1px solid #EEE; }"
            + "tr:hover td { background: #FFFDF5; }"
            + ".free { color: #2E7D32; font-weight: bold; }"
            + ".occupied { color: #1565C0; font-weight: bold; }"
            + ".cleaning { color: #E67E22; font-weight: bold; }"
            + ".maintenance { color: #999; }"
            + ".summary { background: #F5F2ED; padding: 10px 14px; margin-top: 14px; "
            + "           font-size: 13px; }"
            + ".summary b { color: #555; }"
            + ".order-row td { padding: 10px 10px; }"
            + ".stat-card { background: #FFF; border: 1px solid #E8E3DA; "
            + "            padding: 14px; margin: 8px 0; }"
            + ".stat-card h3 { color: #" + String.format("%02X%02X%02X", COLOR_GOLD.getRed(), COLOR_GOLD.getGreen(), COLOR_GOLD.getBlue())
            + "; margin: 0 0 6px 0; font-size: 14px; }"
            + ".stat-num { font-size: 22px; font-weight: bold; color: #1565C0; }"
            + "pre { font-family: 'Microsoft YaHei', monospace; font-size: 13px; "
            + "      line-height: 1.6; }"
            + "hr { border: none; border-top: 1px solid #E0DCD5; margin: 10px 0; }"
            + "</style></head><body>"
            + "<h2>" + title + "</h2>"
            + body
            + "</body></html>";
    }

    /** 获取状态对应的 CSS class 名 */
    private String statusClass(String status) {
        if (Room.STATUS_FREE.equals(status))        return "free";
        if (Room.STATUS_OCCUPIED.equals(status))    return "occupied";
        if (Room.STATUS_CLEANING.equals(status))    return "cleaning";
        return "maintenance";
    }

    /** 刷新房态显示 —— HTML 彩色表格 */
    private void refreshRoomDisplay() {
        StringBuilder body = new StringBuilder();
        List<Room> rooms = hotelManager.getRooms();

        if (rooms.isEmpty()) {
            body.append("<div class='stat-card'><p style='color:#999;text-align:center;'>"
                      + "暂无房间数据，请先录入房间</p></div>");
        } else {
            body.append("<table><tr><th>房号</th><th>类型</th><th>价格</th><th>状态</th></tr>");
            int freeCount = 0, occupiedCount = 0, cleaningCount = 0;
            for (Room room : rooms) {
                body.append("<tr><td><b>").append(room.getRoomNo()).append("</b></td>")
                    .append("<td>").append(room.getType()).append("</td>")
                    .append("<td>￥").append(String.format("%.0f", room.getPrice())).append("</td>")
                    .append("<td class='").append(statusClass(room.getStatus())).append("'>")
                    .append(room.getStatus()).append("</td></tr>");
                if (Room.STATUS_FREE.equals(room.getStatus()))        freeCount++;
                else if (Room.STATUS_OCCUPIED.equals(room.getStatus())) occupiedCount++;
                else if (Room.STATUS_CLEANING.equals(room.getStatus())) cleaningCount++;
            }
            body.append("</table>");

            double rate = rooms.size() > 0 ? occupiedCount * 100.0 / rooms.size() : 0;
            String rateColor = rate >= 80 ? "#C62828" : rate >= 50 ? "#E67E22" : "#2E7D32";
            body.append("<div class='summary'><b>总计:</b> ").append(rooms.size()).append(" 间 &nbsp;|&nbsp; ")
                .append("<b>空闲:</b> <font class='free'>").append(freeCount).append(" 间</font> &nbsp;|&nbsp; ")
                .append("<b>已入住:</b> <font class='occupied'>").append(occupiedCount).append(" 间</font> &nbsp;|&nbsp; ")
                .append("<b>待清洗:</b> <font class='cleaning'>").append(cleaningCount).append(" 间</font> &nbsp;|&nbsp; ")
                .append("<b>入住率:</b> <font color='").append(rateColor).append("'>")
                .append(String.format("%.0f%%", rate)).append("</font></div>");
        }
        displayArea.setText(wrapHtml("&#9670; 实时房态", body.toString()));
        displayArea.setCaretPosition(0);
    }

    /** 刷新底部状态栏 */
    private void refreshStatusBar() {
        List<Room> rooms = hotelManager.getRooms();
        int total = rooms.size();
        int occupied = 0;
        for (Room r : rooms) {
            if (Room.STATUS_OCCUPIED.equals(r.getStatus())) occupied++;
        }
        String rate = total > 0 ? String.format("%.0f%%", occupied * 100.0 / total) : "--";
        statusBarLabel.setText(String.format(
                "总房间: %d 间  |  入住率: %s  |  %s",
                total, rate, BREATHING_DOTS[breathingFrame]));
    }

    /** 刷新订单显示 —— HTML 列表 */
    private void refreshOrderDisplay() {
        StringBuilder body = new StringBuilder();
        List<Order> orders = hotelManager.getOrders();

        if (orders.isEmpty()) {
            body.append("<div class='stat-card'><p style='color:#999;text-align:center;'>"
                      + "暂无订单记录</p></div>");
        } else {
            body.append("<table><tr><th>订单号</th><th>客户</th><th>房间</th><th>日期</th>"
                      + "<th>天数</th><th>金额</th><th>状态</th></tr>");
            for (Order o : orders) {
                String sc = Order.STATUS_CHECKED_IN.equals(o.getStatus()) ? "occupied" : "maintenance";
                body.append("<tr class='order-row'>")
                    .append("<td><b>").append(o.getOrderId()).append("</b></td>")
                    .append("<td>").append(o.getCustomer().getName()).append("</td>")
                    .append("<td>").append(o.getRoom().getRoomNo()).append("</td>")
                    .append("<td>").append(o.getCheckInDate()).append("</td>")
                    .append("<td>").append(o.getDays()).append(" 晚</td>")
                    .append("<td>￥").append(String.format("%.2f", o.getTotalAmount())).append("</td>")
                    .append("<td class='").append(sc).append("'>").append(o.getStatus()).append("</td>")
                    .append("</tr>");
            }
            body.append("</table>");
            body.append("<div class='summary' style='margin-top:10px;'><b>共 ")
                .append(orders.size()).append(" 条订单记录</b></div>");
        }
        displayArea.setText(wrapHtml("&#9670; 历史订单", body.toString()));
        displayArea.setCaretPosition(0);
    }

    // ════════════════════ 6. UI 工具方法 ════════════════════

    /** 在展示区显示纯文本（自动转义 HTML，pre 标签保留排版） */
    private void displayText(String text) {
        String escaped = text
            .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\n", "<br>");
        String html = "<html><head><style>"
            + "body { font-family: 'Microsoft YaHei', sans-serif; font-size: 13px; "
            + "       margin: 16px; color: #333; background: #FCFBF8; }"
            + "pre { font-family: 'Microsoft YaHei', monospace; font-size: 13px; line-height: 1.7; }"
            + "</style></head><body>"
            + "<pre>" + escaped + "</pre>"
            + "</body></html>";
        displayArea.setText(html);
        displayArea.setCaretPosition(0);
    }

    /** 操作成功后的绿闪反馈 */
    private void triggerSuccessFlash() {
        if (flashTimer != null && flashTimer.isRunning()) {
            flashCount = 8;
            return;
        }
        flashCount = 8;
        Color flashGreen = new Color(235, 248, 235);
        flashTimer = new javax.swing.Timer(40, null);
        flashTimer.addActionListener(e -> {
            if (flashCount <= 0) {
                displayArea.setBackground(COLOR_DISPLAY_BG);
                flashTimer.stop();
                return;
            }
            double ratio = flashCount / 8.0;
            int r = (int) (flashGreen.getRed()   * ratio + COLOR_DISPLAY_BG.getRed()   * (1 - ratio));
            int g = (int) (flashGreen.getGreen() * ratio + COLOR_DISPLAY_BG.getGreen() * (1 - ratio));
            int b = (int) (flashGreen.getBlue()  * ratio + COLOR_DISPLAY_BG.getBlue()  * (1 - ratio));
            displayArea.setBackground(new Color(r, g, b));
            flashCount--;
        });
        flashTimer.start();
    }

    /** 启动状态栏呼吸动画（"系统就绪"后的点循环走动） */
    private void startStatusBarAnimation() {
        statusBarTimer = new javax.swing.Timer(500, e -> {
            breathingFrame = (breathingFrame + 1) % BREATHING_DOTS.length;
            refreshStatusBar();
        });
        statusBarTimer.start();
    }

    /** 创建导航按钮 —— 悬停浮起 + 按压下沉 + 平滑过渡 */
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(COLOR_MENU_BG);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setOpaque(true);
        btn.setBorderPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            // 悬停：按钮"浮起"（四周放大 1px + 变蓝）
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_PRIMARY);
                btn.setBorder(BorderFactory.createEmptyBorder(11, 13, 13, 11));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_MENU_BG);
                btn.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            }
            // 按压：文字"下沉"（上边距加大、下边距缩小）
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn.setBorder(BorderFactory.createEmptyBorder(14, 13, 10, 11));
            }
            // 松开：弹簧归位
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (btn.contains(evt.getPoint())) {
                    btn.setBorder(BorderFactory.createEmptyBorder(11, 13, 13, 11));
                } else {
                    btn.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                    btn.setBackground(COLOR_MENU_BG);
                }
            }
        });
        return btn;
    }

    /** 创建通用按钮 —— 按压下沉 + 弹簧回弹 + 释放后轻微放大再缩回 */
    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setOpaque(true);
        btn.setBorderPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            // 按压：文字下沉（上边距+2 下边距-2）
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 4, 18));
                btn.setBackground(darken(bgColor, 0.12f));
            }
            // 松开 → 短暂放大弹跳 → 归位
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // 阶段1: 弹跳放大（50ms 后触发）
                javax.swing.Timer bounceOut = new javax.swing.Timer(40, e -> {
                    btn.setBorder(BorderFactory.createEmptyBorder(4, 16, 8, 20));
                    btn.setBackground(bgColor);
                });
                bounceOut.setRepeats(false);
                bounceOut.start();
                // 阶段2: 归位（120ms 后恢复原状）
                javax.swing.Timer settleBack = new javax.swing.Timer(90, e -> {
                    btn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
                });
                settleBack.setRepeats(false);
                settleBack.start();
            }
        });
        return btn;
    }

    /** 将颜色压暗一定比例 */
    private static Color darken(Color c, float factor) {
        return new Color(
            Math.max(0, (int) (c.getRed()   * (1 - factor))),
            Math.max(0, (int) (c.getGreen() * (1 - factor))),
            Math.max(0, (int) (c.getBlue()  * (1 - factor)))
        );
    }

    /** 创建 "标签 + 输入组件" 的水平行 */
    private JPanel createInputRow(String labelText, JComponent input) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BUTTON);
        label.setPreferredSize(new Dimension(70, 25));
        row.add(label, BorderLayout.WEST);
        row.add(input, BorderLayout.CENTER);
        return row;
    }

    // ──────────── 程序入口 ────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StayHubUI());
    }
}
