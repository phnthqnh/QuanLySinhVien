/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package example.btljava2;

import javax.swing.JDialog;

/**
 *
 * @author qphan
 */
public class BtlJava2 {

    public static void main(String[] args) {
//        MainForm frame = new MainForm();
//        frame.setVisible(true);
        try {
            Login login = new Login();
            login.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            login.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
