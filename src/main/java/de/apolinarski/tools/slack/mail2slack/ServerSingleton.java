package de.apolinarski.tools.slack.mail2slack;

import java.time.LocalDateTime;

public class ServerSingleton {

	public static final ServerSingleton SINGLETON = new ServerSingleton();
	
	private static final int SLEEP_TIME = 15 * 60 * 1000; // 15 minutes
	
	private final Settings settings;
	private final SlackSender sender;
	
	private volatile boolean RUNNING = true;
	
	private Thread runningThread;
	
	private ServerSingleton() {
		settings = new Settings();
		sender = new SlackSender(settings.getSlackHookURL());
	}
	
	public void sendMessage(SlackMessage message)
	{
		new Thread( () -> sender.sendMessage(message)).start();
	}
	
	public synchronized void startServer()
	{
		if(runningThread!=null && runningThread.isAlive())
		{
			return;
		}
		else
		{
			runningThread = new Thread(() -> serverLoop());
			runningThread.start();
		}
	}
	
	private void serverLoop()
	{
		IMailServer server = IMailServer.factory(settings);
		if(server==null)
		{
			System.err.println("Could not create server!");
			return;
		}
		while(RUNNING)
		{
			System.out.println(LocalDateTime.now()+" Checking for new mails.");
			server.readAndPost();
			try
			{
				Thread.sleep(SLEEP_TIME);
			}
			catch (InterruptedException e)
			{
				System.err.println("ServerSingleton-Thread has been interrupted: "+e.getMessage());
				RUNNING = false;
				Thread.currentThread().interrupt();
			}
		}
	}
	
}
