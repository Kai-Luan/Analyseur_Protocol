package pobj.champs;

import java.util.StringJoiner;

import pobj.Donnees;
public class UDP {
	public int portSrc, portDest;
	public int length;
	public String checksum;
	
	public UDP(Donnees trame) {
		portSrc= trame.parseInt(0, 2);
		portDest= trame.parseInt(2, 4);
		length= trame.parseInt(4, 6);
		checksum= trame.parseHexa(6, 8);
	}
	
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner("\n  ", "Protocol UDP\n  ","\n");
		sb.add("Source port: "+ portSrc);
		sb.add("Destination port: "+ portDest);
		sb.add("Length: "+ length);
		sb.add("Checksum: "+ checksum);
		return sb.toString();
	}
}
