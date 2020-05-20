package fictionBook;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.miginfocom.swing.MigLayout;

/**
 * <p>A FontChooser dialog, used to select a font and font settings from the
 * existing fonts on the system.</p>
 * <p>The component implements ActionListener for capturing of the actions,
 * performed with the JButton components and DocumentListener for monitoring
 * the changes in the JTextField components</p>
 * <p>The FontChooser object consists of the following java components:</p>
 *
 * <ol>
 *	<li>JPanels for the setting of an layout, used as a content pane for the JFrame
 *	<li>MigLayout managers for arranging of the visible components
 *	<li>JTextField components for filtering the lists.
 *	<li>FilteringJList components for the font names and the font sizes.
 *	<li>JScrollPane components, for granting a scrollbars to the JList components and the preview pane
 *	<li>JCheckBox components for setting the BOLD and ITALIC values
 *	<li>JButtons for accepting or canceling the font change
 * </ol>
 *
 * <p>For styling with Synth you need to have styles defined for all of the visible
 * components, listend above.</p>
 *
 * <p><b>The FileChooser depends on the <a href="http://www.miglayout.com/">MiG Layout</a></b></p>
 *
 * @author Ivaylo Iliev
 * @see ActionListener
 * @see <a href="http://www.migcalendar.com/miglayout/javadoc/index.html">MiGLayout</a>
 * @see FilteringJList
 */

 /*
 * @todo Implement custom values for the size of the font
 * @todo Add detection of the current used font
 */

public class FontChooser extends JDialog implements ActionListener{

	//Global variables for the class
	private JPanel panel;
	private MigLayout dlgLayout;

	private JLabel fontNameLabel;
	private JLabel fontSizeLabel;

	private JTextField fontNameFilter;
	private JTextField fontSizeFilter;

	private JScrollPane namesScrollPane;
	private JScrollPane sizesScrollPane;
	private JScrollPane previewScrollPane;

	private FilteringJList fontNames;
	private FilteringJList fontSizes;

	private JLabel previewLabel;

	private JCheckBox chkBold;
	private JCheckBox chkItalic;

	private JButton btnOk;
	private JButton btnCancel;

	private ListSelectionModel fontSelModel;
	private ListSelectionModel sizeSelModel;
	private lsHandler lshFonts,  lshSizes;
	private int lSelFontIdx = 0;
	private int lSelSizeIdx = 0;
	private boolean bold = false;
	private boolean italic = false;

	private String fntName, icoPath;
	private int fntSize;

	private Font rFont;

	Settings se;
	private systemProperties sysProp;

