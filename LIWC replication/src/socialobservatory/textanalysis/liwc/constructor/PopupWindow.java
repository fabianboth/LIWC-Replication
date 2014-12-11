package socialobservatory.textanalysis.liwc.constructor;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

public class PopupWindow {

	public PopupWindow(){
		final JFrame parent = new JFrame("Replication");
        parent.setLocationRelativeTo(null);
        parent.setLayout(new BorderLayout());
        parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton();
        
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setText("<html>" + "<p style=\"text-align:center\">"+ "Java: Text analysis finsished!" + "<br>" + "click to proceed" + "</p>"+ "</html>");
        parent.add(button);
        parent.pack();
        parent.setVisible(true);

        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
            }
        });
	}
	
//    public static void main(final String[] args) {
//    	PopupWindow pw = new PopupWindow();
//    }
}