package fictionBook;

import com.arashpayan.filetree.FileTree;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.tree.DefaultTreeCellRenderer;
import net.miginfocom.swing.MigLayout;
import sun.awt.VerticalBagLayout;

public class Fb2BatchConvGui implements ActionListener, DocumentListener {

	//@todo Add tar, tar.gz, tar.bz2, rar support (in version 2)
	//@todo Fix all the missing futures in the plaf
	//@todo Add JavaDoc comments to the methods and classes
	//@todo Add image support to the FB2 format
	//@todo Add zip compression for converted files (*.fb2 and images)
	private FbFrame frame;
	private JToolBar toolBar;

	//Font definitions
	//@todo Add custom font settings
	private Font fieldFont;
	private Font labelFont;

	//Java Trees
	private FileTree fileTree;

	//Panels
	private JPanel appPanel,  mainPanel,  bookInfoPanel,  bookPanel,  filePanel,  mBookInfo,  authorInfo,  titleInfo,  transInfo,  seriesInfo,  fb2Panel;

	//Scroll Panels
	private JScrollPane fileTreeScrollPanel,  fileListScrollPanel;

	//MigLayouts
	MigLayout bookPanelLayout, fileTreeLayout, bookInfoLayout, bookInfoScrollLayout, filePanelLayout, mainPanelLayout;

	//Labels
	private JLabel genereLabel,  langLabel,  oriLangLabel,  authNameLabel,  authSurnameLabel,  authCognomenLabel,  authNicLabel,  authEmailLabel,  authSiteLabel,  bookTitleLabel,  bookKeywordsLabel,  transNameLabel,  transSurnameLabel,  transCognomenLabel,  transNicLabel,  transEmailLabel,  transSiteLabel,  seriesNameLabel,  seriesNumLabel,  bookTitle,  bookISBN,  bookSeries,  bookSeriesNumber;

	//ComboBoxes
	private JComboBox genereCombo,  langCombo,  oriLangCombo;

	//Text fields
	private JTextField authNameTBox,  authSurnameTBox,  authCognomenTBox,  authNicTBox,  authEmailTBox,  authSiteTBox,  bookTitleTBox,  bookKeywordsTBox,  transNameTBox,  transSurnameTBox,  transCognomenTBox,  transNicTBox,  transEmailTBox,  transSiteTBox,  seriesNameTBox,  seriesNumTBox,  bookTitleText,  bookISBNText,  bookSeriesText,  bookSeriesNumberText;
	private JList fileList;

	//List models
	private DefaultListModel fileListModel,  absPathFileListModel;
	//@todo Convert DefaultListModel(s) to Set (in version 2)
	private ArrayList<Sfb> bookList;
	//@todo Convert ArrayList<Sfb> to Set (in version 2)
	private ListSelectionModel lsModel;
	private boolean cGenereCombo,  cLangCombo,  cOriLangCombo,  cAuthNameTBox,  cAuthSurnameTBox,  cAuthCognomenTBox,  cAuthNicTBox,  cAuthEmailTBox,  cAuthSiteTBox,  cBookTitleTBox,  cBookKeywordsTBox,  cTransNameTBox,  cTransSurnameTBox,  cTransCognomenTBox,  cTransNicTBox,  cTransEmailTBox,  cTransSiteTBox,  cSeriesNameTBox,  cSeriesNumTBox,  cBookTitleText,  cBookISBNText,  cBookSeriesText,  cBookSeriesNumberText;
	private ArrayList<Integer> selectIndex;
	private ArrayList<Integer> oldSelectIndex;
	private ArrayList<String> langList,  genereList;
	private systemProperties sysProp;
	private String bookEnc, srcDir, tempDir, outputDir;
	private static String lafName;
	private String iconTheme;

	private boolean isSynth = false;

	private Settings se;

	private void setLafName(String name) {
		lafName = name;
	}

	private void setIconTheme(String name){
		iconTheme = name;
	}

	/**
	 * The initialisation funciton for the Synth Look And Feel
	 * 
	 * @throws javax.swing.UnsupportedLookAndFeelException
	 * @throws java.io.IOException
	 */
	private static void initSynthLookAndFeel() throws UnsupportedLookAndFeelException, IOException {
		SynthLookAndFeel laf = new SynthLookAndFeel();

		try {
			systemProperties sp = new systemProperties();
			String lafFile = sp.getUserDir() + sp.getFileSeparator() + "lookAndFeel" + sp.getFileSeparator() + "Styles" + sp.getFileSeparator() + lafName + sp.getFileSeparator() + "Oxygen.xml";
			URL ur = new URL("file:///" + lafFile);
			laf.load(ur);
			UIManager.setLookAndFeel(laf);

		} catch (ParseException e) {
			System.err.println("Couldn't get specified look and feel (" + laf + "), for some reason.");
			System.err.println("Using the default look and feel.");
			e.printStackTrace();
		}
	}

	private static void initSynthLookAndFeel(String synthTheme, String icoTheme) throws UnsupportedLookAndFeelException, IOException{
		SynthLookAndFeel laf = new SynthLookAndFeel();
		try{
			systemProperties sp = new systemProperties();
			String lafFile = sp.getUserDir() + sp.getFileSeparator() + "lookAndFeel" + sp.getFileSeparator() + "Styles" + sp.getFileSeparator() + synthTheme + sp.getFileSeparator() + synthTheme + ".xml";
			URL ur = new URL("file:///" + lafFile);
			laf.load(ur);
			UIManager.setLookAndFeel(laf);
		} catch (ParseException e){
			System.err.println("Couldn't get specified look and feel (" + synthTheme + "), for some reason.");
			System.err.println("Using the default look and feel.");
			e.printStackTrace();
		}
	}

