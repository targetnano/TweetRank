package com.twitter.hbc.retweet;

import java.util.concurrent.BlockingQueue;

import twitter4j.Status;

public class RetweetRanker implements Runnable 
{
	private BlockingQueue<Status> retweetQueue_;
	
	public RetweetRanker(BlockingQueue<Status> queue)
	{
		retweetQueue_ = queue;
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			try
			{
				Status status = retweetQueue_.take();
				System.out.println(status.getRetweetedStatus().getText());
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
	
}
