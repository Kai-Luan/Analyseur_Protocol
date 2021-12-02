package pobj;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Donnees {
	private List<String> trame;
	public Donnees(String t) {
		trame= new ArrayList<>();
		for (String s : t.split(" "))
			try {
				if (Integer.parseInt(s, 16) <= 255)
				trame.add(s);
			}
			catch (Exception e) {
				continue;
		}
	}
	
	public Donnees(List<String> t) {
		trame= new ArrayList<>(t);
	}
	
	public String get(int i) {
		return trame.get(i);
	}
	
	public String get(int i, int j) {
		StringBuilder sb= new StringBuilder();
		for (int indice=i; indice<j; indice++) {
			sb.append(trame.get(indice));
		}
		return sb.toString();
	}
	
	public int parseInt(int i, int j) {
		return Integer.parseInt(get(i,j), 16);
	}
	
	public int parseInt(int i) {
		return Integer.parseInt(get(i), 16);
	}
	
	public String parseHexa(int i) {
		return "0x"+get(i);
	}
	
	public String parseHexa(int i, int j) {
		return "0x"+get(i,j);
	}
	
	
	
	public String get(int i, int j, String s) {
		StringJoiner sb= new StringJoiner(s);
		for (int indice=i; indice<j; indice++) {
			sb.add(trame.get(indice));
		}
		return sb.toString();
	}
	
	public String getIP(int i, int j) {
		StringJoiner sb= new StringJoiner(".");
		for (int indice=i; indice<j; indice++) {
			sb.add(String.valueOf(parseInt(indice)));
		}
		return sb.toString();
	}
	
	public Donnees subDonnees(int i) {
		return new Donnees(trame.subList(i, trame.size()));
	}
	
	public int size() {
		return trame.size();
	}
	
	public void affiche() {
		for (int i=0; i<trame.size(); i++) {
			if (i%4==0 && i!=0) System.out.println();
			System.out.print(" " +trame.get(i));
		}
		System.out.println("\n");
	}
	
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner(" ", "Trame: \n ","");
		for (int i=0; i<trame.size(); i++) {
			if (i%16==0 && i!=0) sb.add("\n");
			sb.add(trame.get(i));
		}
		return sb.toString();
		}
}
