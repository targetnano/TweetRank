package com.twitter.hbc.retweet;

public class TweetClient {

	public static void main(String args[])
	{
		try {
			TweetStreamSink sink = new TweetStreamSink(1);
			
			/*
			 * args[0] -> API Key
			 * args[1] -> API Secret
			 * args[2] -> Access Token
			 * args[3] -> Access Token Secret
			 */
			sink.startListening(args[0], args[1], args[2], args[3]);
		}
		catch(Exception e) {
			
		}
	}
}
