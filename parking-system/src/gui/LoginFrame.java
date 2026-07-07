package gui;

import entity.Admin;
import service.ParkingService;

import javax.swing.*;
import java.awt.*;

/** 管理员登录窗口 */
public class LoginFrame extends JFrame {

    private ParkingService service = new ParkingService();

    public LoginFrame() {
        this.setTitle("停车场管理系统 - 登录");
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        ImageIcon imageIcon = new ImageIcon("D:\\Users\\zhang\\Documents\\Tencent Files\\3308247152\\nt_qq\\nt_data\\Pic\\2026-07\\Thumb\\0e1826e5cc53bee403e291081ab3429b_0.jpg");
        JPanel panel = new JPanel(new GridBagLayout()){// 设置背景图片
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imageIcon.getImage() != null) {
                    g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setOpaque(false);
        this.setContentPane(panel);

        initComponent();
        this.setVisible(true);
    }

    private void initComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 标题
        JLabel titleLabel = new JLabel("停车场管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        this.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;

        JLabel userLabel = new JLabel("用户名：");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 1;
        this.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        userField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        this.add(userField, gbc);

        JLabel passLabel = new JLabel("密  码：");
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        this.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        this.add(passField, gbc);

        JButton loginBtn = new JButton("登 录");
        loginBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loginBtn.setPreferredSize(new Dimension(120, 40));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        this.add(loginBtn, gbc);

        // 登录按钮事件
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入用户名和密码！");
                return;
            }
            Admin admin = service.login(username, password);
            if (admin != null) {
                JOptionPane.showMessageDialog(this, "登录成功！欢迎，" + admin.getUsername());
                this.dispose();
                new MainFrame();
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误！");
            }
        });

        // 回车键登录
        this.getRootPane().setDefaultButton(loginBtn);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
