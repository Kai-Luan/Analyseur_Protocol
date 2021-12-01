package pobj;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Donnees {
	private List<String> trame;
	public Donnees(String t) {
		trame= new ArrayList<>();
		for (String s : t.split(" "))
			trame.add(s);
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
	
	public String get(int i, int j, String s) {
		StringJoiner sb= new StringJoiner(s);
		for (int indice=i; indice<j; indice++) {
			sb.add(trame.get(indice));
		}
		return sb.toString();
	}
	
	public Donnees subDonnees(int i) {
		return new Donnees(trame.subList(i, trame.size()));
	}
	
	public Donnees subDonnees(int i, int j) {
		return new Donnees(trame.subList(i, j));
	}
	
	public int size() {
		return trame.size();
	}
	
	public void affiche() {
		for (int i=0; i<trame.size(); i++) {
			if (i%4==0 && i!=0) System.out.println();
			System.out.print(" " +trame.get(i));
		}
	}
}
