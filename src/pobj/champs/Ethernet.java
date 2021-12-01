package pobj.champs;
import java.util.ArrayList;
import java.util.List;

import pobj.Donnees;

public class Ethernet {
	private String dest, src;
	private String type;
	public Ethernet(Donnees trame) {
		dest= trame.get(0, 6, ":");
		src= trame.get(6, 12, ":");
		type= trame.get(12, 14);
	}
}
