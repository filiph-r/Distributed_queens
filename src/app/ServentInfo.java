package app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;
	private final String ipAddress;
	private final int listenerPort;
	public List<ServentInfo> neighbors = Collections.synchronizedList(new ArrayList<>());
	private final int limit;

	private final String bootStrapIpAdress;
	private final int bootStrapPort;

	// for token
	public AtomicInteger sn = new AtomicInteger(0);

	public ServentInfo(String ipAddress, int listenerPort, String bootStrapIpAdress, int bootStrapPort, int limit) {
		super();
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;
		this.bootStrapIpAdress = bootStrapIpAdress;
		this.bootStrapPort = bootStrapPort;
		this.limit = limit;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public String getBootStrapIpAdress() {
		return bootStrapIpAdress;
	}

	public int getBootStrapPort() {
		return bootStrapPort;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public String toString() {
		return "ServentInfo [ ipAddress=" + ipAddress + ", listenerPort=" + listenerPort + ", neighbors=" + neighbors
				+ ", limit=" + limit + ", bootStrapIpAdress=" + bootStrapIpAdress + ", bootStrapPort=" + bootStrapPort
				+ "]";
	}

	public boolean equals(ServentInfo other) {
		if (this.ipAddress.equals(other.getIpAddress()) && this.listenerPort == other.getListenerPort()) {
			return true;
		} else {
			return false;
		}

	}

}
