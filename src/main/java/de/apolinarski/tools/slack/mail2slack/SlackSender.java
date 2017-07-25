package de.apolinarski.tools.slack.mail2slack;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class SlackSender {
	
	private static final String MESSAGE_TEXT="text";
	private static final String ICON="icon_emoji";
	private static final String USERNAME="username";
	private static final String CHANNEL="channel";
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	private final String webhook;

	public SlackSender(String webhook)
	{
		this.webhook = webhook;
	}
	
	public boolean sendMessage(SlackMessage message)
	{
		HttpPost post = new HttpPost(webhook);
		post.setHeader(HttpHeaders.CONTENT_TYPE,ContentType.APPLICATION_JSON.toString());
		JsonObject jsonObject = createJson(message);
		Charset charset;
		try
		{
			charset = Charset.forName(message.getCharset());
		}
		catch(Exception e)
		{
			//Could not find charset:
			System.err.println("Could not find charset: "+message.getCharset()+" using default charset: "+DEFAULT_CHARSET);
			charset = Charset.forName(DEFAULT_CHARSET);
		}
		post.setEntity(new StringEntity(jsonObject.toString(),charset));
		try (CloseableHttpClient client = HttpClientBuilder.create().build())
		{
			HttpResponse response = client.execute(post);
			System.out.println("Response code is: "+response.getStatusLine().getStatusCode());
			System.out.println();
			response.getEntity().writeTo(System.out);
			response.getEntity().consumeContent();
		}
		catch (IOException e)
		{
			System.err.println("Could not send message: "+e.getMessage());
		}
		return true;
		
	}

	private JsonObject createJson(SlackMessage message) {
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
				.add(MESSAGE_TEXT, message.getText())
				.add(USERNAME, message.getUserName());
		String channel = message.getChannel();
		String icon = message.getIcon();
		if(channel!=null)
		{
			jsonBuilder.add(CHANNEL, channel);
		}
		if(icon!=null)
		{
			jsonBuilder.add(ICON, icon);
		}
		return jsonBuilder.build();
	}
	
}
