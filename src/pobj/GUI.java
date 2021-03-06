package pobj;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
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

// Affiche dans une FenĂȘtre les list_trame et sa descrption
public class GUI {
	// Interface Graphique
	JFrame frame = new JFrame();
	JPanel boutons = new JPanel(new GridLayout(10, 0));
	JTextArea text, trame, analyse;
	JLabel num_trame;
	JFileChooser filechooser= new JFileChooser();
	// Liste des list_trame avec leurs descriptions
	List<Trame> list_trame =  new ArrayList<>();
	List<Donnees> list_donnees =  new ArrayList<>();
	// Main:
	public static void main(String[] args) {
		new GUI();
	}
	// Constructeur d'interface graphique
	public GUI() {	
		// Ajout de la fenĂȘtre principale
		JPanel mainPanel = new JPanel(new BorderLayout());
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Analyseur Protocol:");
		// Creation de la barre de  menu
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem loadItem = new JMenuItem("Load");
		JMenuItem saveItem = new JMenuItem("Save");
		// ajout de Load : fonctionnalitĂ©
		loadItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				selectFile();
				refreshBoutons();
				trame.setText("");
				analyse.setText("");
				if (list_trame.size()!=0) saveItem.setEnabled(true);
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
		// Ajout du menu dans la fenĂȘtre
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
		
		// Ajouts des zones de texte dans la fenĂȘtre principale
		mainPanel.add(select, BorderLayout.WEST);
		mainPanel.add(contientAnalyse, BorderLayout.CENTER);
		mainPanel.add(contientTrame, BorderLayout.EAST);
		
		// Ajout Label
		num_trame = new JLabel("Trame  ", SwingConstants.CENTER);
		mainPanel.add(num_trame, BorderLayout.NORTH);		
		
		// Application des filtres pour fichiers
		filechooser.setFileFilter(new FileNameExtensionFilter("*.txt","txt"));
		filechooser.setAcceptAllFileFilterUsed(false);
		try {
			filechooser.setCurrentDirectory(new File("."));
		}
		catch(Exception e){	
		}
		
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.pack();
	}
	
	// Selection d'un fichier
	private void selectFile() {
		// Choisis le fichier Ă  rĂ©cupĂ©rer
		int response = filechooser.showOpenDialog(null);
		if (response != JFileChooser.APPROVE_OPTION) return;
		File file =filechooser.getSelectedFile();
		frame.setTitle("Analyseur Protocol: "+ file.getName());
		String path = file.getAbsolutePath();
		refreshlist_trame(path);
	}
	
	private void saveFile() {
		// Choisis le chemin du fichier Ă  sauvegarder
		int response = filechooser.showSaveDialog(null);
		if (response != JFileChooser.APPROVE_OPTION) return;
		File file =filechooser.getSelectedFile();
		Parser.parseOut(file, list_trame);
	}
	
	// Recalcule les list_trame du nouveau fichier donnĂ© par path
	private void refreshlist_trame(String path) {
		list_trame.clear();
		list_donnees.clear();
		list_donnees =Parser.parser(path);
		for (Donnees d: list_donnees) {
			try {
				Trame t = new Trame(d);
				list_trame.add(t);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Refait les boutons si on a un nouveau fichier
	private void refreshBoutons() {
		int taille;
		if (list_trame.size()>=10) taille = list_trame.size();
		else taille = 10;
		boutons.removeAll();
		boutons.setLayout(new GridLayout(taille, 0));
		for (int i=0; i<list_trame.size(); i++) {
			JButton button = new JButton(String.format("Trame  %d", i+1));
			button.addActionListener(new A(i));
			boutons.add(button);
		}
		boutons.revalidate();
		boutons.repaint();
	}
	// Classe qui gĂšre l'Ă©venement si on clique sur le bouton
	// On affiche la trame et sa description dans les zones de textes correspondantes
	private class A implements ActionListener{
		int indice;
		public A(int i) {
			indice = i;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			analyse.setText(list_trame.get(indice).toString());
			trame.setText(list_donnees.get(indice).toString());
			num_trame.setText(String.format("Trame  %d ", indice+1));
		}
	}
}
