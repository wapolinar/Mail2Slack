package de.apolinarski.tools.slack.mail2slack;

public interface IMailServer {

	public static final String[] GMAIL = new String[]{"gmail.com","googlemail.com"};
	
	public static IMailServer factory(Settings settings)
	{
		for(String mail : GMAIL)
		{
			if(settings.getImapServer().endsWith(mail))
			{
				return new GmailServer(settings);
			}
		}
		return new ImapServer(settings);
	}

	public void readAndPost();
	
}
