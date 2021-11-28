package pobj;
import java.util.ArrayList;
import java.util.List;

public class Ethernet {
	private String dest, src;
	private String type;
	private IP ip;
	public Ethernet(Trame trame) {
		dest= trame.get(0, 6, ":");
		src= trame.get(6, 12, ":");
		type= trame.get(12, 14);
		ip= new IP(trame.subTrame(14));
	}
}
