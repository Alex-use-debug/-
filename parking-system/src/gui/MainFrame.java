package gui;

import javax.swing.*;
import java.awt.*;

/** 主菜单窗口 */
public class MainFrame extends JFrame {

    public MainFrame() {
        this.setTitle("停车场管理系统 - 主菜单");
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        this.setContentPane(panel);

        initComponent();
        this.setVisible(true);
    }

    private void initComponent() {
        // 顶部标题
        JLabel titleLabel = new JLabel("停车场管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        // 中间按钮面板
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JButton entryBtn = createButton("车辆入场");
        JButton exitBtn = createButton("车辆出场");
        JButton spotBtn = createButton("停车位管理");
        JButton recordBtn = createButton("停车记录查询");
        JButton exitSysBtn = createButton("退出系统");

        btnPanel.add(entryBtn);
        btnPanel.add(exitBtn);
        btnPanel.add(spotBtn);
        btnPanel.add(recordBtn);
        btnPanel.add(new JLabel());
        btnPanel.add(exitSysBtn);

        this.add(btnPanel, BorderLayout.CENTER);

        // 按钮事件
        entryBtn.addActionListener(e -> new CarEntryFrame());
        exitBtn.addActionListener(e -> new CarExitFrame());
        spotBtn.addActionListener(e -> new SpotManagementFrame());
        recordBtn.addActionListener(e -> new RecordQueryFrame());
        exitSysBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "确定退出系统吗？", "确认", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) System.exit(0);
        });
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        btn.setFocusPainted(false);
        return btn;
    }
}
