package pobj.champs;

import java.util.StringJoiner;

import pobj.Donnees;
public class UDP {
	public int portSrc, portDest;
	public String length, checksum;
	
	public UDP(Donnees trame) {
		portSrc= Integer.parseInt(trame.get(0, 3));
		portSrc= Integer.parseInt(trame.get(3, 5));
		length= trame.get(5, 7);
		checksum= trame.get(7, 9);
	}
	
	@Override
	public String toString() {
		StringJoiner sb = new StringJoiner("\n  ", "Protocol UDP\n  ","\n");
		return sb.toString();
	}
	
	public int getPortSrc(){
		return portSrc;
	}
	
	public int getPortDest(){
		return portDest;
	}
}
