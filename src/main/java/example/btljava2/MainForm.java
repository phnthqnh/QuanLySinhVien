/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package example.btljava2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

import java.sql.*;

import example.btljava2.HocPhan;
import example.btljava2.SinhVien;
import javax.swing.table.TableRowSorter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author qphan
 */
public class MainForm extends javax.swing.JFrame {
    //button giới tính
    private ButtonGroup btnGender;
    
    //tạo các tablemodel
    private DefaultTableModel m_model;
    private DefaultTableModel s_model;
    private DefaultTableModel d_model;
    private DefaultTableModel dk_model1;
    private DefaultTableModel dk_model2;
    
    //biến lưu số dòng được chọn trong bảng
    private int selectRowMH = -1; //bien luu dong duoc chon
    private int selectRowSV = -1; //bien luu dong duoc chon
    private int selectRowDiem = -1;
    
    //biến lưu mã sinh viên, mã môn, sinh viên được chọn
    private String selectMasv = null;
    private String selectMamon = null;
    private SinhVien selectSv = null;
    
    private ArrayList<SinhVien> SV = new ArrayList<SinhVien>();
    private ArrayList<HocPhan> HP = new ArrayList<HocPhan>();
    private ArrayList<BangDiem> BD = new ArrayList<BangDiem>();
    
    private JPanel pannelChart;
   

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
        //pannel thống kê
        pannelChart = new JPanel();
        pannelChart.setLayout(new BorderLayout());
        tk_panelBieuDo.add(pannelChart, BorderLayout.CENTER);
        
        btnGender = new ButtonGroup();
        btnGender.add(jRadioButton1);
        btnGender.add(jRadioButton2);
        dk_jCBMasv.addItem("---");
        d_jCBMasv.addItem("---");
        tk_jCBMasv.addItem("---");

        loadSinhVien();
        loadMonHoc();
        for (SinhVien i : SV) {
            loadMHofSV(i);
//            loadDiemOfSV(i);
//            i.inHP(i);
        }
        
        //xử lý trang thống kê
        //jComboBox sinh viên
        tk_jCBMasv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectMasv = (String) tk_jCBMasv.getSelectedItem();
                if (selectMasv.equals("---") || selectMasv == null) {
                    tk_clear();
                    return;
                } else {
                    selectSv = timSV(selectMasv);
                    tk_txtHoten.setText(selectSv.getHoten());
                    tk_txtLop.setText(selectSv.getLop());
                    tk_txtNgaysinh.setText(selectSv.getNgaysinh());
                    loadDiemOfSV(selectSv);
                    
                    //cập nhật biểu đồ
                    updateChart();
                }
            }
        });
        
        //xử lý trang quản lý điểm
        d_model = (DefaultTableModel) d_tblDiemthi.getModel();
        //jComboBox sinh viên trong trang quản lý điểm
        d_jCBMasv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectMasv = (String) d_jCBMasv.getSelectedItem();
                if (selectMasv.equals("---") || selectMasv == null) {
                    d_clear();
                    d_model.setRowCount(0);
                    return;
                } else {
                    selectSv = timSV(selectMasv);
                    d_txtHoten.setText(selectSv.getHoten());
                    loadDiemOfSV(selectSv);
                    selectSv.inHP();
                    FillTblDiem(selectSv);
                }
            }
        });
        //bắt sự kiện click của tbl điểm thi
        d_tblDiemthi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRowDiem = d_tblDiemthi.getSelectedRow();
                if (selectRowDiem != -1) {
                    d_txtMamon.setText(d_model.getValueAt(selectRowDiem, 2).toString());
                    HocPhan hp = timHP(d_txtMamon.getText().trim());
                    d_txtTenmon.setText(hp.getTenMon());
                    d_txtNhapdiem.setText(d_model.getValueAt(selectRowDiem, 3).toString());
                    selectMamon = d_model.getValueAt(selectRowDiem, 2).toString();
                }
            }
        });
        
        
        //xử lý trang đăng ký học
        //bảng môn chưa đăng ký
        dk_model1 = (DefaultTableModel) dk_tableChuaDK.getModel();
        FillTblMonHoc(dk_model1);
        //bắt sự kiện click của tbl chưa đki trong trang đăng ký học
        dk_tableChuaDK.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dk_btnHuy.setEnabled(false);
                dk_btnDK.setEnabled(true);
                selectRowMH = dk_tableChuaDK.getSelectedRow();
                if (selectRowMH != -1) {
                    dk_txtMonhoc.setText(dk_model1.getValueAt(selectRowMH, 2).toString());
                    selectMamon = dk_model1.getValueAt(selectRowMH, 1).toString();
                }
            }
        });
        
        //bảng môn đã đăng ký
        dk_model2 = (DefaultTableModel) dk_tblDaDK.getModel();
        //jComboBox sinhvien trong trang đăng ký học
        dk_jCBMasv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectMasv = (String) dk_jCBMasv.getSelectedItem();
                if (selectMasv.equals("---")) {
                    dk_clear();
                    return;
                } else {
                    selectSv = timSV(selectMasv);
                    dk_txtHoten.setText(selectSv.getHoten());
                    dk_txtLop.setText(selectSv.getLop());
//                    loadMHofSV(selectSv);
                    FillTblMHofSV(selectSv.getHocPhans());
                }
            }
        });
        //bắt sự kiện click của tbl đã đki trong trang đăng ký học
        dk_tblDaDK.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dk_btnDK.setEnabled(false);
                dk_btnHuy.setEnabled(true);
                selectRowMH = dk_tblDaDK.getSelectedRow();
                if (selectRowMH != -1) {
                    dk_txtMonhoc.setText(dk_model2.getValueAt(selectRowMH, 2).toString());
                    selectMamon = dk_model2.getValueAt(selectRowMH, 1).toString();
                }
            }
        });
        
        
        //xử lý trang quản lý sinh viên
        s_model = (DefaultTableModel) s_tblSV.getModel();
        FillTblSinhVien(s_model);
        //bat su kien click cua table sinh viên
        s_tblSV.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRowSV = s_tblSV.getSelectedRow();
                if (selectRowSV != -1) {
                    s_txtMasv.setText(s_model.getValueAt(selectRowSV, 1).toString());
                    s_txtHoten.setText(s_model.getValueAt(selectRowSV, 2).toString());
                    String gioitinh = s_model.getValueAt(selectRowSV, 3).toString();
                    if (gioitinh.equalsIgnoreCase("Nam")) {
                        jRadioButton1.setSelected(true);
                    } else {
                        jRadioButton2.setSelected(true);
                    }
                    s_txtNgaysinh.setText(s_model.getValueAt(selectRowSV, 4).toString());
                    s_txtLop.setText(s_model.getValueAt(selectRowSV, 5).toString());
                }
            }
        });
        
        // xử lý trang quản lý môn học
        m_model = (DefaultTableModel) m_tblMonhoc.getModel();
        FillTblMonHoc(m_model);
        //bắt xự kiện click của tabel môn học
        m_tblMonhoc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRowMH = m_tblMonhoc.getSelectedRow();
                if (selectRowMH != -1) {
                    m_txtMamon.setText(m_model.getValueAt(selectRowMH, 1).toString());
                    m_txtTenmon.setText(m_model.getValueAt(selectRowMH, 2).toString());
                    m_txtSotinchi.setText(m_model.getValueAt(selectRowMH, 3).toString());
                }
            }
        });
        
