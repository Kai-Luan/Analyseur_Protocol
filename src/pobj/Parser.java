package pobj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

public class Parser {

	public Parser() {}
	
	public static List<String> parser(String fileName){
		BufferedReader br = null;
		List<Donnees> list_trame = new ArrayList<>();
		StringBuilder sb= new StringBuilder();
		List<String> res =new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while((line=br.readLine())!=null) {
				if (line.length()==0) break;
				if (get_offset(line)==0) {
					if (sb.length()!=0) {
						res.add(sb.toString());
						sb.setLength(0);
					}
				}
				sb.append(line.substring(7));
				sb.append(" ");
			}
			res.add(sb.toString());
		} catch (IOException io) {
			System.out.println("Erreur lors de la lecture du fichier\n");
			io.printStackTrace();
			
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//System.out.println(res.toString());	
		return res;
	}
	
	public static List<Donnees> parserbis(String fileName){
		BufferedReader br = null;
		List<Donnees> list_donnees = new ArrayList<>();
		Donnees donnees = new Donnees();
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			if (line ==null ) return list_donnees;
			String[] s_line = line.split(" ");
			String[] s_next;
			String next_line;
			while((next_line = br.readLine())!=null) {
				s_next = next_line.split(" ");
				int length = -1;
				// On vérifie si on a un offset
				try {
					 length = Integer.parseInt(s_next[0]);
				}
				catch(Exception e) {
					continue;
				}
				if (length != 0) readLine(donnees, s_line, length);
				else {
					readLine(donnees, s_line, -1);
					list_donnees.add(donnees);
					donnees = new Donnees();
				}
				s_line = s_next;
			}
			
		} catch (IOException io) {
			System.out.println("Erreur lors de la lecture du fichier\n");
			io.printStackTrace();
			
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
			if (n < length) break;
		}
		if ( n < length) return false;
		return true;
	}
	
	private static int get_offset(String line) {
		int indice=0;
		for (indice=0; indice < line.length(); indice++)
			if (line.charAt(indice)==' ') break;
		String offset_hexa = line.substring(0, indice);
		return Integer.parseInt(offset_hexa, 16);
	}
}

