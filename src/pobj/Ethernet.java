package pobj;
import java.util.ArrayList;

public class Ethernet {
	private String dest, src;
	private String type;
	private IP ip;
	public Ethernet(String[] trame) {
		dest= TrameUtils.listToString(TrameUtils.split(trame, 0, 6));
		src= TrameUtils.listToString(TrameUtils.split(trame, 6, 12));
		type= TrameUtils.listToString(TrameUtils.split(trame, 12, 14));
		ip= new IP(TrameUtils.split(trame, 14));
	}
}
