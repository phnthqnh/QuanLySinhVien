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
public class BangDiem implements Serializable{
    private String masv;
    private String mamon;
    private float diem;
    private String status;
    
    public BangDiem(String masv, String mamon, float diem) {
        this.masv = masv;
        this.mamon = mamon;
        this.diem = diem;
        this.status = (diem >= 4.0) ? "Đạt" : "Trượt";
    }
    
    public BangDiem() {
        
    }
    
    public String getMasv() {
		return masv;
	}

	public void setMasv(String masv) {
		this.masv = masv;
	}
    
    public String getMaMon() {
		return mamon;
	}

	public void setMaMon(String mamon) {
		this.mamon = mamon;
	}
        
    public float getDiem() {
            return diem;
	}
        
        public void setDiem(float diem) {
            this.diem = diem;
            this.status = (diem >= 4.0) ? "Đạt" : "Trượt";
	}

	public String getStatus() {
            return status;
	}
}
