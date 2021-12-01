package pobj.champs;

import java.util.StringJoiner;
import pobj.Donnees;

public class Ethernet {
	private String dest, src;
	private String type;
	// Constructeur
	public Ethernet(Donnees trame) {
		dest= trame.get(0, 6, ":");
		src= trame.get(6, 12, ":");
		type= trame.get(12, 14);
	}
	
	// Retourne en String, l'entête Ethernet
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner("\n  ", "Protocol Ethernet\n  ","\n");
		sb.add("Addresse Mac Destination: "+ dest);
		sb.add("Addresse Mac Source: "+ src);
		sb.add("Type: "+ type);
		return sb.toString();
	}
}
