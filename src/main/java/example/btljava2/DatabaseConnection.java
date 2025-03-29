/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.btljava2;

import java.sql.*;
import java.util.Properties;

/**
 *
 * @author qphan
 */
public class DatabaseConnection {
    public static Connection getConnection() {
        String URL = "jdbc:mysql://localhost:3306/btl_java2";
        String USER = "root";
        String PASSWORD = "";
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
