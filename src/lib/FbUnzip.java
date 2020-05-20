package fictionBook;

import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import net.miginfocom.swing.MigLayout;

public class FbUnzip{

	private String fileName, tempDir;
	private JProgressBar pBar, pBar2;
	private ArrayList<File> files;
	private JPanel pPanel;
	private JFrame pFrame;
	private int flag, curZipFile;
	private ArrayList<String> fList;
	private boolean alive;

	FbUnzip(String fName, String dName){
		alive = false;
		fList = new ArrayList<String>();
		setZipFile(fName);
		setTempDir(dName);
		setFlag(1);
		run();
	}

	FbUnzip(ArrayList<File> fl, String dName){
		alive = false;
		fList = new ArrayList<String>();
		setFiles(fl);
		setTempDir(dName);
		setFlag(2);
		run();
	}

	public ArrayList<String> getFileList(){
		for(String str : fList){
			System.out.println(str);
		}
		return fList;
	}

	public void run(){
		if(this.flag == 1){
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					alive = true;
					createAndRunSingle();
				}
			});
		} else if(this.flag == 2){
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					createAndRunMulti();
				}
			});
		}
	}

	public void setFiles(ArrayList<File> fl){
		files = fl;
	}

	public void setFlag(int value){
		flag = value;
	}

	public void setZipFile(String fName){
		this.fileName = fName;
	}

	public void setTempDir(String tDir){
		if(tDir.equals("")){
			this.tempDir = "./temp/";
		} else {
			this.tempDir = tDir;
		}
	}

	public void resetStatus(){
		pBar.setValue(0);
	}

	private void createAndRunSingle(){
		pFrame = new JFrame();
		MigLayout pMigLayout = new MigLayout("", "grow, fill");

		pBar = new JProgressBar(0, 100);
		pPanel = new JPanel(pMigLayout);
		pPanel.add(pBar, "grow,wmin 300, hmin 20, wrap");

		pFrame.getContentPane().add(pPanel);
		pFrame.pack();
		pFrame.setSize(new Dimension(350, 150));
		pFrame.setLocationRelativeTo(null);
		pFrame.setVisible(true);

		TaskSingle task = new TaskSingle();
		task.execute();
	}

	private void createAndRunMulti(){
		pFrame = new JFrame();
		MigLayout pMigLayout = new MigLayout("", "grow, fill");

		pBar = new JProgressBar(0, 100);
		pPanel = new JPanel(pMigLayout);
		JLabel cProgress = new JLabel("Current file");
		pPanel.add(cProgress, "wrap");
		pPanel.add(pBar, "grow,wmin 300, hmin 20, wrap");

		pBar2 = new JProgressBar(0, 100);
		JLabel allProgres = new JLabel("All files");
		pPanel.add(allProgres, "wrap");
		pPanel.add(pBar2, "grow, wmin 300, hmin 20, wrap");

		pFrame.getContentPane().add(pPanel);
		pFrame.pack();
		pFrame.setSize(new Dimension(350, 150));
		pFrame.setLocationRelativeTo(null);
		pFrame.setVisible(true);

		TaskMulti task = new TaskMulti();
		task.execute();
	}

	private String fileType(String fileName){
		String ext = fileName.substring(fileName.length()-3, fileName.length());
		return ext;
	}

	private void unZip(String file) throws IOException{
		ZipFile zf = new ZipFile(file);
		Enumeration e = zf.entries();
		while(e.hasMoreElements()){
			ZipEntry ze = (ZipEntry) e.nextElement();
			if(ze.isDirectory()){
				new File(tempDir + ze.getName()).mkdir();
				continue;
			}

			copyInputStream(zf.getInputStream(ze), new BufferedOutputStream(new FileOutputStream(tempDir + ze.getName())));

			if(fileType(ze.getName()).equals("zip")){
				unZip(tempDir + ze.getName());
			}
		}
	}

	private void unZip(String file, JProgressBar bar) throws IOException{
		ZipFile zf = new ZipFile(file);
		Enumeration e = zf.entries();
		bar.setValue(0);
		int curFile = 1;
		double newPos = 0;
		while(e.hasMoreElements()){
			ZipEntry ze = (ZipEntry) e.nextElement();
			if(ze.isDirectory()){
				new File(tempDir + ze.getName()).mkdir();
				continue;
			}

			copyInputStream(zf.getInputStream(ze), new BufferedOutputStream(new FileOutputStream(tempDir + ze.getName())));

			if(fileType(ze.getName()).equals("zip")){
				bar.setIndeterminate(true);
				unZip(tempDir + ze.getName());
			} else {
				bar.setIndeterminate(false);
			}

			newPos = ((curFile*100)/countFiles(file));
			bar.setValue((int)newPos);
			curFile++;
		}
	}

	private void alUnZip(String file, JProgressBar bar) throws IOException{
		ZipFile zf = new ZipFile(file);
		Enumeration e = zf.entries();
		bar.setValue(0);
		int curFile = 1;
		double newPos = 0;
		while(e.hasMoreElements()){
			ZipEntry ze = (ZipEntry) e.nextElement();
			if(ze.isDirectory()){
				new File(tempDir + ze.getName()).mkdir();
				continue;
			}

			System.out.println(tempDir + ze.getName());
			fList.add(tempDir + ze.getName());
			copyInputStream(zf.getInputStream(ze), new BufferedOutputStream(new FileOutputStream(tempDir + ze.getName())));

			if(fileType(ze.getName()).equals("zip")){
				bar.setIndeterminate(true);
				unZip(tempDir + ze.getName());
			} else {
				bar.setIndeterminate(false);
			}

			newPos = ((curFile*100)/countFiles(file));
			bar.setValue((int)newPos);
			curFile++;
		}

		//return fileList;
	}

	private void alUnZip(ArrayList<File> fl, JProgressBar bar1, JProgressBar bar2) throws IOException{
		int czFile = 1;
		int newPos2 = 0;
		int zfCount = fl.size();
		String fName;

		for(int i=0;i<fl.size();i++){
			fName = fl.get(i).getAbsolutePath();
			alUnZip(fName, bar1);

			newPos2 = ((czFile*100)/zfCount);
			bar2.setValue(newPos2);
			czFile++;
		}

		//return fileList;
	}

	private void unZip(ArrayList<File> fl, JProgressBar bar1, JProgressBar bar2) throws IOException{
		int czFile = 1;
		int newPos2 = 0;
		int zfCount = fl.size();
		String fName;

		for(int i=0;i<fl.size();i++){
			fName = fl.get(i).getAbsolutePath();
			unZip(fName, bar1);

			newPos2 = ((czFile*100)/zfCount);
			bar2.setValue(newPos2);
			czFile++;
		}
	}
	

	class TaskMulti extends SwingWorker<Void, Void>{

		@Override
		protected Void doInBackground() throws Exception {
			//unZip(files, pBar, pBar2);
			alUnZip(files, pBar, pBar2);

			return null;
		}

		@Override
		public void done(){
			pFrame.setVisible(false);
		}
		
	}

	class TaskSingle extends SwingWorker<Void, Void>{
		@Override
		protected Void doInBackground() throws Exception {
			//unZip(fileName, pBar);
			alUnZip(fileName, pBar);
			return null;
		}

		@Override
		public void done(){
			pFrame.setVisible(false);
		}
	}

	private void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}

		in.close();
		out.close();
	}

	private int countFiles(String fName) throws IOException{
		int count = 0;
		ZipFile zf = new ZipFile(fName);
		Enumeration e = zf.entries();
		while(e.hasMoreElements()){
			e.nextElement();
			count++;
		}
		return count;
	}
}
