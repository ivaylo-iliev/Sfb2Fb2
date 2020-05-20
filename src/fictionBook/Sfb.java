package fictionBook;

import java.io.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sfb {

	public String bookOriginalLanguage,  authorFName,  authorMName,  authorLName,  authEmail,  authHomePage,  authNName,  bookTitle,  bookKeywords,  bookLanguage,  yearOfPublishing,  yearOfTranslation,  translatorName,  translatorSurname,  translatorCognomen,  translatorNickname,  translatorHomepage,  translatorEmail,  genere,  series,  seriesNo,  publishBookTitle,  publishISBN,  publishSeries,  publishSeriesNo, fileEncoding;
	private String file;
	private ArrayList<String> inStream;
	private String outFile;
	private ArrayList<String> outStream;
	private systemProperties sysProp;

	public Sfb(String fileName, String outFileName, String fEnc) throws FileNotFoundException, IOException {
		file = fileName;
		sysProp = new systemProperties();

		if(fEnc.equals("")){
			String enc = detectEncoding(file);
			if(enc.equals("")){
				fileEncoding = "UTF-8";
			} else {
				fileEncoding = enc;
			}
		} else {
			fileEncoding = fEnc;
		}

		bookOriginalLanguage = "";
		authorFName = "";
		authorMName = "";
		authorLName = "";
		authEmail = "";
		authHomePage = "";
		authNName = "";
		bookTitle = "";
		bookKeywords = "";
		bookLanguage = "";
		yearOfPublishing = "";
		yearOfTranslation = "";
		translatorName = "";
		translatorSurname = "";
		translatorCognomen = "";
		translatorNickname = "";
		translatorHomepage = "";
		translatorEmail = "";
		genere = "";
		series = "";
		seriesNo = "";
		publishBookTitle = "";
		publishISBN = "";
		publishSeries = "";
		publishSeriesNo = "";

		try {
			detectEncoding(file);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Sfb.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Sfb.class.getName()).log(Level.SEVERE, null, ex);
		}

		if (outFileName.equals("")) {
			if (!(new File(sysProp.getUserDir() + sysProp.getFileSeparator() + "Books").exists())) {
				new File(sysProp.getUserDir() + sysProp.getFileSeparator() + "Books").mkdir();
			}
			String str[] = fileName.split("\\" + sysProp.getFileSeparator());
			int lng = str[str.length - 1].length() - 4;
			//String tempDir = "./Books/" + str[str.length - 2] + "/";
			String tempDir = sysProp.getUserDir() + sysProp.getFileSeparator() + "Books" + sysProp.getFileSeparator() + str[str.length - 2] + sysProp.getFileSeparator();
			if (!(new File(tempDir).exists())) {
				new File(tempDir).mkdir();
			}
			outFile = tempDir + str[str.length - 1].substring(0, lng) + ".fb2";			
		} else {
			outFile = outFileName;      
		}

		inStream = new ArrayList<String>();
		outStream = new ArrayList<String>();

		try {
			this.Read();
		} catch (IOException ex) {
			Logger.getLogger(Sfb.class.getName()).log(Level.SEVERE, null, ex);
		}

		parseBookInfo();
	}

  public void printBookInfo(){
    System.out.println("");
    System.out.println("Title      : " + this.bookTitle);
    System.out.println("Author     : " + this.authorFName + " " + this.authorLName);
    System.out.println("Source file: " + this.file);
    System.out.println("Output file: " + this.outFile);
  }

	private String detectEncoding(String fl) throws FileNotFoundException, IOException{
		BufferedReader rd = new BufferedReader(new FileReader(fl));
		String line = null;

		int lCount = 0;
		while(lCount < 1){
			line = rd.readLine();
			lCount++;
		}
		
		String[] tCharset = line.split("\\ ");
		String enc = tCharset[1].substring(0, tCharset[1].length()-1);
		rd.close();
		return enc;
	}

	public boolean Read() throws IOException {
		int lcnt = 0;

		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)), fileEncoding));
			//BufferedReader input = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = input.readLine()) != null) {
				if (lcnt != 0) {
					inStream.add(line);
					lcnt++;
				} else {
					lcnt++;
				}
			}

			input.close();

			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	private boolean regExMatch(String pattern, String srchString) {
		Pattern ptrn = Pattern.compile(pattern);
		Matcher match = ptrn.matcher(srchString);

		return match.find();
	}

	private String regExReplace(String pattern, String replace, String srchString) {
		Pattern ptrn = Pattern.compile(pattern);
		Matcher match = ptrn.matcher(srchString);

		return match.replaceAll(replace);
	}

	public void parseBookInfo() {
		
		if (!new File(file.substring(0, file.length() - 3) + "fbi").exists()) {
			
			int cntr = 0;

			for (int i = 0; i < inStream.size(); i++) {
				if (regExMatch("^\\|\\t", inStream.get(i))) {
					if (cntr == 0) {
						String authFullName = regExReplace("^\\|\\t", "", inStream.get(i));
						String[] authNameElements = authFullName.split(" ");

						authorFName = authNameElements[0];
						authorLName = authNameElements[1];
						cntr++;
					} else if (cntr == 1) {
						bookTitle = regExReplace("^\\|\\t", "", inStream.get(i));
					}
				}
			}
		} else {
			
			try {
				fbiParser(file.substring(0, file.length() - 3) + "fbi");
			} catch (IOException ex) {
				Logger.getLogger(Sfb.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private boolean fbiParser(String fbiFile) throws IOException {
		ArrayList<String> fbiInfo = new ArrayList<String>();

		boolean author = false;
		boolean title = false;
		boolean language = false;
		boolean ori_language = false;
		boolean translator = false;
		boolean seriesf = false;
		boolean keywords = false;
		boolean oriTitle = false;
		boolean oriSeriesf = false;

		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fbiFile)), fileEncoding));
						
			String line = null;
			
			while ((line = input.readLine()) != null) {
				fbiInfo.add(line);
			}

			for(String str : fbiInfo){
				if(!author){
					if(regExMatch("\\|Автор", str)){
						String[] t1 = str.split("=");
						String[] t2 = t1[1].split(" ");
						authorFName = t2[1].trim();
						authorLName = t2[2].trim();
						author = true;
					}
				} else if(!title){
					if(regExMatch("\\|Заглавие", str)){
						String[] t1 = str.split("=");
						bookTitle = t1[1].trim();
						title = true;
					}
				} else if(!language){
					if(regExMatch("\\|Език", str)){
						String[] t1 =  str.split("=");
						bookLanguage = t1[1].trim();
						language = true;
					}
				} else if(!ori_language){
					if(regExMatch("\\|Ориг.език", str)){
						String[] t1 =  str.split("=");
						bookOriginalLanguage = t1[1].trim();
						ori_language = true;
					}
				} else if(!translator){
					if(regExMatch("\\|Преводач", str)){
						String[] t1 = str.split("=");
						String[] t2 = t1[1].split(" ");
						translatorName = t2[1].trim();
						translatorCognomen = t2[2].trim();
						translator = true;
					}
				} else if(!seriesf){
					if(regExMatch("\\|Поредица", str)){
						String[] t1 = str.split("=");
						if(regExMatch("\\[[0-9]+\\]", t1[1])){
							series = t1[1].substring(0, t1[1].indexOf("["));
							series = series.trim();
							seriesNo = t1[1].substring(t1[1].indexOf("[") + 1, t1[1].length() -1);
							seriesNo = seriesNo.trim();
						} else {
							series = t1[1].trim();
						}
						seriesf = true;
					}
				} else if(!keywords){
					if(regExMatch("\\|Ключови-думи", str)){
						String[] t1 = str.split("=");
						bookKeywords = t1[1].trim();
						keywords = true;
					}
				} else if(!oriTitle){
					if(regExMatch("\\|Заглавие", str)){
						String[] t1 = str.split("=");
						publishBookTitle = t1[1].trim();
						oriTitle = true;
					}
				} else if(!oriSeriesf){
					if(regExMatch("\\|Поредица", str)){
						String[] t1 = str.split("=");
						if(regExMatch("\\[[0-9]+\\]", t1[1])){
							publishSeries = t1[1].substring(0, t1[1].indexOf("["));
							publishSeries = publishSeries.trim();
							publishSeriesNo = t1[1].substring(t1[1].indexOf("[") + 1, t1[1].length() -1);
							publishSeriesNo = seriesNo.trim();
						} else {
							publishSeries = t1[1].trim();
						}
						oriSeriesf = true;
					}
				}
			}

			input.close();

			

			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	public void convert() {
		int cntr = 0;
		int titleCount = 1;
		int sectionCount = 0;
		String tmpString;

		boolean emphasisOpen = false;

		for (int i = 0; i < inStream.size(); i++) {
			inStream.set(i, regExReplace("\\„", "\\\"", inStream.get(i)));
			inStream.set(i, regExReplace("\\“", "\\\"", inStream.get(i)));
			inStream.set(i, regExReplace("\\ __", "\\ \\<strong\\>", inStream.get(i)));
			inStream.set(i, regExReplace("__\\ ", "\\</strong\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("^\\t\\__", "\\<p\\>\\<strong\\>", inStream.get(i)));
			inStream.set(i, regExReplace("__\\.", "\\</strong\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("__\\,", "\\</strong\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("__$", "\\</strong\\>", inStream.get(i)));
			inStream.set(i, regExReplace("__\\.$", "\\</strong\\>", inStream.get(i)));
			inStream.set(i, regExReplace("__\\!", "\\</strong\\>\\!\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("__\\?", "\\</strong\\>\\?\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("__\\]", "\\</strong\\>\\]\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("\\[__", "\\[\\</strong\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("__\\ ", "\\</strong\\>", inStream.get(i)));
			inStream.set(i, regExReplace("__\\ —", "\\</strong\\>\\ -", inStream.get(i)));
			inStream.set(i, regExReplace("__\\*", "\\</strong\\>\\*", inStream.get(i)));
			inStream.set(i, regExReplace("__\\:", "\\</strong\\>\\:", inStream.get(i)));

			if(regExMatch("\\</strong\\>", inStream.get(i)) && !regExMatch("\\<strong\\>", inStream.get(i))){
				System.out.println("Incomplete </strong> tag on line " + i + " of file " + file + ". Fixing by placing opening <strong> tag.");
				inStream.set(i, regExReplace("\\</strong\\>", "\\<strong\\>\\</strong\\>\\ ", inStream.get(i)));
			}

			if(regExMatch("\\<strong\\>", inStream.get(i)) && !regExMatch("\\</strong\\>", inStream.get(i))){
				System.out.println("Incomplete <strong> tag on line " + i + " of file " + file + ". Fixing by placing closing </strong> tag.");
				inStream.set(i, regExReplace("\\<strong\\>", "\\<strong\\>\\</strong\\>\\ ", inStream.get(i)));
			}

			inStream.set(i, regExReplace("\\ _", "\\ \\<emphasis\\>", inStream.get(i)));
			inStream.set(i, regExReplace("\\\"_[^\\!]", "\\\"\\<emphasis\\>", inStream.get(i)));
			inStream.set(i, regExReplace("\\-_", "\\-\\<emphasis\\>", inStream.get(i)));
			inStream.set(i, regExReplace("\\(_", "\\(\\<emphasis\\>", inStream.get(i)));

			inStream.set(i, regExReplace("_\\ ", "\\</emphasis\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_\\)", "\\</emphasis\\>\\)", inStream.get(i)));
			inStream.set(i, regExReplace("_\\…", "\\</emphasis\\>\\.\\.\\.", inStream.get(i)));
			inStream.set(i, regExReplace("_\\\"", "\\</emphasis\\>\\\"", inStream.get(i)));
			inStream.set(i, regExReplace("^\\t\\_", "\\<p\\>\\<emphasis\\>", inStream.get(i)));
			inStream.set(i, regExReplace("\\]\\_", "\\[\\<emphasis\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_\\.", "\\</emphasis\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_\\,", "\\</emphasis\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_$", "\\</emphasis\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_\\.$", "\\</emphasis\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_\\!", "\\</emphasis\\>\\!\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("\\\"_\\!", "\\\"\\</emphasis\\>\\!\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_\\?", "\\</emphasis\\>\\?\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_\\]", "\\</emphasis\\>\\]\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_\\ ", "\\</emphasis\\>\\ ", inStream.get(i)));
			inStream.set(i, regExReplace("_\\ —", "\\</emphasis\\>\\ -", inStream.get(i)));
			inStream.set(i, regExReplace("_\\*", "\\</emphasis\\>\\*", inStream.get(i)));
			inStream.set(i, regExReplace("_\\:", "\\</emphasis\\>\\:", inStream.get(i)));

			if(regExMatch("\\</emphasis\\>", inStream.get(i)) && !regExMatch("\\<emphasis\\>", inStream.get(i))){
				System.out.println("Incomplete </emphasis> tag on line " + i + " of file " + file + ". Fixing by placing opening <emphasis> tag.");
				inStream.set(i, regExReplace("\\</emphasis\\>", "\\<emphasis\\>\\</emphasis\\>\\ ", inStream.get(i)));
			}

			if(regExMatch("\\<emphasis\\>", inStream.get(i)) && !regExMatch("\\</emphasis\\>", inStream.get(i))){
				System.out.println("Incomplete <emphasis> tag on line " + i + " of file " + file + ". Fixing by placing closing </emphasis> tag.");
				inStream.set(i, regExReplace("\\<emphasis\\>", "\\<emphasis\\>\\</emphasis\\>\\ ", inStream.get(i)));
			}

			if (regExMatch("^\\|\\t", inStream.get(i))) {
				if (cntr == 0) {
					outStream.add("<title>");
				}

				tmpString = regExReplace("^\\|\\t", "<p>", inStream.get(i));
				tmpString = regExReplace("$", "</p>", tmpString);
				outStream.add(tmpString);
				cntr++;

				if ((cntr != 0) && !regExMatch("^\\|\\t", inStream.get(i + 1))) {
					outStream.add("</title>");
					cntr = 0;
				}
			} else if (regExMatch("^\\^$", inStream.get(i))) {
				outStream.add(regExReplace("^\\^$", "<empty-line/>", inStream.get(i)));
			} else if (regExMatch("^\\@\\t", inStream.get(i))) {
				outStream.add(regExReplace("^\\@\\t", "", inStream.get(i)));
			} else if (regExMatch("^A>$", inStream.get(i))) {
				outStream.add("<annotation>");
			} else if (regExMatch("^A\\$$", inStream.get(i))) {
				outStream.add("</annotation><p></p>");
			} else if (regExMatch("^E>$", inStream.get(i))) {
				outStream.add("<empty-line/><epigraph>");
			} else if (regExMatch("^E\\$$", inStream.get(i))) {
				outStream.add("</epigraph><empty-line/>");
			} else if (regExMatch("^D>$", inStream.get(i))) {
				outStream.add("<empty-line/><epigraph>");
			} else if (regExMatch("^D\\$$", inStream.get(i))) {
				outStream.add("</epigraph><empty-line/>");
			} else if (regExMatch("^S>$", inStream.get(i))) {
				outStream.add("<empty-line/><epigraph>");
			} else if (regExMatch("^S\\$$", inStream.get(i))) {
				outStream.add("</epigraph><empty-line/>");
			} else if (regExMatch("^>+\\t", inStream.get(i))) {
				if (cntr == 0) {
					outStream.add("<section id=\"" + regExReplace("^>+\\t", "", inStream.get(i)) + "\"><title>");
				}

				tmpString = regExReplace("^>+\\t", "<p>", inStream.get(i));
				tmpString = regExReplace("$", "</p>", tmpString);
				outStream.add(tmpString);
				cntr++;

				if ((cntr != 0) && !regExMatch("^>+\\t", inStream.get(i + 1))) {
					outStream.add("</title></section>");
					cntr = 0;
				}
			} else if (regExMatch("^>$", inStream.get(i))) {
				if (sectionCount == 0) {
					outStream.add("<section id=\"Глава " + titleCount + "\">");
				} else {
					outStream.add("</section><section id=\"Глава" + titleCount + "\">");
				}

				outStream.add("<title><p>Глава " + titleCount + "</p></title>");
				titleCount++;
				sectionCount++;
			} else if (regExMatch("^>>\t$", inStream.get(i))) {
				outStream.add("<empty-line/>");
			} else if (regExMatch("^\\#\\t", inStream.get(i))) {
				outStream.add(regExReplace("^\\#\\t", "<p>", inStream.get(i)) + "</p>");
			} else if (regExMatch("^P>$", inStream.get(i))) {
				outStream.add("<p></p><poem>");
			} else if (regExMatch("^P\\$$", inStream.get(i))) {
				outStream.add("</poem><p></p>");
			} else if (regExMatch("^L>$", inStream.get(i))) {
				outStream.add("<empty-line/><cite>");
			} else if (regExMatch("^L\\$$", inStream.get(i))) {
				outStream.add("</cite><empty-line/>");
			} else if (regExMatch("^C>$", inStream.get(i))) {
				outStream.add("<p></p><cite>");
			} else if (regExMatch("^C\\$$", inStream.get(i))) {
				outStream.add("</cite><p></p>");
			} else if (inStream.get(i).length() == 0) {
				outStream.add("<empty-line/>");
			} else if ((inStream.get(i).length() > 0) && !regExMatch("^I>$", inStream.get(i)) && !regExMatch("^I\\$$", inStream.get(i))) {
				tmpString = regExReplace("^\\t", "<p>", inStream.get(i));
				tmpString = regExReplace("$", "</p>", tmpString);
				outStream.add(tmpString);
			} else if (!regExMatch("^I>$", inStream.get(i))) {
				outStream.add("</section>");
			}

			
			
		}
	}

	public void prepare() {

		if (seriesNo == null) {
			seriesNo = "0";
		}

		// parseBookInfo();
		outStream.add("<?xml version=\"1.0\" encoding=\"" + fileEncoding + "\"?>");
		outStream.add("<FictionBook xmlns=\"http://www.gribuser.ru/xml/fictionbook/2.0\" xmlns:l=\"http://www.w3.org/1999/xlink\">");
		outStream.add("<description>");
		outStream.add("<title-info>");
		outStream.add("<genere>" + genere + "</genere>");

		outStream.add("<author><first-name>" + authorFName + "</first-name>" + "<middle-name>" + authorMName + "</middle-name>" + "<last-name>" + authorLName + "</last-name>" + "<nickname>" + authNName + "</nickname>" + "<home-page>" + authHomePage + "</home-page>" + "<email>" + authEmail + "</email></author>");

		outStream.add("<book-title>" + bookTitle + "</book-title>");
		outStream.add("<keywords>" + bookKeywords + "</keywords>");
		outStream.add("<translator><first-name>" + translatorName + "</first-name>" + "<middle-name>" + translatorSurname + "</middle-name>" + "<last-name>" + translatorCognomen + "</last-name>" + "<nickname>" + translatorNickname + "</nickname>" + "<home-page>" + translatorHomepage + "</home-page>" + "<email>" + translatorEmail + "</email></translator>");

		outStream.add("<sequence name=\"" + series + "\" number=\"" + seriesNo + "\"/>");

		outStream.add("<lang>" + bookLanguage + "</lang>");
		outStream.add("</title-info>");

		outStream.add("<src-title-info>");
		outStream.add("<genere>" + genere + "</genere>");
		outStream.add("<src-lang>" + bookOriginalLanguage + "</src-lang>");
		outStream.add("<book-title>" + publishBookTitle + "</book-title>");
		outStream.add("<sequence name=\"" + publishSeries + "\" number=\"" + publishSeriesNo + "\"/>");
		outStream.add("</src-title-info>");

		outStream.add("<publish-info>");
		outStream.add("<isbn>" + publishISBN + "</isbn>");
		outStream.add("<year>" + yearOfPublishing + "</year>");
		outStream.add("</publish-info>");

		outStream.add("</description>");
		outStream.add("<body>");
		convert();
		outStream.add("</body>");
		outStream.add("</FictionBook>");

	//finish();
	}

	public boolean finish() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

			for (int i = 0; i < outStream.size(); i++) {
				out.write(outStream.get(i));
			}

			out.close();

			// cleanupObject();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
