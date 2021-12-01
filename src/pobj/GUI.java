package pobj;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI {
	public GUI() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(200, 200, 200, 200));

		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Our GUI");
		
		JLabel label= new JLabel();
		label.setText("Je suis la");
		
		panel.add(label);
		
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.pack();

	}
	
	public static void main(String[] args) {
		new GUI();
	}
}
