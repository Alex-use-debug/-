package gui;

import entity.ParkingRecord;
import service.ParkingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** 停车记录查询窗口 */
public class RecordQueryFrame extends JFrame {

    private ParkingService service = new ParkingService();
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public RecordQueryFrame() {
        this.setTitle("停车记录查询");
        this.setSize(800, 500);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 250, 255));
        this.setContentPane(panel);

        initComponent();
        refreshTable(null);
        this.setVisible(true);
    }

    private void initComponent() {
        Font font = new Font("微软雅黑", Font.PLAIN, 14);

        // 顶部搜索区
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setOpaque(false);
        JLabel label = new JLabel("车牌号（支持模糊查询）：");
        label.setFont(font);
        searchField = new JTextField(10);
        searchField.setFont(font);
        JButton searchBtn = new JButton("查询");
        searchBtn.setFont(font);
        JButton allBtn = new JButton("显示全部");
        allBtn.setFont(font);

        topPanel.add(label);
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(allBtn);
        this.add(topPanel, BorderLayout.NORTH);

        // 中间表格
        String[] columns = {"ID", "车牌号", "车型", "入场时间", "出场时间", "车位", "费用(元)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        // 底部统计
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setOpaque(false);
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.setFont(font);
        bottomPanel.add(refreshBtn);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // 事件
        searchBtn.addActionListener(e -> refreshTable(searchField.getText().trim()));
        allBtn.addActionListener(e -> { searchField.setText(""); refreshTable(null); });
        refreshBtn.addActionListener(e -> refreshTable(searchField.getText().trim()));
        searchField.addActionListener(e -> refreshTable(searchField.getText().trim()));
    }

    private void refreshTable(String carNumber) {
        tableModel.setRowCount(0);
        List<ParkingRecord> list = service.queryRecords(carNumber);
        for (ParkingRecord r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getCarNumber(),
                    r.getCarType(),
                    r.getEntryTime(),
                    r.getExitTime() == null ? "在场" : r.getExitTime().toString(),
                    r.getSpotNumber(),
                    r.getExitTime() == null ? "-" : r.getFee()
            });
        }
    }
}
