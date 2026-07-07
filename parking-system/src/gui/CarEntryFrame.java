package gui;

import entity.ParkingSpot;
import service.ParkingService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** 车辆入场窗口 */
public class CarEntryFrame extends JFrame {

    private ParkingService service = new ParkingService();
    private JComboBox<String> spotCombo;
    private List<ParkingSpot> freeSpots;
    private JTextField carNumberField;
    private JComboBox<String> carTypeCombo;

    public CarEntryFrame() {
        this.setTitle("车辆入场登记");
        this.setSize(450, 350);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 255, 240));
        this.setContentPane(panel);

        initComponent();
        this.setVisible(true);
    }

    private void initComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        Font font = new Font("微软雅黑", Font.PLAIN, 16);

        // 标题
        JLabel titleLabel = new JLabel("车辆入场登记");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        this.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // 车牌号
        JLabel plateLabel = new JLabel("车牌号：");
        plateLabel.setFont(font);
        gbc.gridx = 0; gbc.gridy = 1;
        this.add(plateLabel, gbc);

        carNumberField = new JTextField(15);
        carNumberField.setFont(font);
        gbc.gridx = 1; gbc.gridy = 1;
        this.add(carNumberField, gbc);

        // 车型
        JLabel typeLabel = new JLabel("车  型：");
        typeLabel.setFont(font);
        gbc.gridx = 0; gbc.gridy = 2;
        this.add(typeLabel, gbc);

        carTypeCombo = new JComboBox<>(new String[]{"小型车", "中型车", "大型车"});
        carTypeCombo.setFont(font);
        gbc.gridx = 1; gbc.gridy = 2;
        this.add(carTypeCombo, gbc);

        // 空闲车位
        JLabel spotLabel = new JLabel("分配车位：");
        spotLabel.setFont(font);
        gbc.gridx = 0; gbc.gridy = 3;
        this.add(spotLabel, gbc);

        spotCombo = new JComboBox<>();
        spotCombo.setFont(font);
        refreshFreeSpots();
        gbc.gridx = 1; gbc.gridy = 3;
        this.add(spotCombo, gbc);

        // 刷新按钮
        JButton refreshBtn = new JButton("刷新车位");
        refreshBtn.setFont(font);
        gbc.gridx = 0; gbc.gridy = 4;
        this.add(refreshBtn, gbc);
        refreshBtn.addActionListener(e -> refreshFreeSpots());

        // 确认入场按钮
        JButton confirmBtn = new JButton("确认入场");
        confirmBtn.setFont(font);
        confirmBtn.setBackground(new Color(60, 179, 113));
        confirmBtn.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 4;
        this.add(confirmBtn, gbc);

        confirmBtn.addActionListener(e -> {
            String carNumber = carNumberField.getText().trim();
            if (carNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入车牌号！");
                return;
            }

            // 检查是否已在场
            if (service.getActiveRecord(carNumber) != null) {
                JOptionPane.showMessageDialog(this, "该车辆已在停车场内！");
                return;
            }

            if (freeSpots == null || freeSpots.isEmpty()) {
                JOptionPane.showMessageDialog(this, "没有空闲车位！");
                return;
            }

            int idx = spotCombo.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "请选择车位！");
                return;
            }

            ParkingSpot spot = freeSpots.get(idx);
            String carType = (String) carTypeCombo.getSelectedItem();
            boolean ok = service.carEntry(carNumber, carType, spot.getId(), spot.getSpotNumber());
            if (ok) {
                JOptionPane.showMessageDialog(this, "入场登记成功！\n车牌：" + carNumber + "\n车位：" + spot.getSpotNumber());
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "入场登记失败，请重试！");
            }
        });
    }

    private void refreshFreeSpots() {
        freeSpots = service.getFreeSpots();
        spotCombo.removeAllItems();
        if (freeSpots.isEmpty()) {
            spotCombo.addItem("（无空闲车位）");
        } else {
            for (ParkingSpot s : freeSpots) {
                spotCombo.addItem(s.getSpotNumber());
            }
        }
    }
}
