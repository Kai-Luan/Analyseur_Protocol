package pobj;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class GUI {
	JFrame frame = new JFrame();
	JPanel panel = new JPanel();
	JLabel label= new JLabel();
	JTextArea text;
	JTextArea trame;
	
	public GUI(String res, String data) {
		panel.setBorder(BorderFactory.createEmptyBorder(200, 200, 200, 200));

		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Our GUI");
		

		label.setText("Bonjour");
		panel.add(label);
		
		text= new JTextArea(res);
		text.setEditable(false);
		panel.add(text);
		
		
		trame= new JTextArea(data);
		trame.setEditable(false);
		panel.add(trame);
		
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.pack();
	}
	
	public static void main(String[] args) {
		String res;
		try {
			List<String> s =Parser.parser("data/trame2.txt");
			Donnees trame = new Donnees(s.get(0));
			Trame t = new Trame(trame);
			res = t.toString();
			new GUI(res, trame.toString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
