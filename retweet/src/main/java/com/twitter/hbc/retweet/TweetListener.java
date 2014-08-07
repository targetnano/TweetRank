package com.twitter.hbc.retweet;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class TweetListener implements StatusListener
{
	@Override
	public void onException(Exception arg0) {}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {}

	@Override
	public void onScrubGeo(long arg0, long arg1) {}

	@Override
	public void onStallWarning(StallWarning arg0) {}

	@Override
	public void onStatus(Status status) 
	{
		System.out.println(status.getText());
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {}
}
