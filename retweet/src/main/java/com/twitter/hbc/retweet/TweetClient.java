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
			
			/*
			 * args[4] -> Top 'n' trending retweets to update
			 * args[5] -> Time window in seconds
			 * args[6] -> true if only english tweets are required
			 */
			int numRetweets = Integer.parseInt(args[4]);
			int windowInSecs = Integer.parseInt(args[5]);
			boolean filterEnglish = Boolean.parseBoolean(args[6]);
			
			TweetStreamSink sink = new TweetStreamSink(filterEnglish, queue_);
			RetweetRanker ranker = new RetweetRanker(queue_, numRetweets, windowInSecs);
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
