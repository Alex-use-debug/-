package service;

import dao.DbUtil;
import entity.Admin;
import entity.ParkingRecord;
import entity.ParkingSpot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** 停车场业务逻辑类 — 封装所有数据库操作 */
public class ParkingService {

    /** 管理员登录验证 */
    public Admin login(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(conn, ps, rs);
        }
        return null;
    }

    /** 获取所有停车位 */
    public List<ParkingSpot> getAllSpots() {
        List<ParkingSpot> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM parking_spot ORDER BY spot_number";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ParkingSpot(
                        rs.getInt("id"),
                        rs.getString("spot_number"),
                        rs.getInt("status"),
                        rs.getString("car_number")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 获取所有空闲车位 */
    public List<ParkingSpot> getFreeSpots() {
        List<ParkingSpot> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM parking_spot WHERE status = 0 ORDER BY spot_number";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ParkingSpot(
                        rs.getInt("id"),
                        rs.getString("spot_number"),
                        0,
                        null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 添加新车位 */
    public boolean addSpot(String spotNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DbUtil.getConnection();
            String sql = "INSERT INTO parking_spot (spot_number, status) VALUES (?, 0)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, spotNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        } finally {
            DbUtil.close(conn, ps);
        }
    }

    /** 删除空闲车位 */
    public boolean deleteSpot(int spotId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM parking_spot WHERE id = ? AND status = 0";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, spotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        } finally {
            DbUtil.close(conn, ps);
        }
    }

    /** 车辆入场 — 使用事务：写入记录 + 占用车位 */
    public boolean carEntry(String carNumber, String carType, int spotId, String spotNumber) {
        Connection conn = null;
        PreparedStatement psRecord = null;
        PreparedStatement psSpot = null;
        try {
            conn = DbUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 写入停车记录（入场时间使用 NOW()）
            String sql1 = "INSERT INTO parking_record (car_number, car_type, entry_time, spot_number) VALUES (?, ?, NOW(), ?)";
            psRecord = conn.prepareStatement(sql1);
            psRecord.setString(1, carNumber);
            psRecord.setString(2, carType);
            psRecord.setString(3, spotNumber);
            int r1 = psRecord.executeUpdate();

            // 2. 将车位标记为已占用
            String sql2 = "UPDATE parking_spot SET status = 1, car_number = ? WHERE id = ? AND status = 0";
            psSpot = conn.prepareStatement(sql2);
            psSpot.setString(1, carNumber);
            psSpot.setInt(2, spotId);
            int r2 = psSpot.executeUpdate();

            if (r1 > 0 && r2 > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (psRecord != null) psRecord.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psSpot != null) psSpot.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /** 根据车牌号查询在场记录（未出场的） */
    public ParkingRecord getActiveRecord(String carNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM parking_record WHERE car_number = ? AND exit_time IS NULL";
            ps = conn.prepareStatement(sql);
            ps.setString(1, carNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new ParkingRecord(
                        rs.getInt("id"),
                        rs.getString("car_number"),
                        rs.getString("car_type"),
                        rs.getTimestamp("entry_time"),
                        null,
                        rs.getString("spot_number"),
                        0.0
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(conn, ps, rs);
        }
        return null;
    }

    /** 车辆出场 — 使用事务：更新记录 + 释放车位 + 计算费用
     *  @param record 停车记录
     *  @param hours 停车小时数
     *  @return 费用 */
    public double carExit(ParkingRecord record, long hours) {
        // 计费规则：每小时5元，最低5元
        double fee = Math.max(5.0, hours * 5.0);

        Connection conn = null;
        PreparedStatement psRecord = null;
        PreparedStatement psSpot = null;
        try {
            conn = DbUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 更新停车记录 — 写入出场时间和费用
            String sql1 = "UPDATE parking_record SET exit_time = NOW(), fee = ? WHERE id = ?";
            psRecord = conn.prepareStatement(sql1);
            psRecord.setDouble(1, fee);
            psRecord.setInt(2, record.getId());
            int r1 = psRecord.executeUpdate();

            // 2. 释放车位
            String sql2 = "UPDATE parking_spot SET status = 0, car_number = NULL WHERE spot_number = ?";
            psSpot = conn.prepareStatement(sql2);
            psSpot.setString(1, record.getSpotNumber());
            int r2 = psSpot.executeUpdate();

            if (r1 > 0 && r2 > 0) {
                conn.commit();
                return fee;
            } else {
                conn.rollback();
                return -1;
            }
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return -1;
        } finally {
            try { if (psRecord != null) psRecord.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psSpot != null) psSpot.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /** 查询所有停车记录（支持按车牌号模糊查询） */
    public List<ParkingRecord> queryRecords(String carNumber) {
        List<ParkingRecord> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection();
            String sql;
            if (carNumber == null || carNumber.trim().isEmpty()) {
                sql = "SELECT * FROM parking_record ORDER BY entry_time DESC";
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT * FROM parking_record WHERE car_number LIKE ? ORDER BY entry_time DESC";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + carNumber.trim() + "%");
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ParkingRecord(
                        rs.getInt("id"),
                        rs.getString("car_number"),
                        rs.getString("car_type"),
                        rs.getTimestamp("entry_time"),
                        rs.getTimestamp("exit_time"),
                        rs.getString("spot_number"),
                        rs.getDouble("fee")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(conn, ps, rs);
        }
        return list;
    }
}
