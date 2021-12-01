package pobj.champs;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import pobj.Donnees;

public class IP {
	public int version, IHL, tos, totalLength;
	int identifier;
	int[] flags;
	int offset;
	int ttl, protocol;
	String checksum;
	String src, dest;
	List<String> listOption;
	
	public IP(Donnees trame) {
		// A partir de l'octet n°0
		String s1= trame.get(0);
		version= Integer.parseInt(String.valueOf(s1.charAt(0)));
		IHL= Integer.parseInt(String.valueOf(s1.charAt(1)));
		tos= trame.parseInt(1);
		totalLength= trame.parseInt(2,4);
		// A partir de l'octet n°4
		identifier= trame.parseInt(4,6);
		flags= calculeFlags(trame);
		offset= calculeOffset(trame);
		// A partir de l'octet n°8
		ttl= trame.parseInt(8);
		protocol= calculeProtocol(trame);
		checksum= trame.get(10,12);
		// A partir de l'octet n°12
		src= calculeSrc(trame);
		// A partir de l'octet n°16
		dest= calculeDest(trame);
		// A partir de l'octet n°20
		listOption = calculeOption(trame);
	}
	
	String calculeSrc(Donnees trame) {;
		return calculeIp(trame.get(12,16,":"));
	}
	
	
	public String calculeDest(Donnees trame) {
		return calculeIp(trame.get(16,20,":"));
	}
	
	// Renvoie l'addresse ip sous format décimale 
	// avec "." comme séparateur
	private String calculeIp(String s) {
		StringJoiner sb= new StringJoiner(".");
		String[] tab= s.split(":");
		for (String val : tab) {
			sb.add(String.valueOf(Integer.parseInt(val, 16)));
		}
		return sb.toString();
	}
	
	// Calcule les valeurs du flags
	private int[] calculeFlags(Donnees trame) {
		int i = trame.parseInt(6);
		int[] res= new int[3];
		res[0]= 0;
		if (i>=128) throw new IllegalArgumentException("Fichier invalide");
		if (i>=64) res[1]=1;
		else res[1]=0;
		if (i-64*res[1] >=32) res[2] = 1;
		else res[2] = 0;
		return res;
	}
	
	// Calcule la valeur de offset selon le flags
	private int calculeOffset(Donnees trame) {
		int i = trame.parseInt(6,8);
		return (i - 64*flags[1] - 32*flags[2]);
	}
	
	// Calcule le protocol
	private int calculeProtocol(Donnees trame) {
		int i = trame.parseInt(9);
		if (i!=17) throw new IllegalArgumentException("IP: mauvais protocol");
		return i;
	}
	
	// Calcule la liste des options dans l'entete IP
	private List<String> calculeOption(Donnees trame) {
		List<String> res = new ArrayList<>();
		
		return res;
	}
	
	// Donne le nom et la taille que prendra l'option
	private Pair optionLength(int num) {
		switch(num) {
			case 0:
				return new Pair("EOOL",0);
			case 1:
				return new Pair("NOP",0);
			case 2:
				return new Pair("SEC",0);
			case 7:
				return new Pair("RR",0);
			case 10:
				return new Pair("ZSU",0);
			case 11:
				return new Pair("MTU",0);
			case 12:
				return new Pair("MTUR",0);
			case 15:
				return new Pair("ENCODE",0);
			case 25:
				return new Pair("QS",0);
			case 30:
				return new Pair("EXP",0);
			case 68:
				return new Pair("EOOL",0);
			case 82:
				return new Pair("EOOL",0);
			case 94:
				return new Pair("EOOL",0);
			case 130:
				return new Pair("EOOL",0);
			case 131:
				return new Pair("EOOL",0);
			case 133:
				return new Pair("EOOL",0);
			case 134:
				return new Pair("EOOL",0);
			case 136:
				return new Pair("EOOL",0);
			case 137:
				return new Pair("EOOL",0);
			case 142:
				return new Pair("EOOL",0);
			case 144:
				return new Pair("EOOL",0);
			case 145:
				return new Pair("EOOL",0);
			case 147:
				return new Pair("EOOL",0);
			case 148:
				return new Pair("EOOL",0);
			case 149:
				return new Pair("EOOL",0);
			case 151:
				return new Pair("EOOL",0);
			case 152:
				return new Pair("EOOL",0);
			case 158:
				return new Pair("EOOL",0);
			case 205:
				return new Pair("EOOL",0);
			case 222:
				return new Pair("EOOL",0);
			default:
				throw new IllegalArgumentException("IP: type option non valide");
		}
	}
	// Classe qui représente un tuple
	public class Pair{
		public String x;
		public Integer y;
		public Pair(String a, Integer b) { x=a; y=b; }
	}
}
