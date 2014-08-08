package com.twitter.hbc.retweet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.Status;

public class TweetClient {

	public static void main(String args[])
	{
		try 
		{
			BlockingQueue<Status> queue_ = new LinkedBlockingQueue<Status>();
			TweetStreamSink sink = new TweetStreamSink(queue_);
			RetweetRanker ranker = new RetweetRanker(queue_);
			new Thread(ranker).start();
			/*
			 * args[0] -> API Key
			 * args[1] -> API Secret
			 * args[2] -> Access Token
			 * args[3] -> Access Token Secret
			 */
			sink.startListening(args[0], args[1], args[2], args[3]);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
