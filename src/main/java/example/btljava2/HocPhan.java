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

public class HocPhan implements Serializable{
	/**
	 * 
	 */
	private String mamon;
	private String tenmon;
	private int sotinchi;
	private float diem = 0;
	private String status = null;
	
	public HocPhan(String mamon, String tenmon, int sotinchi) {
		this.mamon = mamon;
		this.tenmon = tenmon;
		this.sotinchi = sotinchi;
	}

	public HocPhan() {
            
	}

	public String getMaMon() {
		return mamon;
	}

	public void setMaMon(String mamon) {
		this.mamon = mamon;
	}

	public String getTenMon() {
		return tenmon;
	}

	public void setTenMon(String tenmon) {
		this.tenmon = tenmon;
	}

	public int getSoTinChi() {
		return sotinchi;
	}

	public void setSoTinChi(int soTinChi) {
		this.sotinchi = soTinChi;
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
        
        public void setStatus(String status) {
            this.status = status;
        }

	
}
