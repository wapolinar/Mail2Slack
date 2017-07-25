package de.apolinarski.tools.slack.mail2slack;

public class SlackMessage {
	
	private String text;
	private String channel;
	private String icon;
	private String charset;
	private String userName;
	

	public String getText() {
		return text;
	}

	public String getChannel() {
		return channel;
	}

	public String getIcon() {
		return icon;
	}

	public String getCharset() {
		return charset;
	}

	public String getUserName() {
		return userName;
	}

	public SlackMessage setText(String text) {
		this.text = text;
		return this;
	}

	public SlackMessage setChannel(String channel) {
		this.channel = channel;
		return this;
	}

	public SlackMessage setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public SlackMessage setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public SlackMessage setUserName(String userName) {
		this.userName = userName;
		return this;
	}
}
