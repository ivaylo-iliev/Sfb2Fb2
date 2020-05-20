import fictionBook.Fb2BatchConvGui;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class fb2Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Fb2BatchConvGui gui;
		try {
			gui = new Fb2BatchConvGui();
			gui.setVisible(true);
		} catch (IOException ex) {
			Logger.getLogger(fb2Test.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
