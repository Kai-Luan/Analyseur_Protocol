package pobj;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Donnees {
	List<String> trame = new ArrayList<>();
	int num_ligne_incomplete= -1;
	public String ligne_incomplete;
	
	// Constructeur
	public Donnees(List<String> t) {
		trame.addAll(t);
	}
	// Constructeur
	public Donnees() {
	}
	// Ajoute la ligne incomplete dans les donnees avec le numero de ligne
	public void setLigne_incomplete(int n, String s){
		num_ligne_incomplete= n;
		ligne_incomplete = s;
	}
	// Renvoie true, c'est la trame est incomplete, sinon false
	public boolean isIncomplete(){
		return num_ligne_incomplete!=-1;
	}
	// Ajoute un octet dans la trame
	public boolean add(String octet) {
		try {
			Integer.parseInt(octet, 16);
			return trame.add(octet);
		}
		catch(Exception e) {
			return false;
		}

	}
	// Recupere un octet de la trame à i
	public String get(int i) {
		return trame.get(i);
	}
	// Recupere les octets de la trame entre i et j (exclus)
	public String get(int i, int j) {
		StringBuilder sb= new StringBuilder();
		for (int indice=i; indice<j; indice++) {
			sb.append(trame.get(indice));
		}
		return sb.toString();
	}
	// retourne la valeur entière des octets entre i et j
	public int parseInt(int i, int j) {
		return Integer.parseInt(get(i,j), 16);
	}
	// retourne la valeur entière de l'octet i
	public int parseInt(int i) {
		return Integer.parseInt(get(i), 16);
	}
	// retourne en hexa l'octet i
	public String parseHexa(int i) {
		return "0x"+get(i);
	}
	// retourne en hexa les octets entre i et j
	public String parseHexa(int i, int j) {
		return "0x"+get(i,j);
	}
	// Retourne en bits l'octet i
	public String parseBit(int i) {
		return new BigInteger(trame.get(i), 16).toString(2);
	}
	// retourne en bits les octets de i à j
	public String parseBit(int i, int j) {
		return new BigInteger(get(i,j), 16).toString(2);
	}
	// retourne les octets de i à j séparés d'un séparateur défini en argument
	// Utile pour obtenir avoir une adresse mac
	public String get(int i, int j, String s) {
		StringJoiner sb= new StringJoiner(s);
		for (int indice=i; indice<j; indice++) {
			sb.add(trame.get(indice));
		}
		return sb.toString();
	}
	// retourne l'adresse IP en décimale séparée par des "."
	public String getIP(int i, int j) {
		StringJoiner sb= new StringJoiner(".");
		for (int indice=i; indice<j; indice++) {
			sb.add(String.valueOf(parseInt(indice)));
		}
		return sb.toString();
	}
	// retourne l'adresse IPv6
	public String getIPv6(int i, int j) {
		StringJoiner sb= new StringJoiner(":");
		for (int indice=i; indice<j; indice+=2) {
			String s = get(i, i+2);
			int indice_not_zero=0;
			for (int c =0; c<4; c++) {
				if ((char) s.charAt(c) != '0') break;
				indice_not_zero++;
			}
			if (indice_not_zero!=0) {
				if ( indice_not_zero==4) s= "";
				else s= s.substring(indice_not_zero);
			}
			sb.add(s);
		}
		return sb.toString();
	}
	// retourne une trame avec les octets à partir de i
	public Donnees subDonnees(int i) {
		return new Donnees(trame.subList(i, trame.size()));
	}
	// retourne le nombre d'octets dans la trame
	public int size() {
		return trame.size();
	}
	// Affiche les octets de la trame
	public void affiche() {
		for (int i=0; i<trame.size(); i++) {
			if (i%4==0 && i!=0) System.out.println();
			System.out.print(" " +trame.get(i));
		}
		System.out.println("\n");
	}
	// Affiche les octets de la trame à partir de i
	public void affiche(int i) {
		System.out.println("indice: "+ i);
		for (i+=0; i<trame.size(); i++) {
			if (i%4==0 && i!=0) System.out.println();
			System.out.print(" " +trame.get(i));
		}
		System.out.println("\n");
	}

	// retourne en String, les octets de la trame
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
