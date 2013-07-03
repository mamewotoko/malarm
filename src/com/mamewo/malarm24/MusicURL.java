package com.mamewo.malarm24;

public class MusicURL {
	enum URLType {
		MUSIC,
		PODCAST_XML,
		PODCAST_EPISODE
	};
	public final URLType type_;
	public final String url_;
	
	public MusicURL(URLType type, String url) {
		type_ = type;
		url_ = url;
	}
}
