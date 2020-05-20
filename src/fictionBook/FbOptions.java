package fictionBook;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

public class FbOptions extends JDialog implements ActionListener {

	private JFrame frame;
	private JPanel container,  panel1,  panel2, panel3;
	//private JTabbedPane optionsTPane;
	private String lafName = "";

	//private JList themes;
	private JTable themes, iconThemes;
	private Font labelFont,  fieldFont;
	private JButton btnOk,  btnCancel;
	private JLabel themesLabel, iconThemesLabel;
	Settings se;
	public boolean returnCode = false;
	private FbTableCellRenderer tcr, itcr;
	private ListSelectionModel themeSelection, ithemeSelection;
	private systemProperties sysProp;
	private String plafName, iconThemeName;
	String icoPath;

	public FbOptions(Frame owner) throws FileNotFoundException {
		super(owner, "Options");
		setModal(true);

		sysProp = new systemProperties();

		se = new Settings("config.ini");
		
		setIconTheme(se.getIconTheme());
		setPlafName(se.getThemeName());

		icoPath = sysProp.getUserDir() + sysProp.getFileSeparator() + "lookAndFeel" + sysProp.getFileSeparator() + "IconThemes" + sysProp.getFileSeparator() + getIconTheme() + sysProp.getFileSeparator();
		
		MigLayout settingsLayout = new MigLayout("insets 7 0 0 0", "[grow, fill]", "[grow,fill]");

		frame = new JFrame();

//		optionsTPane = new JTabbedPane();
		container = new JPanel();
		container.setLayout(settingsLayout);

		MigLayout p1Layout = new MigLayout("insets 0 5 0 5", "[pref!][grow,fill][pref!]");
		panel1 = new JPanel();
		panel1.setLayout(p1Layout);

		MigLayout p2Layout = new MigLayout("insets 0 5 0 5", "[grow,fill]", "[pref!][grow,fill]");
		panel2 = new JPanel();
		panel2.setLayout(p2Layout);
		themesLabel = new JLabel("Styles");

		String[] columnNames = {"Theme", "Description", "Author"};

		ArrayList<String> SynthThemes = new ArrayList<String>();
		File[] fl = new File("lookAndFeel/Styles").listFiles();

		UIManager.LookAndFeelInfo plaf[] = UIManager.getInstalledLookAndFeels();
		int lafCount = 0;
		for (int i = 0, n = plaf.length; i < n; i++) {
			lafCount++;
		}

		if(fl.length > 0){
			for(File tmpFl : fl){
				if(tmpFl.isDirectory()){
					SynthThemes.add(tmpFl.getName());
					lafCount++;
				}
			}
		}

		String[][] data = new String[lafCount][3];

		for (int i = 0, n = plaf.length; i < n; i++) {			
			data[i][0] = plaf[i].getName();
			data[i][1] = plaf[i].getClassName();
			data[i][2] = "";
		}

		systemProperties sp = new systemProperties();

		int st = 0;
		for(int i=plaf.length;i<(SynthThemes.size()+(plaf.length));i++){
			Properties p = new Properties();
			BufferedReader input = new BufferedReader(new FileReader("lookAndFeel/Styles" + sp.getFileSeparator() + SynthThemes.get(st) + sp.getFileSeparator() + SynthThemes.get(st) + ".ini"));
			try {
				p.load(input);
			} catch (IOException ex) {
				Logger.getLogger(FbOptions.class.getName()).log(Level.SEVERE, null, ex);
			}
			data[i][0] = p.getProperty("Name");
			data[i][1] = p.getProperty("Description");
			data[i][2] = p.getProperty("Author");
		}

		themes = new JTable(data, columnNames);
		themes.setAutoscrolls(true);
		themes.setIntercellSpacing(new Dimension(0, 1));
		themes.setShowVerticalLines(false);
		themes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		tcr = new FbTableCellRenderer();
		themeSelection = themes.getSelectionModel();
		themeSelection.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
				}
			}
		});

		themes.getColumnModel().getColumn(0).setCellRenderer(tcr);
		themes.getColumnModel().getColumn(1).setCellRenderer(tcr);
		themes.getColumnModel().getColumn(2).setCellRenderer(tcr);

		themes.getColumnModel().getColumn(0).setPreferredWidth(100);
		themes.getColumnModel().getColumn(1).setPreferredWidth(300);
		themes.getColumnModel().getColumn(2).setPreferredWidth(85);

		if (UIManager.getLookAndFeel().getName().equals("Synth look and feel")) {
			themes.setSelectionBackground(new Color(184, 208, 229));
		}

		themes.setName("themes");
		TableSelectionListener tblListener = new TableSelectionListener(themes);
		themes.getSelectionModel().addListSelectionListener(tblListener);
		themes.getColumnModel().getSelectionModel().addListSelectionListener(tblListener);

		ArrayList<String> iconThemeList = new ArrayList<String>();
		File[] fll = new File("lookAndFeel/IconThemes").listFiles();
		int ithemeCount = 0;

		if(fll.length > 0){
			for(File tmpFl : fll){
				if(tmpFl.isDirectory()){
					iconThemeList.add(tmpFl.getAbsolutePath());
					ithemeCount++;					
				}
			}
		}

		

		String[][] icoData = new String[ithemeCount][2];
		for(int i=0;i<ithemeCount;i++){
			Properties p = new Properties();
			BufferedReader input = new BufferedReader(new FileReader(iconThemeList.get(i) + sp.getFileSeparator() + "Theme.ini"));
			try {
				p.load(input);
			} catch (IOException ex) {
				Logger.getLogger(FbOptions.class.getName()).log(Level.SEVERE, null, ex);
			}
			icoData[i][0] = p.getProperty("Name");
			icoData[i][1] = p.getProperty("Description");
		}

		String[] icoColumnNames = {"Theme", "Description"};

		iconThemes = new JTable(icoData, icoColumnNames);
		iconThemes.setAutoscrolls(true);
		iconThemes.setIntercellSpacing(new Dimension(0, 1));
		iconThemes.setShowVerticalLines(false);
		iconThemes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		itcr = new FbTableCellRenderer();
		ithemeSelection = iconThemes.getSelectionModel();
		ithemeSelection.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
				}
			}
		});

		iconThemes.getColumnModel().getColumn(0).setCellRenderer(tcr);
		iconThemes.getColumnModel().getColumn(1).setCellRenderer(tcr);

		iconThemes.getColumnModel().getColumn(0).setPreferredWidth(100);
		iconThemes.getColumnModel().getColumn(1).setPreferredWidth(385);

		if (UIManager.getLookAndFeel().getName().equals("Synth look and feel")) {
			iconThemes.setSelectionBackground(new Color(184, 208, 229));
		}

		TableSelectionListener itblListener = new TableSelectionListener(iconThemes);
		iconThemes.getSelectionModel().addListSelectionListener(itblListener);
		iconThemes.getColumnModel().getSelectionModel().addListSelectionListener(tblListener);
		iconThemes.setName("ithemes");

		JScrollPane themesScrollPane = new JScrollPane(themes);
		themesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		themesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JScrollPane iconThemesPane = new JScrollPane(iconThemes);
		iconThemesPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		iconThemesPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		

		for(int i=0;i<plaf.length;i++){
			if(!UIManager.getLookAndFeel().getName().equals("Synth look and feel")){
				themesScrollPane.setBorder(BorderFactory.createEtchedBorder());
				iconThemesPane.setBorder(BorderFactory.createEtchedBorder());
			} else {
				Image top_left = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/top-left-corner.png").getImage();
				Image top_center = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/top-border.png").getImage();
				Image top_right = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/top-right-corner.png").getImage();
				Image left_center = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/left-border.png").getImage();
				Image right_center = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/right-border.png").getImage();
				Image bottom_left = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/bottom-left-corner.png").getImage();
				Image bottom_center = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/bottom-border.png").getImage();
				Image bottom_right = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/bottom-right-corner.png").getImage();

				Color col = container.getBackground();

				ImageBorder imgBorder = new ImageBorder(top_left, top_center, top_right, left_center, right_center, bottom_left, bottom_center, bottom_right, col);

				themesScrollPane.setBorder(imgBorder);
				iconThemesPane.setBorder(imgBorder);
				
			}
		}
		
		panel2.add(themesLabel, "wrap");
		panel2.add(themesScrollPane, "hmax 120");

		constructSettingsTab();

		container.add(panel1, "wmin 500, wrap");

		container.add(panel2, "wrap, hmin 100");

		MigLayout p3Layout = new MigLayout("insets 0 5 0 5", "[grow,fill]", "[pref!][grow,fill]");
		panel3 = new JPanel();
		panel3.setLayout(p3Layout);
		iconThemesLabel = new JLabel("Icon Themes");
		
		panel3.add(iconThemesLabel, "wrap");
		panel3.add(iconThemesPane, "hmax 120");

		container.add(panel3, "wrap, hmin 120");

		JPanel optPanel = new JPanel();
		MigLayout optLayout = new MigLayout("insets 0 0 0 0", "[grow, right][pref!, right]");
		optPanel.setLayout(optLayout);

		Icon okGlyph = new ImageIcon(icoPath + "ok.png");
		Icon cancelGlyph = new ImageIcon(icoPath + "cancel.png");

		btnOk = new JButton("OK");
		btnOk.setIcon(okGlyph);
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				returnCode = true;
				writeConfiguration();
				hide();
			}
		});

		btnCancel = new JButton("Cancel");
		btnCancel.setIcon(cancelGlyph);
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				returnCode = false;
				hide();
			}
		});

		optPanel.add(btnOk);
		optPanel.add(btnCancel);

		container.add(optPanel);

		setLabelsFont();
		setFieldFont();

		setContentPane(container);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
	}

	public void setConfigFile(String fileName) {
		se.setIniFile(fileName);
	}

	public void readConfiguration() {

		try {
			se.readIniFile();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(FbOptions.class.getName()).log(Level.SEVERE, null, ex);
		}

		dfltDir.setText(se.getDefaultDir());
		outDir.setText(se.getOutputDir());
		tmpDir.setText(se.getTempDir());
		fntLbl.setText(se.getLabelFontAsString());
		fieldFnt.setText(se.getFieldFontAsString());
		setPlafName(se.getThemeName());
		setIconTheme(se.getIconTheme());
	}

	public void writeConfiguration() {
		se.setDefaultDir(dfltDir.getText());
		se.setOutputDir(outDir.getText());
		se.setTempDir(tmpDir.getText());
		se.setLabelFont(fntLbl.getText());
		se.setFieldFont(fieldFnt.getText());
		se.setThemeName(plafName);
		se.setIconTheme(iconThemeName);

		try {
			se.writeIniFile();
		} catch (IOException ex) {
			Logger.getLogger(FbOptions.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void setLafName(String laf) {
		lafName = laf;

	}
	
	JLabel dfltDirLbl, tmpDirLbl, outDirLbl, labelFntLbl, fieldFntLbl;
	JTextField dfltDir, tmpDir, outDir, fntLbl, fieldFnt;
	JButton dfltDirSel, tmpDirSel, outDirSel, fntLblSel, fieldFntSel;

	private void constructSettingsTab() {
		String icoPath = sysProp.getUserDir() + sysProp.getFileSeparator() + "lookAndFeel" + sysProp.getFileSeparator() + "IconThemes" + sysProp.getFileSeparator() + getIconTheme() + sysProp.getFileSeparator();
		
		dfltDirLbl = new JLabel("Default directory: ");
		dfltDir = new JTextField();

		dfltDirSel = new JButton(new ImageIcon(icoPath + "select_folder.png"));
		dfltDirSel.addActionListener(this);
		dfltDirSel.setActionCommand("selectDfltFolder");

		panel1.add(dfltDirLbl);
		panel1.add(dfltDir);
		panel1.add(dfltDirSel, "wrap");

		tmpDirLbl = new JLabel("Temp directory: ");
		tmpDirLbl.setFont(labelFont);
		tmpDir = new JTextField();

		tmpDirSel = new JButton(new ImageIcon(icoPath + "select_folder.png"));
		tmpDirSel.addActionListener(this);
		tmpDirSel.setActionCommand("selectTmpFolder");

		panel1.add(tmpDirLbl);
		panel1.add(tmpDir);
		panel1.add(tmpDirSel, "wrap");

		outDirLbl = new JLabel("Output directory: ");
		outDir = new JTextField();

		outDirSel = new JButton(new ImageIcon(icoPath + "select_folder.png"));
		outDirSel.addActionListener(this);
		outDirSel.setActionCommand("selectOutFolder");

		panel1.add(outDirLbl);
		panel1.add(outDir);
		panel1.add(outDirSel, "wrap");

		labelFntLbl = new JLabel("Label font: ");
		fntLbl = new JTextField();

		fntLblSel = new JButton(new ImageIcon(icoPath + "select_folder.png"));
		fntLblSel.addActionListener(this);
		fntLblSel.setActionCommand("selectLabelFont");

		panel1.add(labelFntLbl);
		panel1.add(fntLbl);
		panel1.add(fntLblSel, "wrap");

		fieldFntLbl = new JLabel("Field font: ");
		fieldFnt = new JTextField();

		fieldFntSel = new JButton(new ImageIcon(icoPath + "select_folder.png"));
		fieldFntSel.addActionListener(this);
		fieldFntSel.setActionCommand("selectFieldFont");

		panel1.add(fieldFntLbl);
		panel1.add(fieldFnt);
		panel1.add(fieldFntSel, "wrap");

	}

	@Override
	public void actionPerformed(ActionEvent a) {
		if (a.getActionCommand().equals("selectDfltFolder")) {
			actnSelectDefaultFolder();
		} else if (a.getActionCommand().equals("selectTmpFolder")) {
			actnSelectTempFolder();
		} else if (a.getActionCommand().equals("selectOutFolder")) {
			actnSelectOutputFolder();
		} else if (a.getActionCommand().equals("selectLabelFont")) {
			actnSelectLabelFont();
		} else if (a.getActionCommand().equals("selectFieldFont")) {
			actnSelectFieldFont();
		}
	}

	private void getDir(JTextField tField) {
		JFileChooser fc = new JFileChooser();
		JTextField tFld = tField;
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int rVal = fc.showOpenDialog(frame);

		if (rVal == JFileChooser.APPROVE_OPTION) {
			tFld.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}

	private void actnSelectDefaultFolder() {
		getDir(dfltDir);
	}

	private void actnSelectTempFolder() {
		getDir(tmpDir);
	}

	private void actnSelectOutputFolder() {
		getDir(outDir);
	}

	private void actnSelectLabelFont() {
		FontChooser fc = new FontChooser(frame);

		if(UIManager.getLookAndFeel().getName().equals("Synth look and feel")){
			Image topLeft = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/top-left-corner.png").getImage();
			Image topCenter = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/top-border.png").getImage();
			Image topRight = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/top-right-corner.png").getImage();
			Image leftCenter = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/left-border.png").getImage();
			Image rightCenter = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/right-border.png").getImage();
			Image bottomLeft = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/bottom-left-corner.png").getImage();
			Image bottomCenter = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/bottom-border.png").getImage();
			Image bottomRight = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/bottom-right-corner.png").getImage();

			Color color = panel1.getBackground();

			fc.setImageBorder(topLeft, topCenter, topRight, leftCenter, rightCenter, bottomLeft, bottomCenter, bottomRight, color);
		} else {
			fc.setBorder(BorderFactory.createEtchedBorder());
		}

		Icon okGlyph = new ImageIcon(icoPath + "ok.png");
		Icon cancelGlyph = new ImageIcon(icoPath + "cancel.png");

		fc.setOkGlyph(okGlyph);
		fc.setCancelGlyph(cancelGlyph);
		fc.show();
		if (fc.getFont() != null) {
			fntLbl.setText(fc.getFontAsString());
			labelFont = fc.getSelectedFont();
			setLabelsFont();
		}

	}

	private void actnSelectFieldFont() {
		FontChooser fc = new FontChooser(frame);

		if(UIManager.getLookAndFeel().getName().equals("Synth look and feel")){
			Image topLeft = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/top-left-corner.png").getImage();
			Image topCenter = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/top-border.png").getImage();
			Image topRight = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/top-right-corner.png").getImage();
			Image leftCenter = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/left-border.png").getImage();
			Image rightCenter = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/right-border.png").getImage();
			Image bottomLeft = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/bottom-left-corner.png").getImage();
			Image bottomCenter = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/bottom-border.png").getImage();
			Image bottomRight = new ImageIcon("lookAndFeel/Styles/" + getPlafName() + "/Tree/Border/bottom-right-corner.png").getImage();

			Color color = panel1.getBackground();
			fc.setImageBorder(topLeft, topCenter, topRight, leftCenter, rightCenter, bottomLeft, bottomCenter, bottomRight, color);
		} else {
			fc.setBorder(BorderFactory.createEtchedBorder());
		}

		Icon okGlyph = new ImageIcon(icoPath + "ok.png");
		Icon cancelGlyph = new ImageIcon(icoPath + "cancel.png");
			
		fc.setOkGlyph(okGlyph);
		fc.setCancelGlyph(cancelGlyph);
		fc.show();
		if (fc.getFont() != null) {
			fieldFnt.setText(fc.getFontAsString());
			fieldFont = fc.getSelectedFont();
			setFieldFont();
		}
	}

	private void setLabelsFont() {
		labelFont = se.getLabelFont();

		dfltDirLbl.setFont(labelFont);
		tmpDirLbl.setFont(labelFont);
		outDirLbl.setFont(labelFont);
		labelFntLbl.setFont(labelFont);
		fieldFntLbl.setFont(labelFont);
		themesLabel.setFont(labelFont);
		iconThemesLabel.setFont(labelFont);

		btnOk.setFont(labelFont);
		btnCancel.setFont(labelFont);

		JTableHeader th = themes.getTableHeader();
		th.setFont(labelFont);
	}

	private void setFieldFont() {
		fieldFont = se.getFieldFont();
		dfltDir.setFont(fieldFont);
		tmpDir.setFont(fieldFont);
		outDir.setFont(fieldFont);
		fntLbl.setFont(fieldFont);
		fieldFnt.setFont(fieldFont);
		tcr.setFont(fieldFont);
	}

	public Font getLabelFont() {
		return labelFont;
	}

	public Font getFieldFont() {
		return fieldFont;
	}

	public class FbTableCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setFont(fieldFont);
			return c;
		}

		@Override
		public void setFont(Font f) {
			super.setFont(f);
		}
	}

  public String getDefaultDir(){
    return dfltDir.getText();
  }

  public String getOutDir(){
    return outDir.getText();
  }

  public String getTmpDir(){
    return tmpDir.getText();
  }

	public void setPlafName(String name){
		plafName = name;
	}

	public String getPlafName(){
		return plafName;
	}

	private class TableSelectionListener implements ListSelectionListener {

		JTable table;

		public TableSelectionListener(JTable table) {
			this.table = table;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()) {
				int row = table.getSelectedRow();
				TableModel tm = table.getModel();

				if(table.getName().equals("themes")){
					setPlafName((String)tm.getValueAt(row, 0));
				} else if(table.getName().equals("ithemes")){
					setIconTheme((String)tm.getValueAt(row, 0));
				}
			} else if (e.getSource() == table.getColumnModel().getSelectionModel() && table.getColumnSelectionAllowed()) {
				System.err.println("Not implemented yet");
			}

			if (e.getValueIsAdjusting()) {
			}
		}
	}

	public void setIconTheme(String theme){iconThemeName = theme;}
	public String getIconTheme(){return iconThemeName;}
}

