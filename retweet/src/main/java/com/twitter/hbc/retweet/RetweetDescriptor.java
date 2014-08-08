package com.twitter.hbc.retweet;

public class RetweetDescriptor implements Comparable
{
	private int frequency_;
	private String content_;
	
	public RetweetDescriptor(int frequency, String content)
	{
		frequency_ = frequency;
		content_ = content;
	}
	
	public int getFrequency()
	{
		return frequency_;
	}
	
	public String getContent()
	{
		return content_;
	}
	
	public void incrementFrequency()
	{
		frequency_++;
	}

	public void decrementFrequency()
	{
		frequency_--;
	}
	
	@Override
	public int compareTo(Object o) 
	{
		if(!(o instanceof RetweetDescriptor))
			return -1;
		RetweetDescriptor fd = (RetweetDescriptor)o;
		if(frequency_ > fd.getFrequency())
		{
			return -1;
		}
		if(frequency_ < fd.getFrequency())
			return 1;
		return 0;
	}
}