	private static void initSynthLookAndFeel(String synthTheme) throws UnsupportedLookAndFeelException, IOException{
		SynthLookAndFeel laf = new SynthLookAndFeel();
		try{
			systemProperties sp = new systemProperties();
			String lafFile = sp.getUserDir() + sp.getFileSeparator() + "lookAndFeel" + sp.getFileSeparator() + "Styles" + sp.getFileSeparator() + synthTheme + sp.getFileSeparator() + synthTheme + ".xml";
			URL ur = new URL("file:///" + lafFile);
			laf.load(ur);
			UIManager.setLookAndFeel(laf);
		} catch (ParseException e){
			System.err.println("Couldn't get specified look and feel (" + synthTheme + "), for some reason.");
			System.err.println("Using the default look and feel.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructor for the converter gui
	 * 
	 * @throws java.io.IOException
	 */
	public Fb2BatchConvGui() throws IOException {
		sysProp = new systemProperties();
		bookEnc = "";

		setLafName("Oxygen");
		try {
			initSynthLookAndFeel();
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
		}

		bookList = new ArrayList<Sfb>();
		selectIndex = new ArrayList<Integer>();
		oldSelectIndex = new ArrayList<Integer>();

		bookPanelLayout = new MigLayout("wrap 2", "[grow 30][grow 70]", "[grow]");
		bookPanel = new JPanel(bookPanelLayout);
		bookPanel.setBorder(null);

		absPathFileListModel = new DefaultListModel();

		appPanel = new JPanel(new VerticalBagLayout());
		mainPanelLayout = new MigLayout("wrap 2", "[grow]");
		mainPanel = new JPanel(mainPanelLayout);

		filePanelLayout = new MigLayout("debug", "[grow][grow]", "[grow][grow]");
		filePanel = new JPanel(filePanelLayout);

		frame = new FbFrame("SFB to FB2 Convertor", new Dimension(300, 200), new Dimension(1024, 685));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		if(new File("./config.ini").exists()){
			se = new Settings("config.ini");
			if(!se.getIconTheme().equals("")){setIconTheme(se.getIconTheme());} else {setIconTheme("Oxygen");}
		}

		createToolbar();

		createFileTree();

		try {
			createBookInfoPanel();
		} catch (IOException ex) {
			System.out.println(ex.getCause());
			System.out.println(ex.getClass());
			System.out.println(ex.getMessage());
			System.out.println(ex.getStackTrace());
		}

		langList = new ArrayList<String>(loadItemsFromFileArl("./lists/languageCodes.lst"));
		genereList = new ArrayList<String>(loadItemsFromFileArl("./lists/fbGenereCodes.lst"));

		filePanel.add(fileTreeScrollPanel, "growx, wrap");
		filePanel.add(fileListScrollPanel, "grow");
		filePanel.setBorder(null);

		bookPanel.add(filePanel, "grow, wmin 350, wmax 350");
		bookPanel.add(bookInfoPanel, "grow");
		bookPanel.setBorder(null);

		toolBar.setBorder(null);

		mainPanel.add(toolBar, "spanx 2");
		mainPanel.add(fileTreeScrollPanel, "spany 3, growx, hmax 290");
		mainPanel.add(mBookInfo, "growx 70");
		mainPanel.add(authorInfo, "growx 70");
		mainPanel.add(titleInfo, "growx 70, wrap");
		mainPanel.add(fileListScrollPanel, "spany 3, growx, growy");
		mainPanel.add(transInfo, "growx 70");
		mainPanel.add(seriesInfo, "growx 70");
		mainPanel.add(fb2Panel, "growx 70");

		mBookInfo.setBorder(null);
		mainPanel.setBorder(null);
		appPanel.setBorder(null);

		appPanel.add(toolBar);
		appPanel.add(mainPanel);

		frame.setContentPane(appPanel);

		//List selection model and listener configurarion.
		//Needed for determining the selected file from the fileList component.

		lsModel = fileList.getSelectionModel();
		lsModel.addListSelectionListener(new lsHandler());

    //Read the configuration, stored in the config.ini file.
		if(new File("./config.ini").exists()){
			
			setLabelFont(se.getLabelFont());
			setFieldFont(se.getFieldFont());
      if(!se.getDefaultDir().equals("")){srcDir = se.getDefaultDir();} else {srcDir = "";}
      if(!se.getTempDir().equals("")){tempDir = se.getTempDir();} else {tempDir = "";}
      if(!se.getOutputDir().equals("")){outputDir = se.getOutputDir();} else {outputDir = "";}
			
      if(new File(srcDir).exists()){
        fileTree.setCurrentFile(new File(srcDir));
      } else {
        fileTree.setCurrentFile(new File(sysProp.getUserHome()));
      }

			
			try {
				setLafName(se.getThemeName());
				setLookAndFeel(se.getThemeName());
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			} catch (UnsupportedLookAndFeelException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			}
		}		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		fieldChanged(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		fieldChanged(e);
	}

	/**
	 * Check fields for changes. If a field is changed, then a boolean
	 * flag is set to true;
	 * 
	 * @param e The document event
	 */
	private void fieldChanged(DocumentEvent e) {
		if (e.getDocument() == authNameTBox.getDocument()) {
			cAuthNameTBox = true;
		} else if (e.getDocument() == authSurnameTBox.getDocument()) {
			cAuthSurnameTBox = true;
		} else if (e.getDocument() == authCognomenTBox.getDocument()) {
			cAuthCognomenTBox = true;
		} else if (e.getDocument() == authNicTBox.getDocument()) {
			cAuthNicTBox = true;
		} else if (e.getDocument() == authEmailTBox.getDocument()) {
			cAuthEmailTBox = true;
		} else if (e.getDocument() == authSiteTBox.getDocument()) {
			cAuthSiteTBox = true;
		} else if (e.getDocument() == bookTitleTBox.getDocument()) {
			cBookTitleTBox = true;
		} else if (e.getDocument() == bookKeywordsTBox.getDocument()) {
			cBookKeywordsTBox = true;
		} else if (e.getDocument() == transNameTBox.getDocument()) {
			cTransNameTBox = true;
		} else if (e.getDocument() == transSurnameTBox.getDocument()) {
			cTransSurnameTBox = true;
		} else if (e.getDocument() == transCognomenTBox.getDocument()) {
			cTransCognomenTBox = true;
		} else if (e.getDocument() == transNicTBox.getDocument()) {
			cTransNicTBox = true;
		} else if (e.getDocument() == transEmailTBox.getDocument()) {
			cTransEmailTBox = true;
		} else if (e.getDocument() == transSiteTBox.getDocument()) {
			cTransSiteTBox = true;
		} else if (e.getDocument() == seriesNameTBox.getDocument()) {
			cSeriesNameTBox = true;
		} else if (e.getDocument() == seriesNumTBox.getDocument()) {
			cSeriesNumTBox = true;
		} else if (e.getDocument() == bookTitleText.getDocument()) {
			cBookTitleTBox = true;
		} else if (e.getDocument() == bookISBNText.getDocument()) {
			cBookISBNText = true;
		} else if (e.getDocument() == bookSeriesText.getDocument()) {
			cBookSeriesText = true;
		} else if (e.getDocument() == bookSeriesNumberText.getDocument()) {
			cBookSeriesNumberText = true;
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private class lsHandler implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();

			if (!lsm.isSelectionEmpty()) {
				for (int i = lsm.getMinSelectionIndex(); i <= lsm.getMaxSelectionIndex(); i++) {
					if (lsm.isSelectedIndex(i)) {
						selectIndex.add(i);
					}
				}

				if (selectIndex.size() == 1) {


					bookTitleTBox.setEnabled(true);
					seriesNumTBox.setEnabled(true);
					bookTitleText.setEnabled(true);
					bookISBNText.setEnabled(true);
					bookSeriesNumberText.setEnabled(true);

					if (oldSelectIndex.size() == 1) {
						setBookInfo(oldSelectIndex.get(0));
					} else {
						for (int k : oldSelectIndex) {
							setMultiBookInfo(k);
						}
						resetFieldStatus();
					}

					emptyDataFields();
					displayBookInfo(selectIndex.get(0));

					oldSelectIndex.clear();
					oldSelectIndex.addAll(selectIndex);

					selectIndex.clear();
				} else if (selectIndex.size() > 1) {
					emptyDataFields();

					bookTitleTBox.setEnabled(false);
					seriesNumTBox.setEnabled(false);
					bookTitleText.setEnabled(false);
					bookISBNText.setEnabled(false);
					bookSeriesNumberText.setEnabled(false);

					if (oldSelectIndex.size() == 1) {
						resetFieldStatus();
						setBookInfo(oldSelectIndex.get(0));
					} else {
						for (int k : oldSelectIndex) {
							setMultiBookInfo(k);
						}
						resetFieldStatus();
					}

					oldSelectIndex.clear();
					oldSelectIndex.addAll(selectIndex);

					selectIndex.clear();
				}
			}
			resetFieldStatus();
		}
	}

	/**
	 * Checks what fields where modified and sends the modified values to the
	 * coresponding item in the bookList, without reseting the status of the
	 * field flags
	 *
	 * @param idx Index of the element in the list
	 */
	private void setMultiBookInfo(int idx) {
		if (cGenereCombo) {
			bookList.get(idx).genere = genereList.get(genereCombo.getSelectedIndex());
		}
		if (cLangCombo) {
			bookList.get(idx).bookLanguage = langList.get(langCombo.getSelectedIndex());
		}
		if (cOriLangCombo) {
			bookList.get(idx).bookOriginalLanguage = langList.get(oriLangCombo.getSelectedIndex());
		}

		if (cAuthNameTBox) {
			bookList.get(idx).authorFName = authNameTBox.getText();
		}
		if (cAuthSurnameTBox) {
			bookList.get(idx).authorMName = authSurnameTBox.getText();
		}
		if (cAuthCognomenTBox) {
			bookList.get(idx).authorLName = authCognomenTBox.getText();
		}
		if (cAuthNicTBox) {
			bookList.get(idx).authNName = authNicTBox.getText();
		}
		if (cAuthEmailTBox) {
			bookList.get(idx).authEmail = authEmailTBox.getText();
		}
		if (cAuthSiteTBox) {
			bookList.get(idx).authHomePage = authSiteTBox.getText();
		}

		if (cBookKeywordsTBox) {
			bookList.get(idx).bookKeywords = bookKeywordsTBox.getText();
		}

		if (cTransNameTBox) {
			bookList.get(idx).translatorName = transNameTBox.getText();
		}
		if (cTransSurnameTBox) {
			bookList.get(idx).translatorSurname = transSurnameTBox.getText();
		}
		if (cTransCognomenTBox) {
			bookList.get(idx).translatorCognomen = transCognomenTBox.getText();
		}
		if (cTransNicTBox) {
			bookList.get(idx).translatorNickname = transNicTBox.getText();
		}
		if (cTransEmailTBox) {
			bookList.get(idx).translatorEmail = transEmailTBox.getText();
		}
		if (cTransSiteTBox) {
			bookList.get(idx).translatorHomepage = transSiteTBox.getText();
		}

		if (cSeriesNameTBox) {
			bookList.get(idx).series = seriesNameTBox.getText();
		}

		if (cBookSeriesNumberText) {
			bookList.get(idx).publishSeries = bookSeriesText.getText();
		}
	}

	/**
	 * Cleanup of the fields in the GUI
	 */
	private void emptyDataFields() {
		genereCombo.setSelectedIndex(0);
		langCombo.setSelectedIndex(0);
		oriLangCombo.setSelectedIndex(0);

		authNameTBox.setText(null);
		authSurnameTBox.setText(null);
		authCognomenTBox.setText(null);
		authNicTBox.setText(null);
		authEmailTBox.setText(null);
		authSiteTBox.setText(null);

		bookTitleTBox.setText(null);
		bookKeywordsTBox.setText(null);

		transNameTBox.setText(null);
		transSurnameTBox.setText(null);
		transCognomenTBox.setText(null);
		transNicTBox.setText(null);
		transEmailTBox.setText(null);
		transSiteTBox.setText(null);

		seriesNameTBox.setText(null);
		seriesNumTBox.setText(null);

		bookTitleText.setText(null);
		bookISBNText.setText(null);
		bookSeriesText.setText(null);
		bookSeriesNumberText.setText(null);
	}

	/**
	 * Checks what fields where modified and sends the modified values to the
	 * coresponding item in the bookList
	 * 
	 * @param idx The index of the element in the list
	 */
	private void setBookInfo(int idx) {
		if (cGenereCombo) {
			bookList.get(idx).genere = genereList.get(genereCombo.getSelectedIndex());
		}
		if (cLangCombo) {
			bookList.get(idx).bookLanguage = langList.get(langCombo.getSelectedIndex());
		}
		if (cOriLangCombo) {
			bookList.get(idx).bookOriginalLanguage = langList.get(oriLangCombo.getSelectedIndex());
		}

		if (cAuthNameTBox) {
			bookList.get(idx).authorFName = authNameTBox.getText();
		}
		if (cAuthSurnameTBox) {
			bookList.get(idx).authorMName = authSurnameTBox.getText();
		}
		if (cAuthCognomenTBox) {
			bookList.get(idx).authorLName = authCognomenTBox.getText();
		}
		if (cAuthNicTBox) {
			bookList.get(idx).authNName = authNicTBox.getText();
		}
		if (cAuthEmailTBox) {
			bookList.get(idx).authEmail = authEmailTBox.getText();
		}
		if (cAuthSiteTBox) {
			bookList.get(idx).authHomePage = authSiteTBox.getText();
		}

		if (cBookTitleTBox) {
			bookList.get(idx).bookTitle = bookTitleTBox.getText();
		}
		if (cBookKeywordsTBox) {
			bookList.get(idx).bookKeywords = bookKeywordsTBox.getText();
		}

		if (cTransNameTBox) {
			bookList.get(idx).translatorName = transNameTBox.getText();
		}
		if (cTransSurnameTBox) {
			bookList.get(idx).translatorSurname = transSurnameTBox.getText();
		}
		if (cTransCognomenTBox) {
			bookList.get(idx).translatorCognomen = transCognomenTBox.getText();
		}
		if (cTransNicTBox) {
			bookList.get(idx).translatorNickname = transNicTBox.getText();
		}
		if (cTransEmailTBox) {
			bookList.get(idx).translatorEmail = transEmailTBox.getText();
		}
		if (cTransSiteTBox) {
			bookList.get(idx).translatorHomepage = transSiteTBox.getText();
		}

		if (cSeriesNameTBox) {
			bookList.get(idx).series = seriesNameTBox.getText();
		}
		if (cSeriesNumTBox) {
			bookList.get(idx).seriesNo = seriesNumTBox.getText();
		}

		if (cBookTitleText) {
			bookList.get(idx).publishBookTitle = bookTitleText.getText();
		}
		if (cBookISBNText) {
			bookList.get(idx).publishISBN = bookISBNText.getText();
		}
		if (cBookSeriesText) {
			bookList.get(idx).publishSeries = bookSeriesText.getText();
		}
		if (cBookSeriesNumberText) {
			bookList.get(idx).publishSeriesNo = bookSeriesNumberText.getText();
		}

		resetFieldStatus();
	}

	/**
	 * Resets the fields flags status
	 * when the selection in the fileList is changed
	 */
	private void resetFieldStatus() {
		cGenereCombo = false;
		cLangCombo = false;
		cOriLangCombo = false;

		cAuthNameTBox = false;
		cAuthSurnameTBox = false;
		cAuthCognomenTBox = false;
		cAuthNicTBox = false;
		cAuthEmailTBox = false;
		cAuthSiteTBox = false;

		cBookTitleTBox = false;
		cBookKeywordsTBox = false;

		cTransNameTBox = false;
		cTransSurnameTBox = false;
		cTransCognomenTBox = false;
		cTransNicTBox = false;
		cTransEmailTBox = false;
		cTransSiteTBox = false;

		cSeriesNameTBox = false;
		cSeriesNumTBox = false;

		cBookTitleText = false;
		cBookISBNText = false;
		cBookSeriesText = false;
		cBookSeriesNumberText = false;
	}

	/**
	 * Reads the book information from the bookList, selecting only one book by
	 * index in the list. The information is displayed in the GUI
	 *
	 * @param idx Index of the book in the list
	 */
	private void displayBookInfo(int idx) {
		Sfb book = bookList.get(idx);

		if (!book.genere.equals("")) {
			genereCombo.setSelectedIndex(genereList.indexOf(book.genere));
		}
		if (!book.bookLanguage.equals("")) {
			langCombo.setSelectedIndex(langList.indexOf(book.bookLanguage));
		}
		if (!book.bookOriginalLanguage.equals("")) {
			oriLangCombo.setSelectedIndex(langList.indexOf(book.bookOriginalLanguage));
		}

		authNameTBox.setText(book.authorFName);
		authSurnameTBox.setText(book.authorMName);
		authCognomenTBox.setText(book.authorLName);
		authNicTBox.setText(book.authNName);
		authEmailTBox.setText(book.authEmail);
		authSiteTBox.setText(book.authHomePage);

		bookTitleTBox.setText(book.bookTitle);
		bookKeywordsTBox.setText(book.bookKeywords);

		transNameTBox.setText(book.translatorName);
		transSurnameTBox.setText(book.translatorSurname);
		transCognomenTBox.setText(book.translatorCognomen);
		transNicTBox.setText(book.translatorNickname);
		transEmailTBox.setText(book.translatorEmail);
		transSiteTBox.setText(book.translatorHomepage);

		seriesNameTBox.setText(book.series);
		seriesNumTBox.setText(book.seriesNo);

		bookTitleText.setText(book.publishBookTitle);
		bookISBNText.setText(book.publishISBN);
		bookSeriesText.setText(book.publishSeries);
		bookSeriesNumberText.setText(book.publishSeriesNo);
	}

	/**
	 * Reads items for lists from a specified text file. This is the Array
	 * modification of this function. It differs form the ArrayList modification
	 * by the type of the result it returns.
	 * 
	 * @param fileName The name of the file
	 * @return All of the items
	 * @throws java.io.IOException
	 */
	private String[] loadItemsFromFile(String fileName) throws IOException {
		ArrayList<String> items = new ArrayList<String>();
		RegExp rexp = new RegExp();

		BufferedReader input = new BufferedReader(new FileReader(fileName));
		String line = null;
		items.add("");
		while ((line = input.readLine()) != null) {
			if (line.length() > 0 && !rexp.regExMatch("^\\s+$", line)) {
				items.add(line);
			}
		}
		input.close();
		return items.toArray(new String[items.size()]);
	}

	/**
	 * Reads items for lists from a specified text file. This is the ArrayList
	 * modification of this function. It differs form the Array modification
	 * by the type of the result it returns.
	 *
	 * @param fileName The name of the file
	 * @return All of the items
	 * @throws java.io.IOException
	 */
	private ArrayList<String> loadItemsFromFileArl(String fileName) throws IOException {
		ArrayList<String> items = new ArrayList<String>();
		RegExp rexp = new RegExp();

		BufferedReader input = new BufferedReader(new FileReader(fileName));
		String line = null;
		items.add("");
		while ((line = input.readLine()) != null) {
			if (line.length() > 0 && !rexp.regExMatch("^\\s+$", line)) {
				items.add(line);
			}
		}
		input.close();
		return items;
	}

	JButton addFile, removeFile, clearList, convertSel, convert, settings, exit;

	/**
	 * Creates the toolbar, containing all the buttons. Each button has icon and
	 * text label. To each button there is a action command assigned, so
	 * when the button is pressed the action listener can determine which button
	 * is pressed and what is the coresponding action, that should be executed
	 */
	private void createToolbar() {
		String icoPath = sysProp.getUserDir() + sysProp.getFileSeparator() + "lookAndFeel" + sysProp.getFileSeparator() + "IconThemes" + sysProp.getFileSeparator() + iconTheme + sysProp.getFileSeparator();

		toolBar = new JToolBar();
		toolBar.setBorder(null);
		toolBar.setFloatable(false);

		addFile = new JButton("Add to list", new ImageIcon(icoPath + sysProp.getFileSeparator() + "add.png"));
		addFile.setFont(labelFont);
		addFile.addActionListener(this);
		addFile.setActionCommand("addFile");

		removeFile = new JButton("Remove from list", new ImageIcon(icoPath + sysProp.getFileSeparator() + "remove.png"));
		removeFile.setFont(labelFont);
		removeFile.addActionListener(this);
		removeFile.setActionCommand("removeFile");

		clearList = new JButton("Clear selection", new ImageIcon(icoPath + sysProp.getFileSeparator() + "clear.png"));
		clearList.setFont(labelFont);
		clearList.addActionListener(this);
		clearList.setActionCommand("clearList");

		convertSel = new JButton("Convert selected", new ImageIcon(icoPath + sysProp.getFileSeparator() + "convert.png"));
		convertSel.setFont(labelFont);
		convertSel.addActionListener(this);
		convertSel.setActionCommand("convertSel");

		convert = new JButton("Convert all", new ImageIcon(icoPath + sysProp.getFileSeparator() + "convert.png"));
		convert.setFont(labelFont);
		convert.addActionListener(this);
		convert.setActionCommand("convert");

		settings = new JButton("Settings", new ImageIcon(icoPath + sysProp.getFileSeparator() + "settings.png"));
		settings.setFont(labelFont);
		settings.addActionListener(this);
		settings.setActionCommand("settings");

		exit = new JButton("Exit", new ImageIcon(icoPath + sysProp.getFileSeparator() + "exit.png"));
		exit.setFont(labelFont);
		exit.addActionListener(this);
		exit.setActionCommand("exit");

		toolBar.add(addFile);
		toolBar.add(removeFile);
		toolBar.add(clearList);
		toolBar.add(convertSel);
		toolBar.add(convert);
		toolBar.add(settings);
		toolBar.add(exit);

	}

	/**
	 * Determines which action is executed when a button is pressed, based on
	 * the action names assigned to the buttons.
	 *
	 * @param a The Action event
	 */
	@Override
	public void actionPerformed(ActionEvent a) {
		if (a.getActionCommand().equals("addFile")) {
			actnAddFile();
		} else if (a.getActionCommand().equals("removeFile")) {
			actnRemoveFile();
		} else if (a.getActionCommand().equals("clearList")) {
			actnClearList();
		} else if (a.getActionCommand().equals("convert")) {
			actnConvert();
		} else if (a.getActionCommand().equals("convertSel")) {
			actnConvertSelected();
		} else if (a.getActionCommand().equals("settings")) {
			try {
				actnChangeSettings();
			} catch (FileNotFoundException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else if (a.getActionCommand().equals("exit")) {
			actnExit();
		}
	}

	/**
	 * Calls the settings dialog and parses the changes after its closing.
	 */
  @SuppressWarnings("deprecation")
	private void actnChangeSettings() throws FileNotFoundException {
    FbOptions fo = new FbOptions(frame);
		fo.setLafName("Oxygen");
		fo.setConfigFile("config.ini");

		if(new File("config.ini").exists()){
			fo.readConfiguration();
			
		}

		fo.show();
		
		if (fo.returnCode) {
			setLabelFont(fo.getLabelFont());
			setFieldFont(fo.getFieldFont());
			try {
				setLookAndFeel(fo.getPlafName());
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			} catch (UnsupportedLookAndFeelException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
			}

			setIconTheme(fo.getIconTheme());
			updateIconTheme();

			if(!fo.getDefaultDir().equals("")){srcDir = fo.getDefaultDir();} else {srcDir = "";}
			if(!fo.getTmpDir().equals("")){tempDir = fo.getTmpDir();} else {tempDir = "";}
			if(!fo.getOutDir().equals("")){outputDir = fo.getOutDir();} else {outputDir = "";}
		}    
	}

	/**
	 * Reflects the change in the settings dialog, regarding the icon theme
	 */
	private void updateIconTheme(){
		String icoPath = sysProp.getUserDir() + sysProp.getFileSeparator() + "lookAndFeel" + sysProp.getFileSeparator() + "IconThemes" + sysProp.getFileSeparator() + iconTheme + sysProp.getFileSeparator();
		addFile.setIcon(new ImageIcon(icoPath + sysProp.getFileSeparator() + "add.png"));
		removeFile.setIcon(new ImageIcon(icoPath + sysProp.getFileSeparator() + "remove.png"));
		clearList.setIcon(new ImageIcon(icoPath + sysProp.getFileSeparator() + "clear.png"));
		convertSel.setIcon(new ImageIcon(icoPath + sysProp.getFileSeparator() + "convert.png"));
		convert.setIcon(new ImageIcon(icoPath + sysProp.getFileSeparator() + "convert.png"));
		settings.setIcon(new ImageIcon(icoPath + sysProp.getFileSeparator() + "settings.png"));
		exit.setIcon(new ImageIcon(icoPath + sysProp.getFileSeparator() + "exit.png"));

		ImageIcon leafIcon = new ImageIcon(icoPath + "treeLeaf.png");
		ImageIcon openIcon = new ImageIcon(icoPath + "treeOpen.png");
		ImageIcon closeIcon = new ImageIcon(icoPath + "treeClosed.png");
		
		DefaultTreeCellRenderer rdr = new DefaultTreeCellRenderer();
		rdr.setLeafIcon(leafIcon);
		rdr.setOpenIcon(openIcon);
		rdr.setClosedIcon(closeIcon);

		fileTree.setCellRenderer(rdr);
	}

	private void setLookAndFeel(String lookAndFeel) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException{
    UIManager.LookAndFeelInfo plaf[] = UIManager.getInstalledLookAndFeels();
		int themeFlag = 0;
		JPanel borderPrototype = new JPanel();
		
		//Check if native theme is selected.
		for (int i=0; i<plaf.length; i++) {
			if(plaf[i].getName().equals(lookAndFeel) && themeFlag == 0){
				UIManager.setLookAndFeel(plaf[i].getClassName());
				borderPrototype.setBorder(BorderFactory.createEtchedBorder());
				themeFlag = 1;
				isSynth = false;
			} else if(i == (plaf.length-1) && themeFlag == 0){
				setLafName(lookAndFeel);
				initSynthLookAndFeel(lookAndFeel);
				borderPrototype.setName("titled");
				isSynth = true;
				themeFlag = 1;
			}
		}

		authorInfo.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Author Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));
		titleInfo.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Book Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));
		transInfo.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Translator Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));
		seriesInfo.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Series Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));
		fb2Panel.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Publish Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));

		SwingUtilities.updateComponentTreeUI(frame);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	/**
	 * Action, executed when the "Convert selected" button is pressed.
	 */
	private void actnConvertSelected() {
		if (fileList.getSelectedIndices().length > 0) {
			new sfbConvert(fileList.getSelectedIndices());
		}
	}

	/**
	 * Action, executed when the "Add file" button is pressed.
	 */
	private void actnAddFile() {
		if (fileTree.getSelectionCount() > 0) {
			readSelection();
		}
	}

	/**
	 * <p>Reads what is selected in the FileTree component. Performs
	 * different actions, depending on the selected file type.</p>
	 *
	 * <p>If the selected item is a directory, then gets the list of
	 * the contents of that directory and then procedes with the
	 * content separation and processing, depending of the file type.</p>
	 *
	 * <p>If the slected item is a zip file, unzip the file and add the text
	 * files from the zip file to the list.</p>
	 *
	 * <p>If the selected item is a text file, add them to the list.</p>
	 *
	 * <p>The list consists of two tree elements - a JList, a DefaultListModel and
	 * an ArrayList of custom type Sfb, containing the text file contents.</p>
	 *
	 * <p>This function hadles multiple selected items as well as single selected items</p>
	 */
	private void readSelection() {
		ArrayList<File> dirList = null;
		ArrayList<File> zipList = new ArrayList<File>();

		//Case when there is only one item selected
		if (fileTree.getSelectedFiles().length == 1) {
			if (fileTree.getSelectedFile().isDirectory()) {
				//The selected item is a directory
				dirList = dirLst(fileTree.getSelectedFile());
				for (File fl : dirList) {

					if (fileType(fl.getAbsolutePath()).equals("txt")) {
						//The item from the directory is a text file
						Sfb book = null;
						try {
							book = new Sfb(fl.getAbsolutePath(), "", bookEnc);
						} catch (FileNotFoundException ex) {
							Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IOException ex) {
							Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
						}
						bookList.add(book);
						fileListModel.addElement(fl.getName());
					} else if (fileType(fl.getAbsolutePath()).equals("zip")) {
						//The item from the directory is zip file
						zipList.add(fl);
					}
				}
			} else if (fileType(fileTree.getSelectedFile().getAbsolutePath()).equals("zip")) {
				//The selected item is zip file
				new FbUnzip(fileTree.getSelectedFile().getAbsolutePath(), tempDir, outputDir);
			} else if (fileType(fileTree.getSelectedFile().getAbsolutePath()).equals("txt")) {
				//The selected item is a text file
				fileListModel.addElement(fileTree.getSelectedFile().getName());
				absPathFileListModel.addElement(fileTree.getSelectedFile().getAbsolutePath());
			}
		} else {
			/*
			 * Case, when there are more than one selected items. The loop goes through all of the selected items
			 * and determines their type. The recognised types are a directory, a zip archive and a text file
			 */
			File[] fl = fileTree.getSelectedFiles();
			for (File f : fl) {
				if (f.isDirectory()) {
					dirList = dirLst(f.getAbsoluteFile());
					for (File fil : dirList) {
						if (fileType(fil.getAbsolutePath()).equals("txt")) {
							Sfb book = null;
							try {
								book = new Sfb(fil.getAbsolutePath(), "", bookEnc);
							} catch (FileNotFoundException ex) {
								Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
							} catch (IOException ex) {
								Logger.getLogger(Fb2BatchConvGui.class.getName()).log(Level.SEVERE, null, ex);
							}
							bookList.add(book);
							fileListModel.addElement(fil.getName());
						} else if (fileType(fil.getAbsolutePath()).equals("zip")) {
							zipList.add(fil);
						}
					}

				} else if (fileType(f.getAbsolutePath()).equals("zip")) {
					//The item is a zip file
					new FbUnzip(f.getAbsolutePath(), tempDir, outputDir);
				} else if (fileType(f.getAbsolutePath()).equals("txt")) {
					//The item is a text file
					fileListModel.addElement(f.getName());
					absPathFileListModel.addElement(f.getAbsolutePath());
				}
			}
		}

		if (zipList.size() > 0) {
			new FbUnzip(zipList, tempDir, outputDir);
		}
	}

	/**
	 * <p>Create list of the contents of a given directory.</p>
	 * <p>While scanning the directory, the function detects if the element
	 * is a file or a directory. If the element is a file, then it is added
	 * to the returned list, else there is a recursive call to the same
	 * function to read the contents of this directory.</p>
	 * 
	 * @param dir The directory to be listed
	 * @return Listing of the directory
	 */
	private ArrayList<File> dirLst(File dir) {
		ArrayList<File> dlist = new ArrayList<File>();
		ArrayList<File> tempList = new ArrayList<File>();
		File[] fl = dir.listFiles();
		for (File tmpFl : fl) {
			if (tmpFl.isDirectory()) {
				tempList = dirLst(tmpFl);
				for (File tfl : tempList) {
					dlist.add(tfl);
				}
			} else {
				dlist.add(tmpFl);
			}
		}
		return dlist;
	}

  /**
   * <p>Removes a file from the file list.<br>
   * To perform the removal, the function searches for the selected file.</p>
   */
	private void actnRemoveFile() {
		if (fileList.getSelectedIndex() > -1) {
			if (fileList.getSelectedIndices().length == 1) {
				bookList.remove(fileList.getSelectedIndex());
				fileListModel.remove(fileList.getSelectedIndex());
			} else {
				int[] indexes = fileList.getSelectedIndices();
				for (int i = (indexes.length - 1); i >= 0; i--) {
					fileListModel.removeElementAt(indexes[i]);
				}
			}
		}
	}

  /**
   * Extracts the file type from the file name, based on the file extension.
   * @param fileName The file, which type is required.
   * @return The file type.
   */
	private String fileType(String fileName) {
		String ext = fileName.substring(fileName.length() - 3, fileName.length());
		return ext;
	}

  /**
   * Removes all items from the list.
   */
	private void actnClearList() {
		fileListModel.removeAllElements();
		absPathFileListModel.removeAllElements();
		bookList.clear();
	}


  /**
   * Converts all the text files in the list.
   */
	private void actnConvert() {
		new sfbConvert();
	}

	private void actnExit() {
		System.exit(0);
	}

  /**
   * Creates and fine-tunes the file tree component.
   */
	private void createFileTree() {
		fileTree = new FileTree();
		fileTree.setDeleteEnabled(false);
		fileTree.setDragEnabled(false);
		fileTree.setShowHiddenFiles(false);
		fileTree.setFont(fieldFont);
		fileTree.setToggleClickCount(2);

		String icoPath = sysProp.getUserDir() + sysProp.getFileSeparator() + "lookAndFeel" + sysProp.getFileSeparator() + "IconThemes" + sysProp.getFileSeparator() + iconTheme + sysProp.getFileSeparator();
		System.out.println(icoPath);

		ImageIcon leafIcon = new ImageIcon(icoPath + "treeLeaf.png");
		DefaultTreeCellRenderer rdr = new DefaultTreeCellRenderer();
		rdr.setLeafIcon(leafIcon);
		fileTree.setNavigateOSXApps(false);
		fileTree.setRootVisible(true);
		fileTree.setShowsRootHandles(true);
		
		ImageIcon openIcon = new ImageIcon(icoPath + "treeOpen.png");
		ImageIcon closeIcon = new ImageIcon(icoPath + "treeClosed.png");
		rdr.setOpenIcon(openIcon);
		rdr.setClosedIcon(closeIcon);

		fileTree.setCellRenderer(rdr);

		//System.out.println(lafName);

		Image top_left = new ImageIcon("lookAndFeel/Styles/" + lafName + "/Tree/Border/top-left-corner.png").getImage();
		Image top_center = new ImageIcon("lookAndFeel/Styles/" + lafName + "/Tree/Border/top-border.png").getImage();
		Image top_right = new ImageIcon("lookAndFeel/Styles/" + lafName + "/Tree/Border/top-right-corner.png").getImage();
		Image left_center = new ImageIcon("lookAndFeel/Styles/" + lafName + "/Tree/Border/left-border.png").getImage();
		Image right_center = new ImageIcon("lookAndFeel/Styles/" + lafName + "/Tree/Border/right-border.png").getImage();
		Image bottom_left = new ImageIcon("lookAndFeel/Styles/" + lafName + "/Tree/Border/bottom-left-corner.png").getImage();
		Image bottom_center = new ImageIcon("lookAndFeel/Styles/" + lafName + "/Tree/Border/bottom-border.png").getImage();
		Image bottom_right = new ImageIcon("lookAndFeel/Styles/" + lafName + "/Tree/Border/bottom-right-corner.png").getImage();

		//Color col = filePanel.getBackground();
		Color col = new Color(217, 215, 214);

		ImageBorder imgBorder = new ImageBorder(top_left, top_center, top_right, left_center, right_center, bottom_left, bottom_center, bottom_right, col);

		fileTreeScrollPanel = new JScrollPane(fileTree);
		fileTreeScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		fileTreeScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		fileTreeScrollPanel.setBorder(imgBorder);

		fileListModel = new DefaultListModel();
		fileList = new JList(fileListModel);
		fileList.setName("noBorder");
		fileList.setMinimumSize(new Dimension(5, 400));
		fileListScrollPanel = new JScrollPane(fileList);
		fileListScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		fileListScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		fileListScrollPanel.setBorder(imgBorder);
	}

	/**
	 * Creates the book information part of the interface.
	 * 
	 * @throws java.io.IOException
	 */
	private void createBookInfoPanel() throws IOException {
		bookInfoLayout = new MigLayout("", "[grow,fill]");
		bookInfoPanel = new JPanel(bookInfoLayout);

		MigLayout mBookInfoLayout = new MigLayout("", "[pref!][pref!]");
		mBookInfo = new JPanel(mBookInfoLayout);

		genereLabel = new JLabel("Genere");
		genereLabel.setFont(labelFont);

		genereCombo = new JComboBox(loadItemsFromFile("./lists/fbGenereNames.lst"));
		genereCombo.setEditable(true);
		genereCombo.setFont(fieldFont);
		genereCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cGenereCombo = true;
			}
		});

		mBookInfo.add(genereLabel);
		mBookInfo.add(genereCombo);

		langLabel = new JLabel("Language");
		langLabel.setFont(labelFont);

		langCombo = new JComboBox(loadItemsFromFile("./lists/languageNames.lst"));
		langCombo.setEditable(true);
		langCombo.setFont(fieldFont);
		langCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cLangCombo = true;
			}
		});

		mBookInfo.add(langLabel);
		mBookInfo.add(langCombo);

		oriLangLabel = new JLabel("Original Language");
		oriLangLabel.setFont(labelFont);

		oriLangCombo = new JComboBox(loadItemsFromFile("./lists/languageNames.lst"));
		oriLangCombo.setEditable(true);
		oriLangCombo.setFont(fieldFont);
		oriLangCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cOriLangCombo = true;
			}
		});

		mBookInfo.add(oriLangLabel);
		mBookInfo.add(oriLangCombo);


		MigLayout authLayout = new MigLayout("", "[pref!][grow,fill]");
		authorInfo = new JPanel(authLayout);
		authorInfo.setName("titled");
		//authorInfo.setBackground(new Color(217, 216, 215));
		authorInfo.setBorder(BorderFactory.createTitledBorder(authorInfo.getBorder(), "Author Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));

		authNameLabel = new JLabel("Name");
		authNameLabel.setFont(labelFont);

		authNameTBox = new JTextField(20);
		authNameTBox.setFont(fieldFont);
		authNameTBox.setEditable(true);
		authNameTBox.getDocument().addDocumentListener(this);

		authorInfo.add(authNameLabel);
		authorInfo.add(authNameTBox, "wrap, grow 100, span 4");

		authSurnameLabel = new JLabel("Surname");
		authSurnameLabel.setFont(labelFont);

		authSurnameTBox = new JTextField(20);
		authSurnameTBox.setFont(fieldFont);
		authSurnameTBox.setEditable(true);
		authSurnameTBox.getDocument().addDocumentListener(this);

		authorInfo.add(authSurnameLabel);
		authorInfo.add(authSurnameTBox, "wrap, grow 100, span 4");

		authCognomenLabel = new JLabel("Cognomen");
		authCognomenLabel.setFont(labelFont);

		authCognomenTBox = new JTextField(20);
		authCognomenTBox.setFont(fieldFont);
		authCognomenTBox.setEditable(true);
		authCognomenTBox.getDocument().addDocumentListener(this);

		authorInfo.add(authCognomenLabel);
		authorInfo.add(authCognomenTBox, "wrap, grow 100, span 4");

		authNicLabel = new JLabel("Nicname");
		authNicLabel.setFont(labelFont);

		authNicTBox = new JTextField(20);
		authNicTBox.setFont(fieldFont);
		authNicTBox.setEditable(true);
		authNicTBox.getDocument().addDocumentListener(this);

		authorInfo.add(authNicLabel);
		authorInfo.add(authNicTBox, "wrap, grow 100, span 4");

		authEmailLabel = new JLabel("Email");
		authEmailLabel.setFont(labelFont);

		authEmailTBox = new JTextField(20);
		authEmailTBox.setFont(fieldFont);
		authEmailTBox.setEditable(true);
		authEmailTBox.getDocument().addDocumentListener(this);

		authorInfo.add(authEmailLabel);
		authorInfo.add(authEmailTBox);

		authSiteLabel = new JLabel("Website");
		authSiteLabel.setFont(labelFont);

		authSiteTBox = new JTextField(20);
		authSiteTBox.setFont(fieldFont);
		authSiteTBox.setEditable(true);
		authSiteTBox.getDocument().addDocumentListener(this);

		authorInfo.add(authSiteLabel);
		authorInfo.add(authSiteTBox);

		MigLayout titleLayout = new MigLayout("wrap 2", "[pref!][grow,fill]");
		titleInfo = new JPanel(titleLayout);
		titleInfo.setName("titled");
		titleInfo.setBorder(BorderFactory.createTitledBorder(titleInfo.getBorder(), "Book Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));

		bookTitleLabel = new JLabel("Title");
		bookTitleLabel.setFont(labelFont);

		bookTitleTBox = new JTextField(20);
		bookTitleTBox.setFont(fieldFont);
		bookTitleTBox.setEditable(true);
		bookTitleTBox.getDocument().addDocumentListener(this);

		titleInfo.add(bookTitleLabel);
		titleInfo.add(bookTitleTBox);

		bookKeywordsLabel = new JLabel("Keywords");
		bookKeywordsLabel.setFont(labelFont);

		bookKeywordsTBox = new JTextField(20);
		bookKeywordsTBox.setFont(fieldFont);
		bookKeywordsTBox.setEditable(true);
		bookKeywordsTBox.getDocument().addDocumentListener(this);

		titleInfo.add(bookKeywordsLabel);
		titleInfo.add(bookKeywordsTBox);

		MigLayout transLayout = new MigLayout("", "[pref!][grow,fill]");
		transInfo = new JPanel(transLayout);
		transInfo.setName("titled");
		transInfo.setBorder(BorderFactory.createTitledBorder(transInfo.getBorder(), "Translator Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));

		transNameLabel = new JLabel("Name");
		transNameLabel.setFont(labelFont);

		transNameTBox = new JTextField(20);
		transNameTBox.setFont(fieldFont);
		transNameTBox.setEditable(true);
		transNameTBox.getDocument().addDocumentListener(this);

		transInfo.add(transNameLabel);
		transInfo.add(transNameTBox, "wrap, grow 100, span 4");

		transSurnameLabel = new JLabel("Surname");
		transSurnameLabel.setFont(labelFont);

		transSurnameTBox = new JTextField(20);
		transSurnameTBox.setFont(fieldFont);
		transSurnameTBox.setEditable(true);
		transSurnameTBox.getDocument().addDocumentListener(this);

		transInfo.add(transSurnameLabel);
		transInfo.add(transSurnameTBox, "wrap, grow 100, span 4");

		transCognomenLabel = new JLabel("Cognomen");
		transCognomenLabel.setFont(labelFont);

		transCognomenTBox = new JTextField(20);
		transCognomenTBox.setFont(fieldFont);
		transCognomenTBox.setEditable(true);
		transCognomenTBox.getDocument().addDocumentListener(this);

		transInfo.add(transCognomenLabel);
		transInfo.add(transCognomenTBox, "wrap, grow 100, span 4");

		transNicLabel = new JLabel("Nicname");
		transNicLabel.setFont(labelFont);

		transNicTBox = new JTextField(20);
		transNicTBox.setFont(fieldFont);
		transNicTBox.setEditable(true);
		transNicTBox.getDocument().addDocumentListener(this);

		transInfo.add(transNicLabel);
		transInfo.add(transNicTBox, "wrap, grow 100, span 4");

		transEmailLabel = new JLabel("Email");
		transEmailLabel.setFont(labelFont);

		transEmailTBox = new JTextField(20);
		transEmailTBox.setFont(fieldFont);
		transEmailTBox.setEditable(true);
		transEmailTBox.getDocument().addDocumentListener(this);

		transInfo.add(transEmailLabel);
		transInfo.add(transEmailTBox);

		transSiteLabel = new JLabel("Website");
		transSiteLabel.setFont(labelFont);

		transSiteTBox = new JTextField(20);
		transSiteTBox.setFont(fieldFont);
		transSiteTBox.setEditable(true);
		transSiteTBox.getDocument().addDocumentListener(this);

		transInfo.add(transSiteLabel);
		transInfo.add(transSiteTBox);

		MigLayout seriesLayout = new MigLayout("wrap 4", "[pref!][grow,fill][pref!][pref!]");
		seriesInfo = new JPanel(seriesLayout);
		seriesInfo.setName("titled");
		seriesInfo.setBorder(BorderFactory.createTitledBorder(seriesInfo.getBorder(), "Series Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));

		seriesNameLabel = new JLabel("Series");
		seriesNameLabel.setFont(labelFont);

		seriesNameTBox = new JTextField(10);
		seriesNameTBox.setFont(fieldFont);
		seriesNameTBox.setEditable(true);
		seriesNameTBox.getDocument().addDocumentListener(this);

		seriesNumLabel = new JLabel("No.");
		seriesNumLabel.setFont(labelFont);

		seriesNumTBox = new JTextField(10);
		seriesNumTBox.setFont(fieldFont);
		seriesNumTBox.setEditable(true);
		seriesNumTBox.getDocument().addDocumentListener(this);

		bookTitle = new JLabel("Book title");
		bookTitle.setFont(labelFont);

		bookTitleText = new JTextField(20);
		bookTitleText.setFont(fieldFont);
		bookTitleText.setEditable(true);
		bookTitleText.getDocument().addDocumentListener(this);

		MigLayout fb2Layout = new MigLayout("", "[][grow, fill][][grow]");
		fb2Panel = new JPanel(fb2Layout);
		fb2Panel.setName("titled");
		fb2Panel.setBorder(BorderFactory.createTitledBorder(fb2Panel.getBorder(), "Publish Information", TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.BLUE));

		fb2Panel.add(bookTitle);
		fb2Panel.add(bookTitleText, "grow");

		bookISBN = new JLabel("ISBN");
		bookISBN.setFont(labelFont);

		bookISBNText = new JTextField(15);
		bookISBNText.setFont(fieldFont);
		bookISBNText.setEditable(true);
		bookISBNText.getDocument().addDocumentListener(this);

		fb2Panel.add(bookISBN);
		fb2Panel.add(bookISBNText, "wrap");

		bookSeries = new JLabel("Series");
		bookSeries.setFont(labelFont);

		bookSeriesText = new JTextField(15);
		bookSeriesText.setFont(fieldFont);
		bookSeriesText.setEditable(true);
		bookSeriesText.getDocument().addDocumentListener(this);

		fb2Panel.add(bookSeries);
		fb2Panel.add(bookSeriesText, "grow");

		bookSeriesNumber = new JLabel("Number");
		bookSeriesNumber.setFont(labelFont);

		bookSeriesNumberText = new JTextField(10);
		bookSeriesNumberText.setFont(fieldFont);
		bookSeriesNumberText.setEditable(true);
		bookSeriesNumberText.getDocument().addDocumentListener(this);

		fb2Panel.add(bookSeriesNumber);
		fb2Panel.add(bookSeriesNumberText);

		seriesInfo.add(seriesNameLabel);
		seriesInfo.add(seriesNameTBox);
		seriesInfo.add(seriesNumLabel);
		seriesInfo.add(seriesNumTBox);

		bookInfoPanel.add(mBookInfo, "wrap");
		bookInfoPanel.add(authorInfo, "wrap");
		bookInfoPanel.add(titleInfo, "wrap");
		bookInfoPanel.add(transInfo, "wrap");
		bookInfoPanel.add(seriesInfo, "wrap");
		bookInfoPanel.add(fb2Panel, "wrap");
	}

	//-------------------------- BEGIN --------------------
	//Unzip panel with progressbars in a SwingWorker thread
	//-------------------------- BEGIN --------------------
	/**
	 * Class, designed to handle unzip operations over zipped FB files
	 */
	private class FbUnzip {

		private String fileName,  tempDir, dirOut;
		private JProgressBar pBar,  pBar2;
		private ArrayList<File> files;
		private JPanel pPanel;
		private JFrame pFrame;
		private int flag;
		private ArrayList<String> fList;
    int loop = 0;

		FbUnzip(String fName, String dName) {
			fList = new ArrayList<String>();
			setZipFile(fName);
			setTempDir(dName);
			setFlag(1);
			run();
		}

    FbUnzip(String fName, String dName, String dOutName) {
			fList = new ArrayList<String>();
			setZipFile(fName);
			setTempDir(dName);
      setOutputDir(dOutName);
			setFlag(1);
			run();
		}

		FbUnzip(ArrayList<File> fl, String dName) {
			fList = new ArrayList<String>();
			setFiles(fl);
			setTempDir(dName);
			setFlag(2);
			run();
		}

    FbUnzip(ArrayList<File> fl, String dName, String dOutName) {
			fList = new ArrayList<String>();
			setFiles(fl);
			setTempDir(dName);
      setOutputDir(dOutName);
			setFlag(2);
			run();
		}

		public ArrayList<String> getFileList() {
			return fList;
		}

		public void run() {
			if (this.flag == 1) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						createAndRunSingle();
					}
				});
			} else if (this.flag == 2) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						createAndRunMulti();
					}
				});
			}
		}

		public void setFiles(ArrayList<File> fl) {
			files = fl;
		}

		public void setFlag(int value) {
			flag = value;
		}

		public void setZipFile(String fName) {
			this.fileName = fName;
		}

		public void setTempDir(String tDir) {
			if (tDir.equals("")) {
				this.tempDir = sysProp.getUserDir() + sysProp.getFileSeparator() + "temp" + sysProp.getFileSeparator();
			} else {
				this.tempDir = tDir;
			}

			if(!(new File(tDir).exists())){
				new File(tDir).mkdir();
			}
		}

    public void setOutputDir(String oDir){
      if (oDir.equals("")) {
				this.dirOut = sysProp.getUserDir() + sysProp.getFileSeparator() + "Books" + sysProp.getFileSeparator();
			} else {
				this.dirOut = oDir;
			}

      if(!(new File(dirOut).exists())){
        new File(dirOut).mkdir();
      }
    }

		public void resetStatus() {
			pBar.setValue(0);
		}

		private void createAndRunSingle() {
			pFrame = new JFrame("Working, please wait...");
			pFrame.setResizable(false);
			MigLayout pMigLayout = new MigLayout("", "grow, fill");

			pBar = new JProgressBar(0, 100);
			pBar.setFont(labelFont);
			pBar.setString("0%");
			pBar.setStringPainted(true);
			pPanel = new JPanel(pMigLayout);
			pPanel.add(pBar, "grow,wmin 300, hmin 20, wrap");

			pFrame.getContentPane().add(pPanel);
			pFrame.pack();
			pFrame.setSize(new Dimension(350, 62));
			pFrame.setLocationRelativeTo(null);
			pFrame.setVisible(true);

			TaskSingle task = new TaskSingle();
			task.execute();
		}

		private void createAndRunMulti() {
			pFrame = new JFrame("Working, please wait...");
			pFrame.setResizable(false);
			MigLayout pMigLayout = new MigLayout("", "grow, fill");

			pBar = new JProgressBar(0, 100);
			pBar.setString("0%");
			pBar.setStringPainted(true);
			pBar.setFont(labelFont);
			pPanel = new JPanel(pMigLayout);
			JLabel cProgress = new JLabel("Current file");
			cProgress.setFont(labelFont);
			pPanel.add(cProgress, "wrap");
			pPanel.add(pBar, "grow,wmin 300, hmin 20, wrap");

			pBar2 = new JProgressBar(0, 100);
			pBar2.setString("0/0");
			pBar2.setStringPainted(true);
			pBar2.setFont(labelFont);
			JLabel allProgres = new JLabel("All files");
			allProgres.setFont(labelFont);
			pPanel.add(allProgres, "wrap");
			pPanel.add(pBar2, "grow, wmin 300, hmin 20, wrap");

			pFrame.getContentPane().add(pPanel);
			pFrame.pack();
			pFrame.setSize(new Dimension(350, 130));
			pFrame.setLocationRelativeTo(null);
			pFrame.setVisible(true);

			TaskMulti task = new TaskMulti();
			task.execute();
		}

		private void unZip(String file) throws IOException {
			ZipFile zf = new ZipFile(file);
			Enumeration e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				if (ze.isDirectory()) {
					new File(tempDir + ze.getName()).mkdir();
					continue;
				}

				copyInputStream(zf.getInputStream(ze), new BufferedOutputStream(new FileOutputStream(tempDir + ze.getName())));

				if (fileType(ze.getName()).equals("zip")) {
					unZip(tempDir + ze.getName());
				}
			}
		}

		private void alUnZip(String file, JProgressBar bar) throws IOException {
			ZipFile zf = new ZipFile(file);
			Enumeration e = zf.entries();
			bar.setValue(0);
			int curFile = 1;
			double newPos = 0;
			bar.setString((int) newPos + "%");
      String sDir = "";

			String str[] = zf.getName().split("\\" + sysProp.getFileSeparator());

			int lng = str[str.length - 1].length() - 4;

      String[] tmpar;

			String subDir = tempDir + sysProp.getFileSeparator() + str[str.length - 1].substring(0, lng) + sysProp.getFileSeparator();
      sDir = zf.getName().substring(0, zf.getName().length() - 4);
      tmpar = sDir.split("\\" + sysProp.getFileSeparator());
      sDir = tmpar[tmpar.length-1];
      sDir = outputDir + sysProp.getFileSeparator() + sDir;
      

      if(!(new File(sDir).exists())){
        new File(sDir).mkdir();
      }

			new File(subDir).mkdir();

			while (e.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				if (ze.isDirectory()) {
					new File(subDir + ze.getName()).mkdir();
					continue;
				}

				if (fileType(ze.getName()).equals("txt") || fileType(ze.getName()).equals("fbi")) {
					copyInputStream(zf.getInputStream(ze), new BufferedOutputStream(new FileOutputStream(subDir + ze.getName())));

          Sfb book;

          if(!sDir.equals("")){
            book = new Sfb(subDir + ze.getName(), sysProp.getFileSeparator() + sDir + sysProp.getFileSeparator() + ze.getName().substring(0, ze.getName().length()-4) + ".fb2" , bookEnc);
          } else {
            book = new Sfb(subDir + ze.getName(), sysProp.getFileSeparator() + ze.getName().substring(0, ze.getName().length()-4) + ".fb2" , bookEnc);
          }


					if (fileType(ze.getName()).equals("txt")) {
						bookList.add(book);
						fileListModel.addElement(ze.getName());
					}
				}



				if (fileType(ze.getName()).equals("zip")) {
					copyInputStream(zf.getInputStream(ze), new BufferedOutputStream(new FileOutputStream(subDir + ze.getName())));
					bar.setIndeterminate(true);
					unZip(subDir + ze.getName());
				} else {
					bar.setIndeterminate(false);
				}

				newPos = ((curFile * 100) / countFiles(file));
				bar.setString((int) newPos + "%");
				bar.setValue((int) newPos);
				curFile++;
			}
		}

		private void alUnZip(ArrayList<File> fl, JProgressBar bar1, JProgressBar bar2) throws IOException {
			int czFile = 1;
			int newPos2 = 0;
			int zfCount = fl.size();
			String fName;
			bar2.setString(czFile + "/" + zfCount);
			for (int i = 0; i < fl.size(); i++) {
				fName = fl.get(i).getAbsolutePath();
				alUnZip(fName, bar1);

				newPos2 = ((czFile * 100) / zfCount);
				bar2.setString(czFile + "/" + zfCount);
				bar2.setValue(newPos2);
				czFile++;
			}
		}

		class TaskMulti extends SwingWorker<Void, Void> {

			@Override
			protected Void doInBackground() throws Exception {
				frame.setEnabled(false);
				alUnZip(files, pBar, pBar2);

				return null;
			}

			@Override
			public void done() {
				frame.setEnabled(true);
				pFrame.setVisible(false);
			}
		}

		class TaskSingle extends SwingWorker<Void, Void> {

			@Override
			protected Void doInBackground() throws Exception {
				frame.setEnabled(false);
				alUnZip(fileName, pBar);
				return null;
			}

			@Override
			public void done() {
				frame.setEnabled(true);
				pFrame.setVisible(false);
			}
		}

		private void copyInputStream(InputStream in, OutputStream out) throws IOException {
			byte[] buffer = new byte[1024];
			int len;

			while ((len = in.read(buffer)) >= 0) {
				out.write(buffer, 0, len);
			}

			in.close();
			out.close();
		}

		private int countFiles(String fName) throws IOException {
			int count = 0;
			ZipFile zf = new ZipFile(fName);
			Enumeration e = zf.entries();
			while (e.hasMoreElements()) {
				e.nextElement();
				count++;
			}
			return count;
		}
	}
	//--------------------------- END ---------------------

	//-------------------------- BEGIN --------------------
	//Convert the SFB files using SwingWorker thread
	//-------------------------- BEGIN --------------------
	private class sfbConvert {

		private JProgressBar pBar;
		private JPanel pPanel;
		private JFrame pFrame;
		private ArrayList<Integer> idxs;

		sfbConvert() {
			run();
		}

		sfbConvert(int[] indexes) {
			idxs = new ArrayList<Integer>();
			for (int i : indexes) {
				idxs.add(i);
			}
			runSelected();
		}

		private void runSelected() {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					createAndRunSelected();
				}
			});
		}

		public void run() {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					createAndRun();
				}
			});
		}

		private void createAndRunSelected() {
			pFrame = new JFrame("Working, please wait...");
			pFrame.setResizable(false);
			MigLayout pMigLayout = new MigLayout("", "grow, fill");

			pBar = new JProgressBar(0, 100);
			pBar.setFont(labelFont);
			pBar.setString("0/" + idxs.size());
			pBar.setStringPainted(true);
			pPanel = new JPanel(pMigLayout);
			pPanel.add(pBar, "grow,wmin 300, hmin 20, wrap");

			pFrame.getContentPane().add(pPanel);
			pFrame.pack();
			pFrame.setSize(new Dimension(350, 65));
			pFrame.setLocationRelativeTo(null);
			pFrame.setVisible(true);

			TaskSelected task = new TaskSelected();
			task.execute();
		}

		private void createAndRun() {
			pFrame = new JFrame("Working, please wait...");
			pFrame.setResizable(false);
			MigLayout pMigLayout = new MigLayout("", "grow, fill");

			pBar = new JProgressBar(0, 100);
			pBar.setFont(labelFont);
			pBar.setString("0/" + bookList.size());
			pBar.setStringPainted(true);
			pPanel = new JPanel(pMigLayout);
			pPanel.add(pBar, "grow,wmin 300, hmin 20, wrap");

			pFrame.getContentPane().add(pPanel);
			pFrame.pack();
			pFrame.setSize(new Dimension(350, 65));
			pFrame.setLocationRelativeTo(null);
			pFrame.setVisible(true);

			Task task = new Task();
			task.execute();
		}
    /**
     * Creates a SwingWorker thread for converting only the selected item
     * from the book list. 
     */
		class TaskSelected extends SwingWorker<Void, Void> {

			@Override
			@SuppressWarnings("empty-statement")
			protected Void doInBackground() throws Exception {
				int cBook = 1;
				pBar.setMinimum(0);
				pBar.setMaximum(100);
				frame.setEnabled(false);

				if (idxs.size() == 1) {
					pBar.setIndeterminate(true);
					pBar.setStringPainted(false);
				}

				for (int i : idxs) {
					;
					Sfb book = bookList.get(i);
					book.prepare();
					book.finish();
					pBar.setValue((cBook * 100) / (idxs.size() - 1));
					pBar.setString(cBook + "/" + (idxs.size() - 1));
					cBook++;
				}
				return null;
			}

			@Override
			public void done() {
				pFrame.setVisible(false);
				frame.setEnabled(true);
				while (idxs.size() > 0) {
					int rmIdx = idxs.get(idxs.size() - 1);
					bookList.remove(rmIdx);
					fileListModel.remove(rmIdx);
					idxs.remove(idxs.size() - 1);
					fileList.setSelectedIndex(0);
				}
			}
		}

		class Task extends SwingWorker<Void, Void> {

			@Override
			protected Void doInBackground() throws Exception {
				int cBook = 1;
				pBar.setMinimum(0);
				pBar.setMaximum(100);
				frame.setEnabled(false);
				for (Sfb book : bookList) {
					book.prepare();
					book.finish();
					pBar.setValue((cBook * 100) / bookList.size());
					pBar.setString(cBook + "/" + bookList.size());
					cBook++;
				}
				return null;
			}

			@Override
			public void done() {
				pFrame.setVisible(false);
				frame.setEnabled(true);
			}
		}
	}
	//--------------------------- END ---------------------

	public void setVisible(boolean flag) {
		frame.pack();
		
		frame.setLocationRelativeTo(null);
		fileTree.requestFocus();
		Dimension dim = fileTree.getSize();
		fileTree.setMaximumSize(new Dimension(dim.width, 300));
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("sfb2fb2.png"));
		frame.setVisible(flag);
	}

	/**
	 * Set the font for the labels. Besides the label fonts, the function changes the font of the titled borders.
	 *
	 * @param lFont The label font
	 */
	private void setLabelFont(Font lFont){
		labelFont = lFont;

		genereLabel.setFont(lFont);
		langLabel.setFont(lFont);
		oriLangLabel.setFont(lFont);
		authNameLabel.setFont(lFont);
		authSurnameLabel.setFont(lFont);
		authCognomenLabel.setFont(lFont);
		authNicLabel.setFont(lFont);
		authEmailLabel.setFont(lFont);
		authSiteLabel.setFont(lFont);
		bookTitleLabel.setFont(lFont);
		bookKeywordsLabel.setFont(lFont);
		transNameLabel.setFont(lFont);
		transSurnameLabel.setFont(lFont);
		transCognomenLabel.setFont(lFont);
		transNicLabel.setFont(lFont);
		transEmailLabel.setFont(lFont);
		transSiteLabel.setFont(lFont);
		seriesNameLabel.setFont(lFont);
		seriesNumLabel.setFont(lFont);
		bookTitle.setFont(lFont);
		bookISBN.setFont(lFont);
		bookSeries.setFont(lFont);
		bookSeriesNumber.setFont(lFont);
		addFile.setFont(lFont);
		removeFile.setFont(lFont);
		clearList.setFont(lFont);
		convertSel.setFont(lFont);
		convert.setFont(lFont);
		settings.setFont(lFont);
		exit.setFont(lFont);
		
		authorInfo.setBorder(null);
		titleInfo.setBorder(null);
		transInfo.setBorder(null);
		seriesInfo.setBorder(null);
		fb2Panel.setBorder(null);

		JPanel borderPrototype = new JPanel();
		borderPrototype.setName("titled");

		authorInfo.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Author Information", TitledBorder.LEFT, TitledBorder.TOP, lFont, Color.BLUE));
		titleInfo.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Book Information", TitledBorder.LEFT, TitledBorder.TOP, lFont, Color.BLUE));
		transInfo.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Translator Information", TitledBorder.LEFT, TitledBorder.TOP, lFont, Color.BLUE));
		seriesInfo.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Series Information", TitledBorder.LEFT, TitledBorder.TOP, lFont, Color.BLUE));
		fb2Panel.setBorder(BorderFactory.createTitledBorder(borderPrototype.getBorder(), "Publish Information", TitledBorder.LEFT, TitledBorder.TOP, lFont, Color.BLUE));
		
	}

	/**
	 * Set the font for the content of the fields
	 *
	 * @param fFont The field element font
	 */
	private void setFieldFont(Font fFont){
		fieldFont = fFont;

		genereCombo.setFont(fFont);
		langCombo.setFont(fFont);
		oriLangCombo.setFont(fFont);
		authNameTBox.setFont(fFont);
		authSurnameTBox.setFont(fFont);
		authCognomenTBox.setFont(fFont);
		authNicTBox.setFont(fFont);
		authEmailTBox.setFont(fFont);
		authSiteTBox.setFont(fFont);
		bookTitleTBox.setFont(fFont);
		bookKeywordsTBox.setFont(fFont);
		transNameTBox.setFont(fFont);
		transSurnameTBox.setFont(fFont);
		transCognomenTBox.setFont(fFont);
		transNicTBox.setFont(fFont);
		transEmailTBox.setFont(fFont);
		transSiteTBox.setFont(fFont);
		seriesNameTBox.setFont(fFont);
		seriesNumTBox.setFont(fFont);
		bookTitleText.setFont(fFont);
		bookISBNText.setFont(fFont);
		bookSeriesText.setFont(fFont);
		bookSeriesNumberText.setFont(fFont);
		fileList.setFont(fFont);
		fileTree.setFont(fFont);
	}
}
