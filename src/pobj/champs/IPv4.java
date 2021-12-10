package pobj.champs;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import pobj.Donnees;

public class IPv4 implements IP {
	public int version, IHL, tos, totalLength;
	String identifier;
	int[] flags;
	int offset;
	int ttl;
	String protocol;
	String checksum;
	String src, dest;
	List<String> listOption;
	
	// Constructeur
	public IPv4(Donnees trame) {
		// A partir de l'octet n°0
		String s1= trame.get(0);
		version= 4;
		IHL= Integer.parseInt(String.valueOf(s1.charAt(1)));
		tos= trame.parseInt(1);
		totalLength= trame.parseInt(2,4);
		// A partir de l'octet n°4
		identifier= trame.parseHexa(4,6);
		flags= calculeFlags(trame);
		offset= calculeOffset(trame);
		// A partir de l'octet n°8
		ttl= trame.parseInt(8);
		protocol= calculeProtocol(trame);
		checksum= trame.parseHexa(10,12);
		// A partir de l'octet n°12
		src= trame.getIP(12, 16);
		// A partir de l'octet n°16
		dest= trame.getIP(16, 20);
		// A partir de l'octet n°20
		listOption = calculeOption(trame);
	}
	
	// Retourne en String, l'entete IP
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner("\n  ", " 	Internet Protocol\n  ","\n");
		sb.add("Version: "+ version);
		sb.add("Header length: "+ IHL);
		sb.add("Type of service: "+ tos);
		sb.add("Total Length: "+ totalLength);
		sb.add("Identifier "+ identifier);
		sb.add("Flags: ");
		sb.add("  Reserved bit: " + flags[0]);
		sb.add("  Don't fragment: " + flags[1]);
		sb.add("  More fragments: " + flags[2]);
		sb.add("Fragment offset: "+ offset);
		sb.add("Protocol: "+ protocol);
		sb.add("Header Checksum: "+ checksum);
		sb.add("Source Address: "+ src);
		sb.add("Destination Address: "+ dest);
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
		int val = trame.parseInt(6);
		val-= 64*flags[1] + 32*flags[2];
		return val+trame.parseInt(7);
	}
	
	// Calcule le protocol
	private String calculeProtocol(Donnees trame) {		
		int i = trame.parseInt(9);
		if (i!=17) throw new IllegalArgumentException("IP: mauvais protocol");
		return "UDP (17)";
	}
	
	// Calcule la liste des options dans l'entete IP
	private List<String> calculeOption(Donnees trame) {
		List<String> res = new ArrayList<>();	
		return res;
	}
	// Donne la nombre d'octets dans l'entête IP
	public int length() {
		return 4*IHL;
	}
}
