package pobj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
// Classe contenant gère la lecture des fichiers et l'ecriture des fichiers
public class Parser {
	public Parser() {}
	// Lecture de fichier: prend le nom du fichier avec son chemin
	// Retourne une liste de trames non analysée avec leurs octets respectives
	public static List<Donnees> parser(String fileName){
		BufferedReader br = null;
		List<Donnees> list_donnees = new ArrayList<>();
		Donnees donnees = new Donnees();
		boolean isComplet = true;
		try {
			// Lecture du fichier
			br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			if (line ==null ) return list_donnees;
			String[] s_line = line.split(" ");
			String[] s_next;
			String next_line;
			int num_line=1;
			// Recupere 2 lignes du fichiers: un pour l'ajout dans la trame, un pour l'offset
			while((next_line = br.readLine())!=null) {
				// On separe les octets de la ligne par leur espace
				s_next = next_line.split(" ");
				int offset = -1;
				int length = -1;
				// On verifie si on a un offset au debut de la ligne, sinon on ignore la ligne
				try {
					 offset = Integer.parseInt(s_next[0], 16);
					 length= offset - Integer.parseInt(s_line[0], 16);
				}
				catch(Exception e) {
					continue;
				}
				// Si le prochain offset est different de 0, on prend le nombre d'octets indiqué les offsets
				if (offset != 0){
					// On ajoute les octets si la trame n'a pas eu d'erreur: comme une ligne incomplete
					if (isComplet){
						// On ajoute dans la trame, et on verifie si la ligne est complete
						isComplet = readLine(donnees, s_line, length);
						if(!isComplet){
							donnees.setLigne_incomplete(num_line, line);
						}
					}
				}
				// On recupère tous les octets de la ligne, et on initialise une nouvelle trame
				else {
					readLine(donnees, s_line, -1);
					list_donnees.add(donnees);
					donnees = new Donnees();
					isComplet = true;
				}
				line = next_line;
				s_line = s_next;
				num_line++;
			}
			// On ajoute la derniere ligne dans la derniere trame
			if (isComplet)
				readLine(donnees, s_line, -1);
			list_donnees.add(donnees);
		} catch (IOException io) {
			System.out.println("Erreur lors de la lecture du fichier\n");
			io.printStackTrace();
			
		} // Fermeture du fichier de lecture
		finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list_donnees;
	}
	// Ecriture de fichier: prend une liste d'analyse de trames et l'ecrit dans le fichier donné
	public static void parseOut(File file, List<Trame> trames) {
		BufferedWriter writer= null;
		try {
			writer= new BufferedWriter(
							new FileWriter(
									file));
			for (int i=0; i< trames.size(); i++) {
				writer.write("\n=========================================================================\n\n");
				writer.write(String.format("                        Trame  n° %d\n", i+1));
				writer.write("\n=========================================================================\n");
				writer.write(trames.get(i).toString());
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if( writer!=null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	// Lecture d'une ligne du fichier
	// Prend ajoute les octets de la line dans la trame temporaire
	// length: le nombre d'octets à récupérer dans la ligne
	private static boolean readLine(Donnees trame, String[] line, int length) {
		int n = 0;
		line[0]= "";
		// On recupère chaque octet de la ligne
		for (String octet: line) {
			// On verifie si on a le bon format pour l'octet
			if (octet.length()==0) continue;
			if (octet.length()!=2) continue;
			// On vérifie si c'est un octet, si oui, on l'ajoute dans trame
			// Sinon, on l'ignore
			try {
				Integer.parseInt(octet, 16);
			}
			catch (Exception e) {
				continue;
			}
			trame.add(octet);
			n = n + 1;
			// On vérifie si on a pris le nombre suffisant d'octets
			if (length!=-1 && n > length) break;
		}
		// Si on a pris le nombre suffisant d'octets, la ligne est incomplète
		if ( n < length) return false;
		return true;

	}
}

