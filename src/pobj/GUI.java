package pobj;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

import java.awt.GridLayout;

// Affiche dans une Fenêtre les trames et sa descrption
public class GUI {
	// Interface Graphique
	JFrame frame = new JFrame();
	JPanel boutons = new JPanel(new GridLayout(10, 0));
	JTextArea text, trame, analyse;
	JLabel num_trame;
	JFileChooser filechooser= new JFileChooser();
	// Liste des trames avec leurs descriptions
	List<Trame> trames =  new ArrayList<>();;
	List<Donnees> donnees =  new ArrayList<>();;
	
	public static void main(String[] args) {
		Parser.parserbis("data/message.txt");
		new GUI();
	}
	// Constructeur d'interface graphique
	public GUI() {	
		// Ajout de la fenêtre principale
		JPanel mainPanel = new JPanel(new BorderLayout());
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Analyseur Protocol:");
		// Creation de la barre de  menu
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem loadItem = new JMenuItem("Load");
		JMenuItem saveItem = new JMenuItem("Save");
		// ajout de Load : fonctionnalité
		loadItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				selectFile();
				refreshBoutons();
				trame.setText("");
				analyse.setText("");
				if (trames.size()!=0) saveItem.setEnabled(true);
			}
		});
		// Bouton de sauvegarde
		saveItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		
		saveItem.setEnabled(false);
		// Ajout du menu dans la fenêtre
		fileMenu.add(loadItem);
		fileMenu.add(saveItem);
		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
		
		// Ajout des Boutons dans la zones
		JScrollPane select = new JScrollPane(boutons);
		select.setPreferredSize(new Dimension(200, 500));
		
		// Zone des analyse
		analyse= new JTextArea();
		analyse.setEditable(false);
		JScrollPane contientAnalyse = new JScrollPane(analyse);
		contientAnalyse.setPreferredSize(new Dimension(400, 500));
		
		// Zone des affichages de octets
		trame= new JTextArea();
		trame.setEditable(false);
		JScrollPane contientTrame = new JScrollPane(trame);
		contientTrame.setPreferredSize(new Dimension(350, 400));
		
		// Ajouts des zones de texte dans la fenêtre principale
		mainPanel.add(select, BorderLayout.WEST);
		mainPanel.add(contientAnalyse, BorderLayout.CENTER);
		mainPanel.add(contientTrame, BorderLayout.EAST);
		
		// Ajout Label
		num_trame = new JLabel("Trame  n°  ", SwingConstants.CENTER);
		mainPanel.add(num_trame, BorderLayout.NORTH);
		
		
		// Application des filtres pour fichiers
		filechooser.setFileFilter(new FileNameExtensionFilter("*.txt","txt"));
		filechooser.setAcceptAllFileFilterUsed(false);
		filechooser.setCurrentDirectory(new File("."));
		
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.pack();
	}
	
	// Selection d'un fichier
	private void selectFile() {
		// Choisis le fichier à récupérer
		int response = filechooser.showOpenDialog(null);
		if (response != JFileChooser.APPROVE_OPTION) return;
		File file =filechooser.getSelectedFile();
		frame.setTitle("Analyseur Protocol: "+ file.getName());
		String path = file.getAbsolutePath();
		refreshTrames(path);
	}
	
	private void saveFile() {
		// Choisis le chemin du fichier à sauvegarder
		int response = filechooser.showSaveDialog(null);
		if (response != JFileChooser.APPROVE_OPTION) return;
		File file =filechooser.getSelectedFile();
		Parser.parseOut(file, trames);
	}
	
	// Recalcule les trames du nouveau fichier donné par path
	private void refreshTrames(String path) {
		trames.clear();
		donnees.clear();
		try {
			List<String> listString =Parser.parser(path);
			for (String s: listString) {
				Donnees d = new Donnees(s);
				Trame t = new Trame(d);
				donnees.add(d);
				trames.add(t);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Refait les boutons si on a un nouveau fichier
	private void refreshBoutons() {
		int taille;
		if (trames.size()>=10) taille = trames.size();
		else taille = 10;
		boutons.removeAll();
		boutons.setLayout(new GridLayout(taille, 0));
		for (int i=0; i<trames.size(); i++) {
			JButton button = new JButton(String.format("Trame n° %d", i+1));
			button.addActionListener(new A(i));
			boutons.add(button);
		}
		boutons.revalidate();
		boutons.repaint();
	}
	// Classe qui gère l'évenement si on clique sur le bouton
	// On affiche la trame et sa description dans les zones de textes correspondantes
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
}
