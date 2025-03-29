/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.btljava2;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.*;
import java.awt.event.*;

import java.sql.*;

/**
 *
 * @author qphan
 */
public class UserDatabase implements Serializable{

    ArrayList<User> ls = new ArrayList<User>();

    public UserDatabase(ArrayList<User> ls) {
        super();
        this.ls = ls;
    }
    //them tai khoan

    public UserDatabase() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from user_login");
//            sinhvienMap.clear(); // Xóa dữ liệu cũ nếu có
            while (rs.next()) {
                String username = rs.getString("username").toString();
                String password = rs.getString("password").toString();
                String user_role = rs.getString("role").toString();
                boolean role = true;
                if (user_role.equals("admin")) {
                    role = true;
                } else {
                    role = false;
                }
                ls.add(new User(username, password, role));
                // Lưu vào Map
//                sinhvienMap.put(masv, hoten);
//                model.addRow(new Object[]{rs.getInt("id"), rs.getString("masv"), rs.getString("hoten"), 
//                                            rs.getInt("gioitinh"), rs.getString("ngaysinh"), rs.getString("lop")});
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkLogin(String name, String pass) {
        for (User user : ls) {
            if (user.getUsername().equals(name)
                    && user.getPassword().equals(pass)) {
                return true;
            }
        }
        return false;
    }
}
