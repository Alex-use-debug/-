package gui;

import entity.ParkingRecord;
import service.ParkingService;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

/** 车辆出场窗口 */
public class CarExitFrame extends JFrame {

    private ParkingService service = new ParkingService();
    private JTextField carNumberField;
    private JTextArea infoArea;

    public CarExitFrame() {
        this.setTitle("车辆出场结算");
        this.setSize(500, 420);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 248, 240));
        this.setContentPane(panel);

        initComponent();
        this.setVisible(true);
    }

    private void initComponent() {
        Font font = new Font("微软雅黑", Font.PLAIN, 16);

        // 顶部查询区
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        topPanel.setOpaque(false);
        JLabel label = new JLabel("请输入车牌号：");
        label.setFont(font);
        carNumberField = new JTextField(12);
        carNumberField.setFont(font);
        JButton searchBtn = new JButton("查询");
        searchBtn.setFont(font);
        topPanel.add(label);
        topPanel.add(carNumberField);
        topPanel.add(searchBtn);
        this.add(topPanel, BorderLayout.NORTH);

        // 中间信息展示区
        infoArea = new JTextArea();
        infoArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(255, 255, 255));
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        this.add(scrollPane, BorderLayout.CENTER);

        // 底部按钮区
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setOpaque(false);
        JButton exitBtn = new JButton("确认出场");
        exitBtn.setFont(font);
        exitBtn.setBackground(new Color(220, 80, 80));
        exitBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("返回");
        cancelBtn.setFont(font);
        bottomPanel.add(exitBtn);
        bottomPanel.add(cancelBtn);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // 查询事件
        searchBtn.addActionListener(e -> searchCar());
        this.getRootPane().setDefaultButton(searchBtn);

        // 出场事件
        exitBtn.addActionListener(e -> {
            String carNumber = carNumberField.getText().trim();
            if (carNumber.isEmpty()) { JOptionPane.showMessageDialog(this, "请先输入车牌号查询！"); return; }

            ParkingRecord record = service.getActiveRecord(carNumber);
            if (record == null) { JOptionPane.showMessageDialog(this, "未找到该车辆的在停记录！"); return; }

            Timestamp entry = record.getEntryTime();
            long hours = (System.currentTimeMillis() - entry.getTime()) / (1000 * 60 * 60);
            if (hours < 1) hours = 1;

            double fee = service.carExit(record, hours);
            if (fee >= 0) {
                JOptionPane.showMessageDialog(this,
                        "出场结算成功！\n车牌号：" + carNumber
                                + "\n车位：" + record.getSpotNumber()
                                + "\n停车时长：" + hours + " 小时"
                                + "\n费用：" + fee + " 元");
                infoArea.setText("");
                carNumberField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "结算失败，请重试！");
            }
        });

        cancelBtn.addActionListener(e -> this.dispose());
    }

    private void searchCar() {
        String carNumber = carNumberField.getText().trim();
        if (carNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入车牌号！");
            return;
        }
        ParkingRecord record = service.getActiveRecord(carNumber);
        if (record == null) {
            infoArea.setText("未找到该车辆的在停记录！\n请确认车牌号是否正确，或该车辆已出场。");
        } else {
            long millis = System.currentTimeMillis() - record.getEntryTime().getTime();
            long hours = millis / (1000 * 60 * 60);
            if (hours < 1) hours = 1;
            double estFee = hours * 5.0;

            StringBuilder sb = new StringBuilder();
            sb.append("══════ 停车信息 ══════\n");
            sb.append("车牌号：").append(record.getCarNumber()).append("\n");
            sb.append("车型：").append(record.getCarType()).append("\n");
            sb.append("车位：").append(record.getSpotNumber()).append("\n");
            sb.append("入场时间：").append(record.getEntryTime()).append("\n");
            sb.append("已停时长：").append(hours).append(" 小时\n");
            sb.append("预估费用：").append(estFee).append(" 元\n");
            sb.append("\n（计费规则：每小时5元，最低5元）");
            infoArea.setText(sb.toString());
        }
    }
}
