package com.twitter.hbc.retweet;

import java.util.Comparator;

public class FrequencyComparator implements Comparator<FrequencyDescriptor>
{

	private HeapType heap_;
	
	public FrequencyComparator(HeapType heap)
	{
		heap_ = heap;
	}
	
	public static enum HeapType
	{
		MAX_HEAP,
		MIN_HEAP
	}
	@Override
	public int compare(FrequencyDescriptor fd1, FrequencyDescriptor fd2) 
	{
		int retVal = 0;
		if(heap_.equals(HeapType.MAX_HEAP))
		{
			if(fd1.getFrequency() > fd2.getFrequency())
			{
				retVal = -1;
			}
			if(fd1.getFrequency() < fd2.getFrequency())
			{
				retVal = 1;
			}
		}
		else
		{
			if(retVal == -1) 
				retVal = 1;
			else if(retVal == 1) 
				retVal = -1;
		}
		return retVal;
	}

}
