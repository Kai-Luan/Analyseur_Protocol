package pobj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

public class Parser {

	public Parser() {}
	
	public static List<String> parser(String fileName) throws IOException{
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
				br.close();
			}
		}
		//System.out.println(res.toString());	
		return res;
	}

	private static int get_offset(String line) {
		int indice=0;
		for (indice=0; indice < line.length(); indice++)
			if (line.charAt(indice)==' ') break;
		String offset_hexa = line.substring(0, indice);
		return Integer.parseInt(offset_hexa, 16);
	}
	
	private static boolean readLine(Donnees trame, String line, int length) {
		String[] octets = line.split(" ");
		int n = 0;
		for (String octet: octets) {
			if (octets.length==0) continue;
			if (octets.length!=2) continue;
			try {
				Integer.parseInt(octet, 16);
				trame.add(octet);
				n = n + 1;
				if (n < length) break;
			}
			catch (Exception e) {
				continue;
			}
		}
		if (n < length) throw new IllegalArgumentException();
		return true;
	}
}

