package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import calculation.Job;
import calculation.OneResult;
import threadSafeObj.FinalResults;
import threadSafeObj.TokenHolder;

public class AppConfig {

	public static Object stealingKey = new Object();
	public static volatile boolean stealing = false;
	public static ConcurrentLinkedQueue<ServentInfo> victims = new ConcurrentLinkedQueue<ServentInfo>();

	public static volatile boolean stoping = false;

	public static ServentInfo myServentInfo;
	public static Object jobQueueKey = new Object();
	public static BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<Job>();
	public static ConcurrentHashMap<Integer, ArrayList<OneResult>> resultMap = new ConcurrentHashMap<>();
	public static FinalResults finalResults = new FinalResults();

	public static ConcurrentLinkedQueue<Job> pausedJobs = new ConcurrentLinkedQueue<Job>();

	public static TokenHolder tokenHolder = new TokenHolder();
	public static volatile boolean worksWithToken = false;

	public static AtomicInteger statusCount = new AtomicInteger(-1);
	public static ConcurrentLinkedQueue<Entry<Integer, String>> statusList = new ConcurrentLinkedQueue<>();

	public static void timestampedStandardPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();

		System.out.println(timeFormat.format(now) + " - " + message);
	}

	public static void timestampedErrorPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();

		System.err.println(timeFormat.format(now) + " - " + message);
	}

	public static void readConfig(String configName) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(configName)));

		} catch (IOException e) {
			timestampedErrorPrint("Couldn't open properties file. Exiting...");
			System.exit(0);
		}

		int port = -1;
		try {
			port = Integer.parseInt(properties.getProperty("port"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading port. Exiting...");
			System.exit(0);
		}

		String bootStrapIpAddress = "";
		try {
			bootStrapIpAddress = properties.getProperty("bootstrap.ip");
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap ip. Exiting...");
			System.exit(0);
		}

		int bootstrapPort = -1;
		try {
			bootstrapPort = Integer.parseInt(properties.getProperty("bootstrap.port"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap port. Exiting...");
			System.exit(0);
		}

		int limit = -1;
		try {
			limit = Integer.parseInt(properties.getProperty("limit"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap port. Exiting...");
			System.exit(0);
		}

		String ip = null;
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), port);
			ip = socket.getLocalAddress().getHostAddress();
			socket.close();
		} catch (Exception e) {
			timestampedErrorPrint("Problem reading local ip address. Exiting...");
			System.exit(0);
		}

		myServentInfo = new ServentInfo(ip, port, bootStrapIpAddress, bootstrapPort, limit);
		System.out.println(myServentInfo.toString());
	}

}
