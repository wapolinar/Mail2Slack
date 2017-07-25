package de.apolinarski.tools.slack.mail2slack;

public class ImapServer implements IMailServer {

	private final Settings settings;
	
	public ImapServer(Settings settings)
	{
		this.settings = settings;
	}

	@Override
	public void readAndPost() {
		// TODO Auto-generated method stub
		
	}

}
