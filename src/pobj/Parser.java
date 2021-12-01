package pobj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	public Parser() {}
	
	public static List<String> parser(String fileName) throws IOException{
		BufferedReader br = null;
		StringBuilder sb= new StringBuilder();
		List<String> res =new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while((line=br.readLine())!=null) {
				if (newTrame(line)) {
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
	
	private static boolean newTrame(String line) {
		boolean b = line.charAt(0)=='0';
		b = b && line.charAt(1)=='0';
		b = b && line.charAt(2)=='0';
		return b && line.charAt(3)=='0';
	}
}

