package de.apolinarski.tools.slack.mail2slack;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Settings {

//	public static final Settings SINGLETON = new Settings();
	
	private static final String SETTINGS_FILE = "settings.conf";
	private static final String SECURITY="SECURITY";
	private static final String USERNAME="USERNAME";
	private static final String PASSWORD="PASSWORD";
	private static final String RELEVANT_FOLDER="RELEVANT-FOLDER";
	private static final String DELETE_OLD_MESSAGES_DAYS="DELETE-OLD-MESSAGES-AFTER-DAYS";
	private static final String IMAP_SERVER="IMAP-SERVER";
	private static final String IMAP_PROTOCOL="IMAP-PROTOCOL";
	private static final String SLACK_HOOK_URL = "SLACK-HOOK-URL";
	private static final String USER_ICON_PAIRS ="USER-ICON-PAIRS";
	private static final String ALLOWED_CHANNELS = "ALLOWED-CHANNELS";
	private static final String EXCLUDE_CHANNELS = "EXCLUDE-CHANNELS";
	private static final String MAX_MESSAGE_SIZE="MAX-MESSAGE-SIZE";
	private static final int MAX_MESSAGE_SIZE_DEFAULT = 250;
	
	private final Configuration config;
	
	public Settings()
	{
		FileBasedBuilderParameters params = new Parameters()
				.fileBased()
				.setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
				.setFileName(SETTINGS_FILE);
		Configuration configuration;
		try
		{
			FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
					.configure(params);
			configuration = builder.getConfiguration();
		}
		catch(ConfigurationException e)
		{
			System.err.println("Could not load configuration: "+e.getMessage());
			configuration = new PropertiesConfiguration(); //TODO: Test this without properties file, maybe a runtimeexception would make more sense.
		}
		this.config = configuration;
	}
	
	public String getSecurity()
	{
		return config.getString(SECURITY);
	}
	
	public String getUserName()
	{
		return config.getString(USERNAME);
	}
	
	public String getPassword()
	{
		return config.getString(PASSWORD);
	}
	
	public String getFolder()
	{
		return config.getString(RELEVANT_FOLDER);
	}
	
	public int getDeleteOldMesagesDays()
	{
		return config.getInt(DELETE_OLD_MESSAGES_DAYS, 0);
	}
	
	public String getImapServer()
	{
		return config.getString(IMAP_SERVER);
	}
	
	public String getImapProtocol()
	{
		return config.getString(IMAP_PROTOCOL);
	}
	
	public String getSlackHookURL()
	{
		return config.getString(SLACK_HOOK_URL);
	}
	
	public String[] getExcludedChannels()
	{
		return config.getStringArray(EXCLUDE_CHANNELS);
	}

	public int getMaxMessageSize() {
		return config.getInt(MAX_MESSAGE_SIZE,MAX_MESSAGE_SIZE_DEFAULT);
	}
	
	/**
	 *  # This is a configuration file for the Mail2Slack server application
 
 
# SECURITY = [STARTTLS|TLS|NONE]
SECURITY = STARTTLS

# USERNAME = me@example.com
USERNAME = me@example.com
# PASSWORD = <PASSWORD>
PASSWORD = some_secret_password

# RELEVANT-FOLDER - the folder that should be searched for new messages
RELEVANT-FOLDER = inbox

# DELETE-OLD-MESSAGES-AFTER-DAYS = [<DAYS>|0<NEVER>|-1<AS_SOON_AS_SENT>]
DELETE-OLD-MESSAGES-AFTER-DAYS = 14

# IMAP-Settings:
# IMAP-SERVER = imap.example.com
IMAP-SERVER = imap.gmail.com

# IMAP-PROTOCOL = [IMAPS]
IMAP-PROTOCOL = IMAPS

# SLACK-HOOK-URL = https://hooks.slack.com/services/<HOOK>

# Settings that are not used: 
# SMTP-SERVER = smtp.example.com
SMTP-SERVER = smtp.gmail.com
# SMTP-PORT = <PORT_NUMBER>
SMTP-PORT = 587
 
 
 
	 */
	
}