//        DefaultTableModel dk_model1 = (DefaultTableModel) dk_tableChuaDK.getModel();
//        FillTblMonHoc(dk_model1);   
    }
    
    //vẽ biểu đồ
    private void updateChart() {
        if (selectSv == null) return;
        
        //lấy tỉ lệ 
        double[] tyle = selectSv.tinhTyLeMon();

            // Tạo dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Môn đạt", tyle[0]);
        dataset.setValue("Môn trượt", tyle[1]);

        // Tạo Pie Chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Tỷ lệ môn đạt/trượt của " + selectSv.getHoten(),
                dataset,
                true,
                true,
                false
        );

        // Tùy chỉnh màu sắc
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Môn đạt", new Color(50, 130, 246));  // Xanh biển
        plot.setSectionPaint("Môn trượt", new Color(255, 51, 51)); // Đỏ

        // 3. Hiển thị biểu đồ trên tk_panelBieuDo
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(530, 400));
        // Hiển thị biểu đồ
         // Xóa các thành phần cũ trước khi thêm biểu đồ mới
        tk_panelBieuDo.removeAll();
        tk_panelBieuDo.setLayout(new BorderLayout());
        tk_panelBieuDo.add(chartPanel, BorderLayout.CENTER);
//        tk_panelBieuDo.validate();
        tk_panelBieuDo.revalidate(); // Cập nhật giao diện
        tk_panelBieuDo.repaint();    // Vẽ lại
        }
    
    //tìm sinh viên theo mã sinh viên
    private SinhVien timSV(String masv) {
        for (SinhVien i : SV) {
            if ((i.getMasv()).equals(masv)) {
                return i;
            }
        }
        return null;
    }
    
    //tìm học phần theo mã môn
    private HocPhan timHP(String mamon) {
        for (HocPhan i : HP) {
            if ((i.getMaMon()).equals(mamon)) {
                return i;
            }
        }
        return null;
    }
    
    //tìm bảng điểm theo mã sinh viên
    private BangDiem timBD(String masv) {
        for (BangDiem i : BD) {
            if (i.getMasv().equals(masv)){
                return i;
            }
        }
        return null;
    }
    
    //xóa các giá trị trong trang thống kê
    private void tk_clear() {
        tk_txtHoten.setText("");
        tk_txtLop.setText("");
        tk_txtNgaysinh.setText("");
        
    }
    
    //lấy điểm của sinh viên từ database
    private void loadDiemOfSV(SinhVien sv) {
        BD.clear();
        sv.getBangDiems().clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstm = conn.prepareStatement("select * from diemthi where masv = ?");
            pstm.setString(1, sv.getMasv());
            ResultSet rs = pstm.executeQuery();
            
            
            while (rs.next()) {
                String mamon = rs.getString("mamon");
                Float diem = rs.getFloat("diem");
                BD.add(new BangDiem(sv.getMasv(), mamon, diem));
            }
            sv.setBangDiems(BD);
//            FillTblMHofSV(sv.getHocPhans());
            rs.close();
            pstm.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // điền danh sách điểm thi của sinh viên vào bảng
    private void FillTblDiem (SinhVien sv) {
        d_model.setRowCount(0);
        int n=1;
        for (HocPhan i : sv.getHocPhans()) {
            d_model.addRow(new Object[]{n, sv.getMasv(), i.getMaMon(), sv.getDiem(i.getMaMon()), sv.getStatus(i.getMaMon())});
            n++;
        }
    }
    
    //xóa các giá trị trong trang quản lý điểm
    private void d_clear() {
        d_txtMamon.setText("");
        d_txtTenmon.setText("");
        d_txtNhapdiem.setText("");
        
    }
    
    
    //lấy các học phần đã đăng ký của sinh viên từ database
    private void loadMHofSV(SinhVien sv) {
        //lay cac môn học của sinh vien từ dangkyhoc
        sv.getHocPhans().clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstm = conn.prepareStatement("select * from dangkyhoc where masv = ?");
            pstm.setString(1, sv.getMasv());
            ResultSet rs = pstm.executeQuery();
            
            
            while (rs.next()) {
                String mamon = rs.getString("mamon");
                HocPhan hp = timHP(mamon);
                if (hp == null) {
                    continue;
                }
                // Kiểm tra xem môn học đã tồn tại trong danh sách hay chưa
                boolean isExists = false;
                for (HocPhan existingHP : sv.getHocPhans()) {
                    if (existingHP.getMaMon().equals(mamon)) {
                        isExists = true;
                        break;
                    }
                }
                

                // Nếu chưa tồn tại, mới thêm vào danh sách
                if (!isExists) {
                    
                    sv.getHocPhans().add(hp);
                }
            }
            //FillTblMHofSV(sv.getHocPhans());
            rs.close();
            pstm.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // điền danh sách học phần của sinh viên vào bảng "Học phần đã đăng ký"
    private void FillTblMHofSV (ArrayList<HocPhan> dshpcuaSV) {
        dk_model2.setRowCount(0);
        int n=1;
        for (HocPhan i : dshpcuaSV) {
            dk_model2.addRow(new Object[]{n, i.getMaMon(), i.getTenMon(), i.getSoTinChi()});
            n++;
        }
    }
    //xóa các giá trị trong trang đăng ký học
    private void dk_clear() {
        dk_txtHoten.setText("");
        dk_txtLop.setText("");
        dk_model2.setRowCount(0);
    }
    
    //lấy các sinh viên từ database
    private void loadSinhVien(){
        SV.clear();
        //lay cac sinh vien tu database
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from sinhvien order by id");
//            sinhvienMap.clear(); // Xóa dữ liệu cũ nếu có
            while (rs.next()) {
                String masv = rs.getString("masv");
//                maPB = maPhongBan;
                String hoten = rs.getString("hoten");
                SV.add(new SinhVien(rs.getString("masv"), rs.getString("hoten"), rs.getString("gioitinh"),
                                  rs.getString("ngaysinh"), rs.getString("lop")));
                dk_jCBMasv.addItem(masv);
                d_jCBMasv.addItem(masv);
                tk_jCBMasv.addItem(masv);
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
    //điền sinh viên vào bảng
    private void FillTblSinhVien (DefaultTableModel model) {
        model.setRowCount(0);
        int n=1;
        for (SinhVien i : SV) {
            model.addRow(new Object[]{n, i.getMasv(), i.getHoten(), i.getGender(), i.getNgaysinh(), i.getLop()});
            n++;
        }
    }
    //xóa các giá trị trong trang quản lý sinh viên
    private void s_clear() {
        s_txtMasv.setText("");
        s_txtHoten.setText("");
        btnGender.clearSelection();
        s_txtNgaysinh.setText("");
        s_txtLop.setText("");
    }
    
    //lấy các môn học từ database
    private void loadMonHoc() {
        //lay cac mon hoc tu database
        HP.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from monhoc order by id");
//            monhocnMap.clear(); // Xóa dữ liệu cũ nếu có
            while (rs.next()) {
                String mamon = rs.getString("mamon");
//                maPB = maPhongBan;
                String tenmon = rs.getString("tenmon");

                HP.add(new HocPhan(rs.getString("mamon"), rs.getString("tenmon"), rs.getInt("sotinchi")));
                // Lưu vào Map
//                monhocnMap.put(mamon, tenmon);
//                model.addRow(new Object[]{rs.getInt("id"), rs.getString("mamon"), 
//                                          rs.getString("tenmon"), rs.getInt("sotinchi")});
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //điền môn học vào bảng quản lý môn học
    private void FillTblMonHoc (DefaultTableModel model) {
        model.setRowCount(0);
        int n=1;
        for (HocPhan i : HP) {
            model.addRow(new Object[]{n, i.getMaMon(), i.getTenMon(), i.getSoTinChi()});
            n++;
        }
    }
    
    //xóa các giá trị của trang quản lý môn học
    private void m_clear() {
        m_txtMamon.setText("");
        m_txtTenmon.setText("");
        m_txtSotinchi.setText("");
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        m_txtMamon = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        m_txtTenmon = new javax.swing.JTextField();
        m_txtSotinchi = new javax.swing.JTextField();
        m_btnThem = new javax.swing.JButton();
        m_btnSua = new javax.swing.JButton();
        m_btnXoa = new javax.swing.JButton();
        JScrollPane = new javax.swing.JScrollPane();
        m_tblMonhoc = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        d_jCBMasv = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        d_txtMamon = new javax.swing.JTextField();
        d_txtHoten = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        d_txtTenmon = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        d_tblDiemthi = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        d_txtNhapdiem = new javax.swing.JTextField();
        d_btnNhapdiem = new javax.swing.JButton();
        d_btnXoadiem = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        tk_jCBMasv = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        tk_txtHoten = new javax.swing.JTextField();
        tk_txtNgaysinh = new javax.swing.JTextField();
        tk_txtLop = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tk_panelBieuDo = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        dk_txtHoten = new javax.swing.JTextField();
        dk_jCBMasv = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        dk_txtLop = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        dk_txtMonhoc = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        JScrollPane1 = new javax.swing.JScrollPane();
        dk_tableChuaDK = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        JScrollPane2 = new javax.swing.JScrollPane();
        dk_tblDaDK = new javax.swing.JTable();
        dk_btnDK = new javax.swing.JButton();
        dk_btnHuy = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        s_txtTim = new javax.swing.JTextField();
        s_btnTim = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        s_txtMasv = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        s_txtHoten = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        s_txtLop = new javax.swing.JTextField();
        s_txtNgaysinh = new javax.swing.JTextField();
        s_btnSua = new javax.swing.JButton();
        s_btnThem = new javax.swing.JButton();
        s_btnXoa = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        s_tblSV = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel12.setText("Mã môn học");

        jLabel13.setText("Tên môn học");

        jLabel14.setText("Số tín chỉ");

        m_btnThem.setText("Thêm");
        m_btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnThemActionPerformed(evt);
            }
        });

        m_btnSua.setText("Sửa");
        m_btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnSuaActionPerformed(evt);
            }
        });

        m_btnXoa.setText("Xóa");
        m_btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnXoaActionPerformed(evt);
            }
        });

        m_tblMonhoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã môn học", "Tên môn học", "Số tín chỉ"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JScrollPane.setViewportView(m_tblMonhoc);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(m_txtSotinchi, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(m_txtTenmon, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(26, 26, 26)
                        .addComponent(m_txtMamon, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_btnThem)
                    .addComponent(m_btnSua)
                    .addComponent(m_btnXoa))
                .addGap(96, 96, 96))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 614, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(m_txtMamon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_btnThem))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(m_txtTenmon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_btnSua))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(m_txtSotinchi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_btnXoa))
                .addGap(27, 27, 27)
                .addComponent(JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Quản lý môn học", jPanel1);

        jLabel1.setText("Mã sinh viên");

        jLabel2.setText("Mã môn học");

        jLabel3.setText("Họ và tên");

        d_txtMamon.setEnabled(false);

        d_txtHoten.setEnabled(false);

        jLabel4.setText("Tên môn học");

        d_txtTenmon.setEnabled(false);

        d_tblDiemthi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã sinh viên", "Mã môn", "Điểm", "Trạng thái"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(d_tblDiemthi);

        jLabel5.setText("Nhập điểm");

        d_btnNhapdiem.setText("Nhập điểm");
        d_btnNhapdiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                d_btnNhapdiemActionPerformed(evt);
            }
        });

        d_btnXoadiem.setText("Xóa điểm");
        d_btnXoadiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                d_btnXoadiemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(d_txtNhapdiem))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(d_txtMamon))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(d_jCBMasv, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(d_txtTenmon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(d_txtHoten, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(d_btnNhapdiem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(d_btnXoadiem)))))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(d_jCBMasv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(d_txtHoten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(d_txtMamon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(d_txtTenmon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(d_txtNhapdiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(d_btnNhapdiem)
                    .addComponent(d_btnXoadiem))
                .addGap(43, 43, 43)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Quản lý điểm", jPanel3);

        jLabel20.setText("Mã sinh viên");

        jLabel21.setText("Họ và tên");

        tk_txtHoten.setEditable(false);

        tk_txtNgaysinh.setEditable(false);

        tk_txtLop.setEditable(false);

        jLabel22.setText("Lớp chuyên ngành");

        jLabel23.setText("Ngày sinh");

        javax.swing.GroupLayout tk_panelBieuDoLayout = new javax.swing.GroupLayout(tk_panelBieuDo);
        tk_panelBieuDo.setLayout(tk_panelBieuDoLayout);
        tk_panelBieuDoLayout.setHorizontalGroup(
            tk_panelBieuDoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
        );
        tk_panelBieuDoLayout.setVerticalGroup(
            tk_panelBieuDoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21))
                .addGap(40, 40, 40)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tk_jCBMasv, 0, 135, Short.MAX_VALUE)
                    .addComponent(tk_txtHoten))
                .addGap(70, 70, 70)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23))
                .addGap(28, 28, 28)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tk_txtNgaysinh, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(tk_txtLop))
                .addGap(70, 70, 70))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(tk_panelBieuDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(tk_jCBMasv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tk_txtNgaysinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tk_txtHoten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(tk_txtLop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addGap(27, 27, 27)
                .addComponent(tk_panelBieuDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Thống kê", jPanel5);

        jLabel6.setText("Mã sinh viên");

        dk_txtHoten.setEnabled(false);

        jLabel7.setText("Họ và tên");

        jLabel8.setText("Lớp chuyên ngành");

        dk_txtLop.setEnabled(false);

        jLabel9.setText("Học phần đang chọn");

        dk_txtMonhoc.setEnabled(false);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 255, 102));
        jLabel10.setText("Học phần đã đăng ký");

        dk_tableChuaDK.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã môn", "Tên môn", "Số tín chỉ"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JScrollPane1.setViewportView(dk_tableChuaDK);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 204, 102));
        jLabel11.setText("Học phần đang chờ đăng ký");

        dk_tblDaDK.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã môn", "Tên môn", "Số tín chỉ"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JScrollPane2.setViewportView(dk_tblDaDK);

        dk_btnDK.setText("Đăng ký");
        dk_btnDK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dk_btnDKActionPerformed(evt);
            }
        });

        dk_btnHuy.setText("Hủy");
        dk_btnHuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dk_btnHuyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(dk_btnDK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addGap(141, 141, 141)
                        .addComponent(dk_btnHuy))
                    .addComponent(JScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(JScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dk_txtHoten)
                            .addComponent(dk_jCBMasv, 0, 171, Short.MAX_VALUE))
                        .addGap(48, 48, 48)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(26, 26, 26)
                                .addComponent(dk_txtLop, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dk_txtMonhoc, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)))))
                .addGap(25, 25, 25))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(210, 210, 210)
                    .addComponent(jLabel11)
                    .addContainerGap(219, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(dk_jCBMasv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(dk_txtLop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dk_txtHoten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(dk_txtMonhoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(70, 70, 70)
                .addComponent(JScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addComponent(dk_btnHuy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dk_btnDK, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(JScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(123, 123, 123)
                    .addComponent(jLabel11)
                    .addContainerGap(419, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Đăng ký học", jPanel4);

        s_btnTim.setText("Tìm kiếm");
        s_btnTim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                s_btnTimActionPerformed(evt);
            }
        });

        jLabel15.setText("Mã sinh viên");

        jLabel16.setText("Họ và tên");

        jLabel17.setText("Giới tính");

        jRadioButton1.setText("Nam");

        jRadioButton2.setText("Nữ");

        jLabel18.setText("Ngày sinh");

        jLabel19.setText("Lớp chuyên ngành");

        s_btnSua.setText("Sửa");
        s_btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                s_btnSuaActionPerformed(evt);
            }
        });

        s_btnThem.setText("Thêm");
        s_btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                s_btnThemActionPerformed(evt);
            }
        });

        s_btnXoa.setText("Xóa");
        s_btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                s_btnXoaActionPerformed(evt);
            }
        });

        s_tblSV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã sinh viên", "Họ và tên", "Giới tính", "Ngày sinh", "Lớp chuyên ngành"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(s_tblSV);
        if (s_tblSV.getColumnModel().getColumnCount() > 0) {
            s_tblSV.getColumnModel().getColumn(0).setPreferredWidth(20);
            s_tblSV.getColumnModel().getColumn(1).setPreferredWidth(50);
            s_tblSV.getColumnModel().getColumn(2).setPreferredWidth(200);
            s_tblSV.getColumnModel().getColumn(3).setPreferredWidth(50);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel18))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(s_txtNgaysinh, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 193, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(s_txtLop, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(s_btnXoa))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jRadioButton1)
                                .addGap(59, 59, 59)
                                .addComponent(jRadioButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(s_btnSua))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(s_txtTim, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(s_btnTim))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(s_txtMasv, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(s_txtHoten, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(115, 115, 115)
                                .addComponent(s_btnThem)))))
                .addGap(73, 73, 73))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(s_txtTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(s_btnTim))
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(s_txtMasv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(s_btnThem))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(s_txtHoten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(s_btnSua))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(s_txtNgaysinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(s_txtLop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(s_btnXoa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        jTabbedPane1.addTab("Quản lý sinh viên", jPanel2);

        jMenu1.setText("Hướng dẫn");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Đăng xuất");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 666, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void d_btnNhapdiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_d_btnNhapdiemActionPerformed
        // TODO add your handling code here:
        if (selectRowDiem == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn để nhập điểm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String mamon = selectMamon;
        String masv = selectSv.getMasv();
        float diem = Float.parseFloat(d_txtNhapdiem.getText().trim());
        String status_old = null;
        String status_new = (diem >= 4.0) ? "Đạt" : "Trượt";
        
        for (HocPhan hp : selectSv.getHocPhans()) {
            if (hp.getMaMon().equals(mamon)){
                status_old = selectSv.getStatus(hp.getMaMon());
                break;
            }
        }
        
        //nếu đã có điểm -> update điểm
        if (status_old != null) {
            String sql = "UPDATE diemthi SET diem = ?, status = ? WHERE masv = ? AND mamon = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement pstm = conn.prepareStatement(sql)) {

                pstm.setFloat(1, diem);
                pstm.setString(2, status_new);
                pstm.setString(3, masv);
                pstm.setString(4, mamon);

                int affectRow = pstm.executeUpdate();
                if (affectRow > 0) {
                    //cap nhat lai jtable
                    loadDiemOfSV(selectSv);
                    FillTblDiem(selectSv);

                    JOptionPane.showMessageDialog(this, "Nhập điểm thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    d_clear();
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy môn học", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                conn.close();
                    pstm.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        } else { //nếu chưa có thì tạo mới
            String sql = "INSERT INTO diemthi (masv, mamon, diem, status) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement pstm = conn.prepareStatement(sql)) {

                pstm.setString(1, masv);
                pstm.setString(2, mamon);
                pstm.setFloat(3, diem);
                pstm.setString(4, status_new);

                int affectRow = pstm.executeUpdate();
                if (affectRow > 0) {
                    //cap nhat lai jtable
                    loadDiemOfSV(selectSv);
                    FillTblDiem(selectSv);

                    JOptionPane.showMessageDialog(this, "Nhập điểm thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    d_clear();
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy môn học", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                conn.close();
                    pstm.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_d_btnNhapdiemActionPerformed

    private void m_btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnThemActionPerformed
        // TODO add your handling code here:

        if (m_txtMamon.getText().trim().isEmpty() || m_txtTenmon.getText().trim().isEmpty() || m_txtSotinchi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO monhoc (mamon, tenmon, sotinchi) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, m_txtMamon.getText().trim());
            pstmt.setString(2, m_txtTenmon.getText().trim());
            pstmt.setInt(3, Integer.parseInt(m_txtSotinchi.getText().trim()));
            //hp.add(new HocPhan(m_txtMamon.getText().trim(), m_txtTenmon.getText().trim(), Integer.parseInt(m_txtSotinchi.getText().trim())));
            

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                loadMonHoc();
                FillTblMonHoc(m_model);
                JOptionPane.showMessageDialog(this, "Thêm môn học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                m_clear();
            }
            conn.close();
                    pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } 
    }//GEN-LAST:event_m_btnThemActionPerformed

    private void m_btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnXoaActionPerformed
        // TODO add your handling code here:
        selectRowMH = m_tblMonhoc.getSelectedRow();
        
        if (selectRowMH == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một môn để xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            if (JOptionPane.showConfirmDialog(this, "Bạn có muốn xóa môn này không?") == 0) {
                String mamon = (String) m_model.getValueAt(selectRowMH, 1);
        
                String sql = "delete from monhoc where mamon = ?";

                try (Connection conn = DatabaseConnection.getConnection();
                        PreparedStatement pstm = conn.prepareStatement(sql)) {
                    pstm.setString(1, mamon);

                    int affectedRows = pstm.executeUpdate();
                    if(affectedRows > 0){
//                        m_model.removeRow(selectMH);
                        loadMonHoc();
                        FillTblMonHoc(m_model);
                        JOptionPane.showMessageDialog(this, "Xóa thành công", "Cập nhật", JOptionPane.INFORMATION_MESSAGE);
                        m_clear();
                    }
                    conn.close();
                    pstm.close();

                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_m_btnXoaActionPerformed

    private void m_btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnSuaActionPerformed
        // TODO add your handling code here:
        selectRowMH = m_tblMonhoc.getSelectedRow();
        
        if (selectRowMH == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để cập nhật", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        
        if (m_txtMamon.getText().trim().isEmpty() || m_txtTenmon.getText().trim().isEmpty() || m_txtSotinchi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String sql = "UPDATE monhoc SET mamon = ?, tenmon = ?, sotinchi = ? WHERE mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql)) {
            
            pstm.setString(1, m_txtMamon.getText().trim());
            pstm.setString(2, m_txtTenmon.getText().trim());
            pstm.setInt(3, Integer.parseInt(m_txtSotinchi.getText().trim()));
            pstm.setString(4, m_txtMamon.getText().trim());
            
            int affectRow = pstm.executeUpdate();
            if (affectRow > 0) {
                //cap nhat lai jtable
                loadMonHoc();
                FillTblMonHoc(m_model);
                
                JOptionPane.showMessageDialog(this, "Cập nhật thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                m_clear();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy môn học", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            conn.close();
                    pstm.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_m_btnSuaActionPerformed

    private void s_btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_s_btnXoaActionPerformed
        // TODO add your handling code here:
        selectRowSV = s_tblSV.getSelectedRow();
        
        if (selectRowSV == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên để xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            if (JOptionPane.showConfirmDialog(this, "Bạn có muốn xóa sinh viên này không?") == 0) {
                String masv = (String) s_model.getValueAt(selectRowSV, 1);
        
                String sql = "delete from sinhvien where masv = ?";

                try (Connection conn = DatabaseConnection.getConnection();
                        PreparedStatement pstm = conn.prepareStatement(sql)) {
                    pstm.setString(1, masv);

                    int affectedRows = pstm.executeUpdate();
                    if(affectedRows > 0){
                        loadSinhVien();
                        FillTblSinhVien(s_model);
                        JOptionPane.showMessageDialog(this, "Xóa thành công", "Cập nhật", JOptionPane.INFORMATION_MESSAGE);
                        s_clear();
                    }
                    conn.close();
                    pstm.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_s_btnXoaActionPerformed

    private void s_btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_s_btnThemActionPerformed
        // TODO add your handling code here:
        if (s_txtMasv.getText().trim().isEmpty() || s_txtHoten.getText().trim().isEmpty() || s_txtNgaysinh.getText().trim().isEmpty() ||
                s_txtLop.getText().trim().isEmpty() || btnGender.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String gioitinh = "";
        if (jRadioButton1.isSelected()) {
            gioitinh = "Nam";
        } else {
            gioitinh = "Nữ";
        }

        String sql = "INSERT INTO sinhvien (masv, hoten, gioitinh, ngaysinh, lop) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, s_txtMasv.getText().trim());
            pstmt.setString(2, s_txtHoten.getText().trim());
            pstmt.setString(3, gioitinh);
            pstmt.setString(4, s_txtNgaysinh.getText().trim());
            pstmt.setString(5, s_txtLop.getText().trim());
            //hp.add(new HocPhan(m_txtMamon.getText().trim(), m_txtTenmon.getText().trim(), Integer.parseInt(m_txtSotinchi.getText().trim())));
            

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                loadSinhVien();
                FillTblSinhVien(s_model);
                JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                s_clear();
            }
            conn.close();
                    pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } 
    }//GEN-LAST:event_s_btnThemActionPerformed

    private void s_btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_s_btnSuaActionPerformed
        // TODO add your handling code here:
        selectRowSV = s_tblSV.getSelectedRow();
        
        if (selectRowSV == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để cập nhật", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String gioitinh = "";
        if (jRadioButton1.isSelected()) {
            gioitinh = "Nam";
        } else {
            gioitinh = "Nữ";
        }
        
        
        if (s_txtMasv.getText().trim().isEmpty() || s_txtHoten.getText().trim().isEmpty() || s_txtNgaysinh.getText().trim().isEmpty() ||
                s_txtLop.getText().trim().isEmpty() || btnGender.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String sql = "UPDATE sinhvien SET masv = ?, hoten = ?, gioitinh = ?, ngaysinh = ?, lop = ? WHERE masv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql)) {
            
            pstm.setString(1, s_txtMasv.getText().trim());
            pstm.setString(2, s_txtHoten.getText().trim());
            pstm.setString(3, gioitinh);
            pstm.setString(4, s_txtNgaysinh.getText().trim());
            pstm.setString(5, s_txtLop.getText().trim());
            pstm.setString(6, s_txtMasv.getText().trim());
            
            int affectRow = pstm.executeUpdate();
            if (affectRow > 0) {
                //cap nhat lai jtable
                loadSinhVien();
                FillTblSinhVien(s_model);
                
                JOptionPane.showMessageDialog(this, "Cập nhật thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                s_clear();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            conn.close();
            pstm.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_s_btnSuaActionPerformed

    private void s_btnTimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_s_btnTimActionPerformed
        // TODO add your handling code here:
        String keyword = s_txtTim.getText().trim();
        searStudentInTabel(keyword);
    }//GEN-LAST:event_s_btnTimActionPerformed

    private void dk_btnDKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dk_btnDKActionPerformed
        // TODO add your handling code here:
        SinhVien sv = selectSv;
        String mamon = selectMamon;
        if (selectRowMH == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn để đăng ký", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (sv == null) {
            return;
        }
        boolean isExists = false;
        for (HocPhan existingHP : sv.getHocPhans()) {
            if (existingHP.getMaMon().equals(mamon)) {
                isExists = true;
                break;
            }
        }
        if (isExists) {
            JOptionPane.showMessageDialog(this, "Môn học đã tồn tại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            String sql = "INSERT INTO dangkyhoc (masv, mamon) VALUES (?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, sv.getMasv());
                pstmt.setString(2, mamon);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    loadMHofSV(sv);
                    FillTblMHofSV(sv.getHocPhans());
                    JOptionPane.showMessageDialog(this, "Đăng ký học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    m_clear();
                }
                conn.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi đăng ký!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } 
        }
    }//GEN-LAST:event_dk_btnDKActionPerformed

    private void dk_btnHuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dk_btnHuyActionPerformed
        // TODO add your handling code here:
        if (selectRowMH == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn hủy đăng ký", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            if (JOptionPane.showConfirmDialog(this, "Bạn có muốn hủy đăng ký môn này không?") == 0) {
        
                String sql = "DELETE FROM dangkyhoc WHERE masv = ? AND mamon = ?";

                try (Connection conn = DatabaseConnection.getConnection();
                        PreparedStatement pstm = conn.prepareStatement(sql)) {
                    pstm.setString(1, selectSv.getMasv());
                    pstm.setString(2, selectMamon);

                    int affectedRows = pstm.executeUpdate();
                    if(affectedRows > 0){
                        loadMHofSV(selectSv);
                        FillTblMHofSV(selectSv.getHocPhans());
                        JOptionPane.showMessageDialog(this, "Xóa thành công", "Cập nhật", JOptionPane.INFORMATION_MESSAGE);
                        s_clear();
                    }
                    conn.close();
                    pstm.close();

                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        
    }//GEN-LAST:event_dk_btnHuyActionPerformed

    private void d_btnXoadiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_d_btnXoadiemActionPerformed
        // TODO add your handling code here:
        if (selectRowDiem == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn điểm muốn xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            if (JOptionPane.showConfirmDialog(this, "Bạn có muốn xóa điểm của môn này không?") == 0) {
        
                String sql = "DELETE FROM diemthi WHERE masv = ? AND mamon = ?";

                try (Connection conn = DatabaseConnection.getConnection();
                        PreparedStatement pstm = conn.prepareStatement(sql)) {
                    pstm.setString(1, selectSv.getMasv());
                    pstm.setString(2, selectMamon);
                    
                    for (HocPhan hp : selectSv.getHocPhans()) {
                        if (hp.getMaMon().equals(selectMamon)) {
                            selectSv.setDiem(hp.getMaMon(), 0);
//                            selectSv.setStatus(hp, null);
                            break;
                        }
                    }

                    int affectedRows = pstm.executeUpdate();
                    if(affectedRows > 0){
                        loadDiemOfSV(selectSv);
                        FillTblDiem(selectSv);
                        JOptionPane.showMessageDialog(this, "Xóa thành công", "Cập nhật", JOptionPane.INFORMATION_MESSAGE);
                        d_clear();
                    }
                    conn.close();
                    pstm.close();

                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_d_btnXoadiemActionPerformed
    
    private void searStudentInTabel (String keyword) {
        DefaultTableModel model = (DefaultTableModel) s_tblSV.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        s_tblSV.setRowSorter(sorter);
        
        if (keyword.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword, 1, 2));
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JScrollPane JScrollPane1;
    private javax.swing.JScrollPane JScrollPane2;
    private javax.swing.JButton d_btnNhapdiem;
    private javax.swing.JButton d_btnXoadiem;
    private javax.swing.JComboBox<String> d_jCBMasv;
    private javax.swing.JTable d_tblDiemthi;
    private javax.swing.JTextField d_txtHoten;
    private javax.swing.JTextField d_txtMamon;
    private javax.swing.JTextField d_txtNhapdiem;
    private javax.swing.JTextField d_txtTenmon;
    private javax.swing.JButton dk_btnDK;
    private javax.swing.JButton dk_btnHuy;
    private javax.swing.JComboBox<String> dk_jCBMasv;
    private javax.swing.JTable dk_tableChuaDK;
    private javax.swing.JTable dk_tblDaDK;
    private javax.swing.JTextField dk_txtHoten;
    private javax.swing.JTextField dk_txtLop;
    private javax.swing.JTextField dk_txtMonhoc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton m_btnSua;
    private javax.swing.JButton m_btnThem;
    private javax.swing.JButton m_btnXoa;
    private javax.swing.JTable m_tblMonhoc;
    private javax.swing.JTextField m_txtMamon;
    private javax.swing.JTextField m_txtSotinchi;
    private javax.swing.JTextField m_txtTenmon;
    private javax.swing.JButton s_btnSua;
    private javax.swing.JButton s_btnThem;
    private javax.swing.JButton s_btnTim;
    private javax.swing.JButton s_btnXoa;
    private javax.swing.JTable s_tblSV;
    private javax.swing.JTextField s_txtHoten;
    private javax.swing.JTextField s_txtLop;
    private javax.swing.JTextField s_txtMasv;
    private javax.swing.JTextField s_txtNgaysinh;
    private javax.swing.JTextField s_txtTim;
    private javax.swing.JComboBox<String> tk_jCBMasv;
    private javax.swing.JPanel tk_panelBieuDo;
    private javax.swing.JTextField tk_txtHoten;
    private javax.swing.JTextField tk_txtLop;
    private javax.swing.JTextField tk_txtNgaysinh;
    // End of variables declaration//GEN-END:variables
}
