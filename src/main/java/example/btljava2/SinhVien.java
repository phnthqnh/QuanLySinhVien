/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.btljava2;

/**
 *
 * @author qphan
 */
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class SinhVien implements Serializable{
	/**
	 * 
	 */
	private String masv;
	private String hoten;
	private String gioitinh;
        private String ngaysinh;
	private String lop;
	private ArrayList<HocPhan> hocPhans = new ArrayList<HocPhan>();
        private ArrayList<BangDiem> bangDiems = new ArrayList<BangDiem>();
	
	
	public SinhVien(String masv, String hoten, String gioitinh, String ngaysinh, String lop) {
		this.masv = masv;
		this.hoten = hoten;
		this.gioitinh = gioitinh;
                this.ngaysinh = ngaysinh;
		this.lop = lop;
	}

	public SinhVien() {

	}

	public String getMasv() {
		return masv;
	}

	public void setMasv(String masv) {
		this.masv = masv;
	}

	public String getHoten() {
		return hoten;
	}

	public void setHoten(String hoten) {
		this.hoten = hoten;
	}
        
        public String getNgaysinh() {
		return ngaysinh;
	}

	public void setNgaysinh(String ngaysinh) {
		this.ngaysinh = ngaysinh;
	}

	public String getGender() {
		return gioitinh;
	}

	public void setGender(String gender) {
		this.gioitinh = gender;
	}

	public String getLop() {
		return lop;
	}

	public void setLop(String lop) {
		this.lop = lop;
	}

	public ArrayList<HocPhan> getHocPhans() {
		return hocPhans;
	}

	public void setHocPhans(ArrayList<HocPhan> hocPhans) {
		this.hocPhans = hocPhans;
	}
        
        public ArrayList<BangDiem> getBangDiems() {
		return bangDiems;
	}

	public void setBangDiems(ArrayList<BangDiem> bangDiems) {
		this.bangDiems = bangDiems;
	}
        
        public float getDiem(String mamon) {
            for (BangDiem bd : bangDiems) {
                if (bd.getMaMon().equals(mamon)) {
                    return bd.getDiem();
                }
            }
            return 0;
	}
        
        public void setDiem(String mamon, float diem) {
            for (BangDiem bd : bangDiems) {
                if (bd.getMaMon().equals(mamon)) {
                   bd.setDiem(diem);
                   return;
                }
            }
	}

	public String getStatus(String mamon) {
            for (BangDiem bd : bangDiems) {
                if (bd.getMaMon().equals(mamon)) {
                    return bd.getStatus();
                }
            }
            return null;
	}
        
        public void inHP() {
            System.out.println("Masv: " + masv);
            for (BangDiem bd : bangDiems) {
                System.out.println("mamon: " + bd.getMaMon() + " diem: " + bd.getDiem() +" status: "+bd.getStatus());
            }
        }
        
        public double[] tinhTyLeMon() {
            int truot = 0;
            int dau = 0;
            
            for (BangDiem bd : bangDiems) {
                if (bd.getDiem() >= 4) {
                    dau++;
                } else {
                    truot++;
                }
            }
            int tong = dau + truot;
            double tyleDau = (tong==0) ? 0 : (dau * 100.0/tong);
            double tyleTruot = (tong==0) ? 0 : (truot * 100.0/tong);
            
            return new double[]{tyleDau, tyleTruot};
        }
		
}
//public MainForm() {
//        initComponents();
//        
//        dk_model1 = (DefaultTableModel) dk_tableChuaDK.getModel();
//        loadMonHoc(dk_model1);
//    }
//    
//    private void loadSinhVien(DefaultTableModel model){
//        model.setRowCount(0);
//        //lay cac sinh vien tu database
//        try {
//            Connection conn = DatabaseConnection.getConnection();
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("select * from sinhvien");
//            sinhvienMap.clear(); // Xóa dữ liệu cũ nếu có
//            while (rs.next()) {
//                String masv = rs.getString("masv");
////                maPB = maPhongBan;
//                String hoten = rs.getString("hoten");
//                sv.add(new SinhVien(rs.getString("masv"), rs.getString("hoten"), rs.getString("gioitinh"),
//                                  rs.getString("ngaysinh"), rs.getString("lop")));
//
//                // Lưu vào Map
//                sinhvienMap.put(masv, hoten);
//                model.addRow(new Object[]{rs.getInt("id"), rs.getString("masv"), rs.getString("hoten"), 
//                                            rs.getInt("gioitinh"), rs.getString("ngaysinh"), rs.getString("lop")});
//            }
//            rs.close();
//            stmt.close();
//            conn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    
//    private void loadMonHoc(DefaultTableModel model) {
//        model.setRowCount(0);
//        //lay cac mon hoc tu database
//        try {
//            Connection conn = DatabaseConnection.getConnection();
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("select * from monhoc");
//            monhocnMap.clear(); // Xóa dữ liệu cũ nếu có
//            while (rs.next()) {
//                String mamon = rs.getString("mamon");
////                maPB = maPhongBan;
//                String tenmon = rs.getString("tenmon");
//
//                hp.add(new HocPhan(rs.getString("mamon"), rs.getString("tenmon"), rs.getInt("sotinchi")));
//                // Lưu vào Map
//                monhocnMap.put(mamon, tenmon);
//                model.addRow(new Object[]{rs.getInt("id"), rs.getString("mamon"), 
//                                          rs.getString("tenmon"), rs.getInt("sotinchi")});
//            }
//            rs.close();
//            stmt.close();
//            conn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }