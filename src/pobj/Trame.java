package pobj;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Trame{
	private List<String> trame;
	public Trame(List<String> t) {
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
	
	public String get(int i, int j, String s) {
		StringJoiner sb= new StringJoiner(s);
		for (int indice=0; indice<j; indice++) {
			sb.add(trame.get(indice));
		}
		return sb.toString();
	}
	
	public Trame subTrame(int i) {
		return new Trame(trame.subList(i, trame.size()));
	}
}
