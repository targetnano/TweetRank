package com.twitter.hbc.retweet;

public class RetweetMetadata 
{

	private long timestamp_;
	private long id_;
	
	public RetweetMetadata(long timestamp, long id)
	{
		timestamp_ = timestamp;
		id_ = id;
	}
	
	public long getTimestamp()
	{
		return timestamp_;
	}
	
	public long getRetweetId()
	{
		return id_;
	}
}
