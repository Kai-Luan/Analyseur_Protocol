package pobj;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

import java.awt.GridLayout;

// Affiche dans une Fenêtre les trames et sa descrption
public class GUI {
	JFrame frame = new JFrame();
	JPanel mainPanel = new JPanel(new BorderLayout());
	JScrollPane contientTrame;
	JScrollPane contientAnalyse;
	JScrollPane select;
	JTextArea text;
	JTextArea trame;
	JTextArea analyse;
	JLabel num_trame;
	List<Trame> trames =  new ArrayList<>();;
	List<Donnees> donnees =  new ArrayList<>();;
	int i=0;
	
	public GUI() {
		String data;
		try {
			List<String> listString =Parser.parser("data/message.txt");
			for (String s: listString) {
				Donnees d = new Donnees(s);
				Trame t = new Trame(d);
				donnees.add(d);
				trames.add(t);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Analyseur Protocol");
		
		// Boutons
		JPanel boutons = new JPanel(new GridLayout(trames.size(), 0));
		for (i=0; i<trames.size(); i++) {
			JButton button = new JButton(String.format("Trame n° %d", i+1));
			button.addActionListener(new A(i));
			boutons.add(button);
		}
		// Ajout des Boutons dans la zones
		select = new JScrollPane(boutons);
		select.setPreferredSize(new Dimension(400, 450));
		
		// Zone des affichages de octets
		trame= new JTextArea();
		trame.setEditable(false);
		contientTrame = new JScrollPane(trame);
		contientTrame.setPreferredSize(new Dimension(500, 500));
		
		// Zone des analyse
		analyse= new JTextArea();
		analyse.setEditable(false);
		contientAnalyse = new JScrollPane(analyse);
		contientAnalyse.setPreferredSize(new Dimension(500, 500));
		
		// Ajouts des zones de texte dans la fenêtre principale
		mainPanel.add(select, BorderLayout.WEST);
		mainPanel.add(contientAnalyse, BorderLayout.CENTER);
		mainPanel.add(contientTrame, BorderLayout.EAST);
		
		// Ajout Label
		num_trame = new JLabel("Trame  n°  ", SwingConstants.CENTER);
		mainPanel.add(num_trame, BorderLayout.NORTH);
		
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.pack();
	}
	
	private class A implements ActionListener{
		int indice;
		public A(int i) {
			indice = i;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			analyse.setText(trames.get(indice).toString());
			trame.setText(donnees.get(indice).toString());
			num_trame.setText(String.format("Trame  n° %d ", indice+1));
		}
	}
	
	public static void main(String[] args) {		
		new GUI();
	}
}