	/**
	 * The default constructor for the FileChooserClass. Besides creating the object,
	 * the constructor creates the FontChooser GUI
	 */
	public FontChooser(Frame owner){
		super(owner, "Choose Font");
		setModal(true);
		dlgLayout = new MigLayout("", "[grow, fill][pref!]","[pref!][pref!][grow,fill]");

		sysProp = new systemProperties();
		se = new Settings("config.ini");
		icoPath = sysProp.getUserDir() + sysProp.getFileSeparator() + "lookAndFeel" + sysProp.getFileSeparator() + "IconThemes" + sysProp.getFileSeparator() + se.getIconTheme() + sysProp.getFileSeparator();

		panel = new JPanel();
		panel.setLayout(dlgLayout);

		//MiG Layout Row1
		fontNameLabel = new JLabel("Font");
		fontSizeLabel = new JLabel("Size");
		
		panel.add(fontNameLabel);
		panel.add(fontSizeLabel, "wrap");

		//MiG Layout Row2
		fontNameFilter = new JTextField(5);
		fontSizeFilter = new JTextField(5);

		panel.add(fontNameFilter);
		panel.add(fontSizeFilter, "wrap, wmin 100, wmax 100, growy");

		//MiG Layout Row3
		//Create the font list
		fontNames = new FilteringJList();
		fontNames.setName("noBorder");
		fontNames.installJTextField(fontNameFilter);
		namesScrollPane = new JScrollPane(fontNames);
		namesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		namesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		//Create the font sizes list
		fontSizes = new FilteringJList();
		fontSizes.setName("noBorder");
		fontSizes.installJTextField(fontSizeFilter);
		sizesScrollPane = new JScrollPane(fontSizes);
		sizesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sizesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		panel.add(namesScrollPane, "wmin 300");
		panel.add(sizesScrollPane, "wmin 100, wmax 100, growy, wrap, hmin 200");

		previewLabel = new JLabel("The quick brown fox jumped over the lazy dog.", JLabel.CENTER);
		previewScrollPane = new JScrollPane(previewLabel);

		panel.add(previewScrollPane, "spanx 2, wrap, hmin 80");

		JPanel optPanel = new JPanel();
		MigLayout optLayout = new MigLayout("", "[pref!,left][grow,left][grow,right][pref!,right]");
		optPanel.setLayout(optLayout);

		chkBold = new JCheckBox("Bold");
		chkBold.addActionListener(this);
		chkItalic = new JCheckBox("Italic");
		chkItalic.addActionListener(this);

		Icon okGlyph = new ImageIcon(icoPath + "ok.png");
		Icon cancelGlyph = new ImageIcon(icoPath + "cancel.png");

		btnOk = new JButton("OK");
		btnOk.setIcon(okGlyph);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		
		btnCancel = new JButton("Cancel");
		btnCancel.setIcon(cancelGlyph);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rFont = null;
				hide();
			}
		});

		optPanel.add(chkBold);
		optPanel.add(chkItalic);
		optPanel.add(btnOk);
		optPanel.add(btnCancel);

		panel.add(optPanel, "spanx 2");

		//Load fonts and sizes
		readFonts();
		readSizes();

		//Add listeners for the selection change in the lists
		fontSelModel = fontNames.getSelectionModel();
		lshFonts = new lsHandler();
		fontSelModel.addListSelectionListener(lshFonts);

		sizeSelModel = fontSizes.getSelectionModel();
		lshSizes = new lsHandler();
		sizeSelModel.addListSelectionListener(lshSizes);

		setContentPane(panel);
		
		//Set dialog size and location
		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * Gets the font, selected with this dialog
	 *
	 * @return The font, choosen with the dialog
	 */
	public Font getSelectedFont(){
		return rFont;
	}

	/**
	 * Get a list ot all available fonts in the system and add them to the font
	 * list model
	 */
	private void readFonts(){
		Set fntSet = new HashSet<String>();
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fnts = e.getAllFonts();
		for (Font fnt : fnts) {
			fntSet.add(fnt.getFamily());
		}

		Iterator it = fntSet.iterator();
		while (it.hasNext()) {
			fontNames.addElement(it.next());
		}

		fontNames.sort();
	}

	/**
	 * Sets a ImageBorder to the JScrollPanes.
	 *
	 * @see ImageBorder
	 *
	 * @param topLeft The top left element of the border
	 * @param topCenter The top central element of the border
	 * @param topRight The top right element of the border
	 * @param leftCenter The left central element of the border
	 * @param rightCenter The right central element of the border
	 * @param bottomLeft The bottom left element of the border
	 * @param bottomCenter The bottom central element of the border
	 * @param bottomRight The bottom right element of the border
	 * @param clr A backgroud color, visible in case of JScrollPane with vertical and
	 * horisontal scrollbars. The color is visible between the two scrollbars.
	 * 
	 */
	public void setImageBorder(Image topLeft, Image topCenter, Image topRight,
			Image leftCenter, Image rightCenter, Image bottomLeft,
			Image bottomCenter, Image bottomRight, Color color) {

		ImageBorder imgBorder = new ImageBorder(topLeft, topCenter, topRight, leftCenter,
				rightCenter, bottomLeft, bottomCenter, bottomRight, color);

		namesScrollPane.setBorder(imgBorder);
		sizesScrollPane.setBorder(imgBorder);
		previewScrollPane.setBorder(imgBorder);
	}

	public void setBorder(Border brdr){
		namesScrollPane.setBorder(brdr);
		sizesScrollPane.setBorder(brdr);
		previewScrollPane.setBorder(brdr);
	}

	/**
	 * Sets the borders of the JScrollPanes. This is used in case no
	 * ImageBorder is used
	 *
	 * @param border A border for the two JScrollPanes
	 */
	public void setComponentBorder(Border border) {
		namesScrollPane.setBorder(border);
		sizesScrollPane.setBorder(border);
		previewScrollPane.setBorder(border);
	}

	/**
	 * Set the glyph of the OK Button
	 *
	 * @param okGlyph Icon, containing the glyph
	 */
	public void setOkGlyph(Icon okGlyph){
		btnOk.setIcon(okGlyph);
	}

	/**
	 * Set the glyph for the Cancel Button
	 *
	 * @param cancelGlyph Icon, containing the glyph
	 */
	public void setCancelGlyph(Icon cancelGlyph){
		btnCancel.setIcon(cancelGlyph);
	}

	/**
	 * Fill the font sizes list with default font sizes in points.
	 */
	private void readSizes(){
		for(int i=1;i<=100;i++){
			fontSizes.addElement(Integer.toString(i));
		}

		fontSizes.setSelectedIndex(10);
	}

	/**
	 * Override for the implemented action listener. It determines the performed
	 * action when a button is pressed.
	 *
	 * @param e The Action event
	 */
	@Override
	public void actionPerformed(ActionEvent a) {

		if (lSelSizeIdx == 0) {
			lSelSizeIdx = 10;
		}

		if (a.getSource() == chkBold) {
			bold = chkBold.isSelected();
			changePreview((String) fontNames.getElementAt(lSelFontIdx), Integer.parseInt((String) fontSizes.getElementAt(lSelSizeIdx)), bold, italic);
		} else if (a.getSource() == chkItalic) {
			italic = chkItalic.isSelected();
			changePreview((String) fontNames.getElementAt(lSelFontIdx), Integer.parseInt((String) fontSizes.getElementAt(lSelSizeIdx)), bold, italic);
		}
	}

	/**
	 * Handles the selection change events in the lists
	 */
	private class lsHandler implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();

			if (lsHandler.this.hashCode() == lshFonts.hashCode()) {
				if (!lsm.isSelectionEmpty()) {
					if (lSelSizeIdx == 0) {
						lSelSizeIdx = 10;
					}
					changePreview((String) fontNames.getElementAt(lsm.getMaxSelectionIndex()), Integer.parseInt((String) fontSizes.getElementAt(lSelSizeIdx)), bold, italic);
					lSelFontIdx = lsm.getMaxSelectionIndex();
				}
			} else if (lsHandler.this.hashCode() == lshSizes.hashCode()) {
				if (!lsm.isSelectionEmpty()) {
					changePreview((String) fontNames.getElementAt(lSelFontIdx), Integer.parseInt((String) fontSizes.getElementAt(lsm.getMaxSelectionIndex())), bold, italic);
					lSelSizeIdx = lsm.getMaxSelectionIndex();
				}
			}
		}
	}

	/**
	 * Changes the properties in the font preview box. This method is called
	 * every thime there is a change of the font properties.
	 *
	 * @param fontName The name of the selected font
	 * @param fontSize The new font size
	 * @param bold Is the font bold
	 * @param italic Is the font italic
	 */
	private void changePreview(String fontName, int fontSize, boolean bold, boolean italic) {
		Font previewFont = null;

		fntName = fontName;
		fntSize = fontSize;

		if (!bold && !italic) {
			previewFont = new Font(fontName, Font.PLAIN, fontSize);
		} else if (bold && !italic) {
			previewFont = new Font(fontName, Font.BOLD, fontSize);
		} else if (!bold && italic) {
			previewFont = new Font(fontName, Font.ITALIC, fontSize);
		} else if (bold && italic) {
			previewFont = new Font(fontName, Font.BOLD + Font.ITALIC, fontSize);
		}

		rFont = previewFont;
		previewLabel.setFont(previewFont);
	}

	/**
	 * Return the selected font as string, for example for display in a options dialog.
	 *
	 * @return String, describing the selected font
	 */
	public String getFontAsString(){
		String fontString = "";

		if (!bold && !italic) {
			fontString = fntName + ", " + "Plain" + ", " + fntSize;
		} else if (bold && !italic) {
			fontString = fntName + ", " + "Bold" + ", " + fntSize;
		} else if (!bold && italic) {
			fontString = fntName + ", " + "Italic" + ", " + fntSize;
		} else if (bold && italic) {
			fontString = fntName + ", " + "Bold+Italic" + ", " + fntSize;
		}

		return fontString;
	}

	/**
	 * Sets the current used font, so the user may make changes to it.
	 */
	public void setCurrentFont(){
		
	}
}
