package com.twitter.hbc.retweet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
	/* Contain all retweeted tweets orderby frequency*/
	PriorityQueue<RetweetDescriptor> pq_;	
	/* Mapping from retweeted statusId to content */
	Map<Long, RetweetDescriptor> idToRetweetMap_;
	
	private long thresholdInMillis_ = 5 * 60 * 1000 * 1000 * 1000L;
	private int numTopRetweets_ = 4;
	
	public RetweetRanker(BlockingQueue<Status> queue)
	{
		retweetQueue_ = queue;
		rankerQueue_ = new LinkedList<RetweetMetadata>();
		pq_ = new PriorityQueue<RetweetDescriptor>(1, new FrequencyComparator(HeapType.MAX_HEAP));
	    idToRetweetMap_ = new HashMap<Long, RetweetDescriptor>();
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			try
			{
				//System.out.println("RankerQ length: " + rankerQueue_.size() + ", pqSize: " + pq_.size() + ", MapSize: " + idToRetweetMap_.size());
				Status status = retweetQueue_.take();
				if(isThresholdViolated())
				{
					removeRetweet();
				}
				addNewRetweet(status);
				printTopRetweets();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
	
	private void printTopRetweets()
	{
		List<RetweetDescriptor> list = new ArrayList<RetweetDescriptor>();
		for( int i = 0; i < numTopRetweets_; i++)
		{
			if(pq_.isEmpty())
				break;
			RetweetDescriptor rD = pq_.remove();
			System.out.println("Frequency = " + rD.getFrequency() + ", Retweet: " + rD.getContent());
			list.add(rD);
		}
		for(RetweetDescriptor rD : list)
		{
			pq_.add(rD);
		}
		System.out.println("=================================================================================");
	}
	
	private void addNewRetweet(Status status)
	{
		long retweetId = status.getRetweetedStatus().getId();
		RetweetDescriptor rD = idToRetweetMap_.get(retweetId);
		if(rD == null)
		{
			rD = new RetweetDescriptor(1, status.getRetweetedStatus().getText());
			idToRetweetMap_.put(retweetId, rD);
		}
		else
		{
			pq_.remove(rD);
			rD.incrementFrequency();
		}
		pq_.add(rD);
		RetweetMetadata rM = new RetweetMetadata(System.nanoTime(), retweetId);
		rankerQueue_.add(rM);
	}
	
	private void removeRetweet()
	{
		RetweetMetadata rM = rankerQueue_.poll();
		long id = rM.getRetweetId();
		RetweetDescriptor rD = idToRetweetMap_.get(id);
		pq_.remove(rD);
		rD.decrementFrequency();
		if(rD.getFrequency() > 0)
		{
			pq_.add(rD);
		}
		else
		{
			idToRetweetMap_.remove(id);
		}
	}
	
	private boolean isThresholdViolated()
	{
		boolean bVal = false;
		if(!rankerQueue_.isEmpty())
		{
			RetweetMetadata rM = rankerQueue_.peek();
			long timedelta = System.nanoTime() - rM.getTimestamp();
			System.out.println("Timedelta = " + timedelta);
			if(timedelta > thresholdInMillis_)
				bVal = true;
		}
		return bVal;
	}
}
