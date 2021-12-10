package pobj.champs;

import java.util.StringJoiner;

import pobj.Donnees;

public class IPv6 implements IP{
	int version=6;
	String traffic_class;
	String flow_label;
	int payload_length;
	int next_header;
	int hop_limit;
	String src, dest;
	
	// Constructeur
	public IPv6(Donnees trame) {
		String bits = trame.parseBit(0, 4);
		traffic_class= calcule_traffic_class(bits.substring(4,12));
		flow_label = bits.substring(12, 32);
		payload_length = trame.parseInt(4,6);
		next_header = trame.parseInt(6);
		hop_limit = trame.parseInt(7);
		src = trame.getIPv6(8, 24);
		dest = trame.getIPv6(24, 40);
	}
	
	private String calcule_traffic_class(String s) {
		StringJoiner sj = new StringJoiner("\n  ","Traffic Class:\n", "");
		int DSCP = Integer.parseInt(s.substring(0, 7), 2);
		int ECN = Integer.parseInt(s.substring(7, 9), 2);
		sj.add("DSCP: "+ DSCP);
		sj.add("ECN: "+ ECN);
		return sj.toString();
	}
	// Retourne en String, l'entete IP
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner("\n  ", " 	Internet Protocol\n  ","\n");
		sb.add("Version: 6");
		sb.add(traffic_class);
		sb.add("Payload Length: "+ payload_length);
		sb.add("Next Header: "+ next_header);
		sb.add("Hop Limit: "+ hop_limit);
		sb.add("Source Address: "+ src);
		sb.add("Destination Address: "+ dest);
		return sb.toString();
	}
	
	public int length() {
		return 40;
	}
}
