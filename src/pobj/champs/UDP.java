package pobj.champs;

import java.util.List;

import pobj.Donnees;
public class UDP {
	private int portSrc, portDest;
	private String length, checksum;
	
	public UDP(Donnees trame) {
		portSrc= Integer.parseInt(trame.get(0, 3));
		portSrc= Integer.parseInt(trame.get(3, 5));
		length= trame.get(5, 7);
		checksum= trame.get(7, 9);
	}
	
	public int getPortSrc(){
		return portSrc;
	}
	
	public int getPortDest(){
		return portDest;
	}
}
