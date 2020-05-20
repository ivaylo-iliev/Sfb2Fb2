package fictionBook;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

public class FbFrame extends JFrame {
    private Dimension maximumSize;
    private Dimension minimumSize;

    public FbFrame(String title, Dimension minimumSize, Dimension maximumSize) {
        super(title);
        this.minimumSize = minimumSize;
        this.maximumSize = maximumSize;
        initialize();
    }

    private void initialize() {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                FbFrame frame  = (FbFrame) e.getSource();
                int     width  = frame.getWidth();
                int     height = frame.getHeight();
                boolean resize = false;

                if (width < minimumSize.getSize().width) {
                    width  = minimumSize.getSize().width;
                    resize = true;
                } else if (width > maximumSize.getSize().width) {
                    width  = maximumSize.getSize().width;
                    resize = true;
                }

                if (height < minimumSize.getSize().height) {
                    height = minimumSize.getSize().height;
                    resize = true;
                } else if (height > maximumSize.getSize().height) {
                    height = maximumSize.getSize().height;
                    resize = true;
                }

                if (resize) {
                    frame.setSize(width, height);
                }
            }
        });
    }
}
