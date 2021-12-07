package pobj;
import java.awt.Dimension;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.StringJoiner;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Affiche dans une Fenêtre les trames et sa descrption
public class GUI {
	JFrame frame = new JFrame();
	JPanel mainPanel = new JPanel();
	JScrollPane contientTrame;
	JScrollPane contientData;
	JTextArea text;
	JTextArea trame;
	
	// L'interface graphique pour afficher la description de la trame
	public GUI(String data, String octets) {
		mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Our GUI");
		
		// Affiche l'analyse de la trame
		text= new JTextArea(data);
		text.setEditable(false);
		contientData = new JScrollPane(text);
		contientData.setPreferredSize(new Dimension(400, 450));
		
		// Affiche les octets de la trame
		trame= new JTextArea(octets);
		trame.setEditable(false);
		contientTrame = new JScrollPane(trame);
		contientTrame.setPreferredSize(new Dimension(500, 500));
		
		// Ajouts des zones de texte dans la fenêtre principale
		mainPanel.add(contientData);
		mainPanel.add(contientTrame);
		
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.pack();
	}
	
	public static void main(String[] args) {		
		String data;
		try {
			List<String> s =Parser.parser("data/trame_DNS.txt");
			Donnees octets= new Donnees(s.get(0));
			Trame t = new Trame(octets);
			data = t.toString();
			//new GUI(data, octets.toString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
