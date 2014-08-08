package com.twitter.hbc.retweet;

public class FrequencyDescriptor implements Comparable
{
	private int frequency_;
	private String content_;
	
	public FrequencyDescriptor(int frequency, String content)
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
	
	public void deccrementFrequency(int delta)
	{
		frequency_ -= delta;
	}

	@Override
	public int compareTo(Object o) 
	{
		if(!(o instanceof FrequencyDescriptor))
			return -1;
		FrequencyDescriptor fd = (FrequencyDescriptor)o;
		if(frequency_ > fd.getFrequency())
		{
			return 1;
		}
		if(frequency_ < fd.getFrequency())
			return -1;
		return 0;
	}
}
