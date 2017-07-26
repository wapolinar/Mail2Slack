package de.apolinarski.tools.slack.mail2slack;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailStore;

public class GmailServer implements IMailServer {

	private static final String GMAIL = "gmail:";
	private static final char OPEN_BRACKET_SUBJECT='[';
	private static final char CLOSE_BRACKET_SUBJECT=']';
	private static final String CHANNEL_START="#";
	private static final String CONTENT_TYPE = "text/plain";
	private static final String MESSAGE_TOO_LONG_STRING = " (...)";
	
	private final Settings settings;
	
	public GmailServer(Settings settings)
	{
		this.settings = settings;
	}

	@Override
	public void readAndPost()
	{	
		Session imapSessions = Session.getDefaultInstance(new Properties());
		GmailStore store = new GmailStore(imapSessions, new URLName(GMAIL));
		try {
			store.connect(settings.getUserName(), settings.getPassword());
			GmailFolder inbox = (GmailFolder) store.getFolder(settings.getFolder());
			inbox.open(Folder.READ_WRITE);
			for(Message message : inbox.getMessages())
			{
				if((!message.isSet(Flag.RECENT) && (message.isSet(Flag.SEEN))))
				{
					//Check age and delete
					LocalDate date = message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate maxDate = date.plusDays(settings.getDeleteOldMesagesDays());
					if(LocalDate.now().isAfter(maxDate))
					{
						System.err.println("Deleted mail that is older than 14 days.");
						message.setFlag(Flag.DELETED, true);
					}
					continue;
				}
				String channel = extractChannel(message.getSubject());
				if(channel==null)
				{
					System.err.println("Channel is invalid: "+message.getSubject());
					message.setFlag(Flag.DELETED, true);
					continue;
				}
				String userName=null;
				InternetAddress address;
				try
				{
					InternetAddress[] userNameAddress=(InternetAddress[]) message.getFrom();
					if(userNameAddress==null || userNameAddress.length==0 || userNameAddress[0]==null)
					{
						System.err.println("No valid user name address!");
						message.setFlag(Flag.DELETED, true);
						continue;
					}
					address = userNameAddress[0];
					userName = userNameAddress[0].getPersonal();
					if(userName == null)
					{
						userName = userNameAddress[0].getAddress();
						if(userName==null)
						{
							System.err.println("No valid user name!");
							message.setFlag(Flag.DELETED, true);
							continue;
						}
					}
				}
				catch(ClassCastException e)
				{
					System.err.println("ClassCastException for e-mail-address: "+e.getMessage());
					message.setFlag(Flag.DELETED, true);
					continue;
				}
				String icon=getIcon(address);
				String messageContent = null;
				try
				{
					Object content = message.getContent();
					if(content instanceof String)
					{
						messageContent = decodeAndShortenString((String) content,message);
					}
					else if(content instanceof Multipart)
					{
						Multipart multipartContent = (Multipart) content;
						int size = multipartContent.getCount();
						for(int i=0;i<size;i++)
						{
							BodyPart bPart = multipartContent.getBodyPart(i);
							if(bPart.isMimeType(CONTENT_TYPE))
							{
								Object multipartContentObject = bPart.getContent();
								if(multipartContentObject instanceof String)
								{
									messageContent = decodeAndShortenString((String) multipartContentObject, bPart);
									break;
								}
								else
								{
									System.err.println("Wrong class when transforming message content, should be string: "+multipartContentObject.getClass());
								}
							}
						}
					}
				}
				catch(IOException e)
				{
					System.err.println("Could not resolve content!");
				}
				if(messageContent==null)
				{
					System.err.println("Message content is NULL!");
					message.setFlag(Flag.DELETED, true);
					continue;
				}
				SlackMessage sm = new SlackMessage()
						.setChannel(channel)
						.setCharset(((MimeMessage)message).getEncoding())
						.setIcon(icon)
						.setText(messageContent)
						.setUserName(userName);
				ServerSingleton.SINGLETON.sendMessage(sm);
			}
			inbox.close(true);
			store.close();
		}
		catch(MessagingException e)
		{
			System.err.println("Could not retrieve messages: "+e.getClass()+" - "+ e.getMessage());
		}
	}

	private String decodeAndShortenString(String content, Part message) {
		if(content.length()>settings.getMaxMessageSize())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(content.substring(0, settings.getMaxMessageSize()))
				.append(MESSAGE_TOO_LONG_STRING);
			return sb.toString();
			
		}
		else
		{
			return content;
		}
	}

	private String getIcon(InternetAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	private String extractChannel(String subject) {
		if(subject.matches("\\[.*\\]"))
		{
			int indexLeftBracket = subject.indexOf(OPEN_BRACKET_SUBJECT);
			int indexRightBracket = subject.substring(indexLeftBracket).indexOf(CLOSE_BRACKET_SUBJECT);
			if(indexRightBracket != -1)
			{
				String result=subject.substring(indexLeftBracket+1, indexRightBracket);
				if(!result.startsWith(CHANNEL_START))
				{
					StringBuilder sb = new StringBuilder()
						.append(CHANNEL_START)
						.append(result);
					return sb.toString();
				}
				else
				{
					return result;
				}
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}
