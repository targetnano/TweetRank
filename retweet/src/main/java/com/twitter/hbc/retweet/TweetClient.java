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
			sink.startListening("Ox91zPc2uCTMOAINlMbJ7J8SP", "qs3YwPwqf6WTUQNVPzpbzOfoUn9QQcoSaTjS0a2ehahIyVQ8Ra", "56464224-T8dxvKRz1q7TgvxCD3N7POFeiTdktTeuJKiysTk4h", "vPplPoOFe3kBjSS1xzNPa6IlfU1IdsV8Zy6Y0vmUrO7Mp");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
