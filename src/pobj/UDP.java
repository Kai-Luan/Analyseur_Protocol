package pobj;

import java.util.List;
public class UDP {
	private String portSrc, portDest;
	private String length, checksum;
	private Couche7 d;
	
	public UDP(Trame trame) {
		portSrc= trame.get(0, 3);
		portSrc= trame.get(3, 5);
		length= trame.get(5, 7);
		checksum= trame.get(7, 9);
		int i= Integer.parseInt(portDest, 16);
		if (i ==53)
			d= new DNS(trame.subTrame(9));
		else
			if (i==67)
				d= new DHCP(trame.subTrame(9));
	}
}
