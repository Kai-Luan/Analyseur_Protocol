package pobj.champs;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import pobj.Donnees;

public class IP {
	public int version, IHL, tos, totalLength;
	String identifier;
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
		identifier= trame.get(4,6);
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
	
	// Retourne en String, l'entete IP
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner("\n  ", "Protocol IP\n  ","\n");
		sb.add("Version: "+ version);
		sb.add("Header length: "+ IHL);
		sb.add("Type of service: "+ tos);
		sb.add("Total Length: "+ totalLength);
		sb.add("Identifier "+ identifier);
		sb.add("Flags: "+ flags.toString());
		sb.add("Fragment offset: "+ offset);
		sb.add("Protocol: "+ protocol);
		sb.add("Header Checksum: "+ checksum);
		return sb.toString();
		
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
		String s = trame.get(6,8);
		int val = trame.parseInt(6);
		val-= 64*flags[1] + 32*flags[2];
		return val+trame.parseInt(7);
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
}
