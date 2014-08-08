package com.twitter.hbc.retweet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import com.twitter.hbc.retweet.FrequencyComparator.HeapType;

import twitter4j.Status;

public class RetweetRanker implements Runnable 
{
	private BlockingQueue<Status> retweetQueue_;
	/* Contains all retweets in the given time window */
	private Queue<RetweetMetadata> rankerQueue_;
	/* Container top 10 frequenctly retweeted tweets */
	private ConcurrentSkipListSet<FrequencyDescriptor> topRetweets_; 
	/* Max-Heap that contains all retweets (except top 10) in descending order */
	PriorityQueue<FrequencyDescriptor> pq_;
	/* Mapping from retweeted statusId to content */
	Map<Long, FrequencyDescriptor> idToRetweetMap_;
	
	private long thresholdInMillis_ = 50000;
	
	public RetweetRanker(BlockingQueue<Status> queue)
	{
		retweetQueue_ = queue;
		rankerQueue_ = new LinkedList<RetweetMetadata>();
		topRetweets_ = new ConcurrentSkipListSet<FrequencyDescriptor>();
		pq_ = new PriorityQueue<FrequencyDescriptor>(1, new FrequencyComparator(HeapType.MAX_HEAP));
		idToRetweetMap_ = new HashMap<Long, FrequencyDescriptor>();
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			try
			{
				Status status = retweetQueue_.take();
				addNewRetweet(status);
				if(isThresholdViolated())
				{
					removeRetweet();
				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
	
	private void addNewRetweet(Status status)
	{
		long retweetId = status.getRetweetedStatus().getId();
		RetweetMetadata rM = new RetweetMetadata(System.currentTimeMillis(), retweetId);
		rankerQueue_.add(rM);
	}
	
	private void removeRetweet()
	{
		rankerQueue_.poll();
	}
	
	private boolean isThresholdViolated()
	{
		boolean bVal = false;
		if(!rankerQueue_.isEmpty())
		{
			RetweetMetadata rM = rankerQueue_.peek();
			long timedelta = System.currentTimeMillis() - rM.getTimestamp();
			if(timedelta > thresholdInMillis_)
				bVal = true;
		}
		return bVal;
	}
}
