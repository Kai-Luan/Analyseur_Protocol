package pobj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	public Parser() {}
	public static List<Donnees> parser(String fileName){
		BufferedReader br = null;
		List<Donnees> list_donnees = new ArrayList<>();
		Donnees donnees = new Donnees();
		boolean isComplet = true;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			if (line ==null ) return list_donnees;
			String[] s_line = line.split(" ");
			String[] s_next;
			String next_line;
			int num_line=1;
			while((next_line = br.readLine())!=null) {
				s_next = next_line.split(" ");
				int offset = -1;
				int length = -1;
				// On verifie si on a un offset
				try {
					 offset = Integer.parseInt(s_next[0], 16);
					 length= offset - Integer.parseInt(s_line[0], 16);
				}
				catch(Exception e) {
					continue;
				}
				if (offset != 0){
					if (isComplet){
						isComplet = readLine(donnees, s_line, length);
						if(!isComplet){
							donnees.setLigne_incomplete(num_line, line);
						}
					}
				}
				else {
					readLine(donnees, s_line, -1);
					list_donnees.add(donnees);
					System.out.println("New: " + num_line+"\n line: "+ line);
					donnees = new Donnees();
					isComplet = true;
				}
				line = next_line;
				s_line = s_next;
				num_line++;
			}
			if (isComplet)
				readLine(donnees, s_line, -1);
			list_donnees.add(donnees);
		} catch (IOException io) {
			System.out.println("Erreur lors de la lecture du fichier\n");
			io.printStackTrace();
			
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println(res.toString());	
		return list_donnees;
	}
	
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
		for (String octet: line) {
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
			if (length==-1) continue;
			if (n > length) break;
		}
		System.out.println();
		if ( n < length) return false;
		return true;

	}
}

