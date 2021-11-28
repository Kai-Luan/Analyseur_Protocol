package pobj;

import java.util.List;
import java.util.StringJoiner;

public class IP {
	private String version, headerLength, tos, totalLength;
	private String identifier, flags, offset;
	private String ttl, protocol, checksum;
	private String src, dest;
	private String option;
	private UDP udp;
	
	public IP(Trame trame) {
		String s1= trame.get(0);
		version= String.valueOf(s1.charAt(0));
		headerLength= String.valueOf(s1.charAt(1));
		tos= trame.get(1);
		totalLength= trame.get(2,4);
		identifier= trame.get(4,6);
		flags= trame.get(6);
		offset= trame.get(6,8);
		ttl= trame.get(8);
		protocol= trame.get(9);
		checksum= trame.get(10,12);
		src= trame.get(12,16);
		dest= trame.get(16,20);
		udp= new UDP(trame.subTrame(Integer.parseInt(headerLength, 16)*8));
	}
	
	public String getSrc() {
		return getIp(src);
	}
	
	public String getDest() {
		return getIp(dest);
	}
	
	private String getIp(String s) {
		StringJoiner sb= new StringJoiner(".");
		String[] tab= s.split(":");
		for (String val : tab) {
			sb.add(String.valueOf(Integer.parseInt(val, 16)));
		}
		return sb.toString();
	}
}
