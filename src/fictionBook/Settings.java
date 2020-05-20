package fictionBook;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class, for reading and writing a configuration file.
 * 
 * @author Ivailo Iliev
 * @see  Properties
 */
public class Settings {

	private String iniFile;
	private String defaultDir, tempDir, outputDir, labelFont, fieldFont,  themeName, iconTheme;
	private Properties p;

	/**
	 * The constructor of the class.
	 */
	public Settings(){
		p = new Properties();
	}

	/**
	 * Alternate constructor, takes a configuration file as parameter
	 *
	 * @param file The configuration file.
	 */
	public Settings(String file){
		setIniFile(file);
		p = new Properties();
		try {
			readIniFile();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void setIniFile(String file){iniFile = file;}

	/**
	 * Read the configuration file and store its contents in variables.
	 * 
	 * @throws java.io.FileNotFoundException
	 */
	public void readIniFile() throws FileNotFoundException{
		BufferedReader input = new BufferedReader(new FileReader(iniFile));

		try {
			p.load(input);
			setDefaultDir(p.getProperty("DefaultDirectory"));
			setTempDir(p.getProperty("TempDir"));
			setOutputDir(p.getProperty("OutputDir"));
			setLabelFont(p.getProperty("LabelFont"));
			setFieldFont(p.getProperty("FieldFont"));
			setThemeName(p.getProperty("Theme"));
			setIconTheme(p.getProperty("Icons"));
		} catch (IOException ex) {
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
		}
		try {
			input.close();
		} catch (IOException ex) {
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Write the settings to the configuration file
	 *
	 * @throws IOException
	 */
	public void writeIniFile() throws IOException{
		p.setProperty("DefaultDirectory",getDefaultDir());
		p.setProperty("TempDir",getTempDir());
		p.setProperty("OutputDir",getOutputDir());
		p.setProperty("LabelFont", labelFont);
		p.setProperty("FieldFont", fieldFont);
		p.setProperty("Theme", themeName);
		p.setProperty("Icons", iconTheme);

		BufferedWriter out = new BufferedWriter(new FileWriter(iniFile));
		p.store(out, "./");
		out.close();
	}

	public void setThemeName(String tName){themeName = tName;}
	public void setIconTheme(String theme){iconTheme = theme;}
	public void setDefaultDir(String dir){defaultDir = dir;}
	public void setTempDir(String dir){tempDir = dir;}
	public void setOutputDir(String dir){outputDir = dir;}
	public void setLabelFont(String labelFnt){labelFont = labelFnt;}
	public void setFieldFont(String fieldFnt){fieldFont = fieldFnt;}

	public String getThemeName(){return themeName;}
	public String getIconTheme(){return iconTheme;}
	public Font getLabelFont(){return parseFontPropery(labelFont);}
	public String getLabelFontAsString(){return labelFont;}
	public String getFieldFontAsString(){return fieldFont;}
	public Font getFieldFont(){return parseFontPropery(fieldFont);}

	/**
	 * Parse the font properties from the config file. The string, providing the font info is
	 * in the format "FontName, FontStyle(Bold)+FontStyle(Italic), FontSize"
	 *
   * @param font The font configuration string.
   * @return Font, described in the provided string.
	 */
	public Font parseFontPropery(String font){
		Font tFont;
		String[] items = font.split(",");
		
		if(items[1].trim().equals("Bold+Italic")){
			tFont = new Font(items[0].trim(), Font.BOLD+Font.ITALIC, Integer.parseInt(items[2].trim()));
		} else if(items[1].trim().equals("Bold")){
			tFont = new Font(items[0].trim(), Font.BOLD, Integer.parseInt(items[2].trim()));
		} else if(items[1].trim().equals("Italic")){
			tFont = new Font(items[0].trim(), Font.ITALIC, Integer.parseInt(items[2].trim()));
		} else {
			tFont = new Font(items[0].trim(), Font.PLAIN, Integer.parseInt(items[2].trim()));
		}
		
		return tFont;
	}

	/**
	 * @return the defaultDir
	 */
	public String getDefaultDir() {
		return defaultDir;
	}

	/**
	 * @return the tempDir
	 */
	public String getTempDir() {
		return tempDir;
	}

	/**
	 * @return the outputDir
	 */
	public String getOutputDir() {
		return outputDir;
	}

}
