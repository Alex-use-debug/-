package gui;

import entity.ParkingSpot;
import service.ParkingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** 停车位管理窗口 */
public class SpotManagementFrame extends JFrame {

    private ParkingService service = new ParkingService();
    private JTable spotTable;
    private DefaultTableModel tableModel;

    public SpotManagementFrame() {
        this.setTitle("停车位管理");
        this.setSize(600, 450);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 255));
        this.setContentPane(panel);

        initComponent();
        refreshTable();
        this.setVisible(true);
    }

    private void initComponent() {
        Font font = new Font("微软雅黑", Font.PLAIN, 14);

        // 顶部操作区
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setOpaque(false);
        JLabel spotLabel = new JLabel("新车位编号：");
        spotLabel.setFont(font);
        JTextField spotField = new JTextField(8);
        spotField.setFont(font);
        JButton addBtn = new JButton("添加车位");
        addBtn.setFont(font);
        JButton delBtn = new JButton("删除选中车位");
        delBtn.setFont(font);
        JButton refreshBtn = new JButton("刷新列表");
        refreshBtn.setFont(font);

        topPanel.add(spotLabel);
        topPanel.add(spotField);
        topPanel.add(addBtn);
        topPanel.add(delBtn);
        topPanel.add(refreshBtn);
        this.add(topPanel, BorderLayout.NORTH);

        // 中间表格
        String[] columns = {"ID", "车位编号", "状态", "占用车牌号"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        spotTable = new JTable(tableModel);
        spotTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        spotTable.setRowHeight(28);
        spotTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(spotTable);
        this.add(scrollPane, BorderLayout.CENTER);

        // 底部统计
        JLabel statsLabel = new JLabel("", SwingConstants.CENTER);
        statsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        this.add(statsLabel, BorderLayout.SOUTH);

        // 事件
        addBtn.addActionListener(e -> {
            String num = spotField.getText().trim();
            if (num.isEmpty()) { JOptionPane.showMessageDialog(this, "请输入车位编号！"); return; }
            if (service.addSpot(num)) {
                JOptionPane.showMessageDialog(this, "车位 " + num + " 添加成功！");
                spotField.setText("");
                refreshTable();
                updateStats(statsLabel);
            } else {
                JOptionPane.showMessageDialog(this, "添加失败，车位编号可能已存在！");
            }
        });

        delBtn.addActionListener(e -> {
            int row = spotTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选择要删除的车位！"); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            String status = (String) tableModel.getValueAt(row, 2);
            if ("占用中".equals(status)) {
                JOptionPane.showMessageDialog(this, "该车位正在使用中，无法删除！");
                return;
            }
            int r = JOptionPane.showConfirmDialog(this, "确定删除该车位吗？", "确认", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                if (service.deleteSpot(id)) {
                    JOptionPane.showMessageDialog(this, "删除成功！");
                    refreshTable();
                    updateStats(statsLabel);
                } else {
                    JOptionPane.showMessageDialog(this, "删除失败！");
                }
            }
        });

        refreshBtn.addActionListener(e -> { refreshTable(); updateStats(statsLabel); });
        updateStats(statsLabel);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<ParkingSpot> list = service.getAllSpots();
        for (ParkingSpot s : list) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getSpotNumber(),
                    s.isFree() ? "空闲" : "占用中",
                    s.isFree() ? "-" : s.getCarNumber()
            });
        }
    }

    private void updateStats(JLabel label) {
        List<ParkingSpot> list = service.getAllSpots();
        long free = list.stream().filter(ParkingSpot::isFree).count();
        label.setText("总车位：" + list.size() + "  |  空闲：" + free + "  |  占用：" + (list.size() - free));
    }
}
