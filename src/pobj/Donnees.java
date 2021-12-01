package pobj;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Donnees {
	private List<String> trame;
	public Donnees(List<String> t) {
		trame= new ArrayList<>(t);
	}
	
	public String get(int i) {
		return trame.get(i);
	}
	
	public String get(int i, int j) {
		StringBuilder sb= new StringBuilder();
		for (int indice=0; indice<j; indice++) {
			sb.append(trame.get(indice));
		}
		return sb.toString();
	}
	
	public int parseInt(int i, int j) {
		return Integer.parseInt(get(i,j));
	}
	
	public int parseInt(int i) {
		return Integer.parseInt(get(i));
	}
	
	public String get(int i, int j, String s) {
		StringJoiner sb= new StringJoiner(s);
		for (int indice=0; indice<j; indice++) {
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
}
