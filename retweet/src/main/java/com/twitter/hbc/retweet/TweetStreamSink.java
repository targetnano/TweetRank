/**
 * Copyright 2013 Twitter, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.twitter.hbc.retweet;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class TweetStreamSink {

	private static final int queueSize_ = 10000;
	private static final int numThreads_ = 4;
	private static final String english_ = "en";
	
	private BlockingQueue<Status> retweetQueue_;
	private boolean filterEnglish_ = true;

	public TweetStreamSink(boolean filterEnglish, BlockingQueue<Status> retweetQueue)
	{
		filterEnglish_ = filterEnglish;
		retweetQueue_ = retweetQueue;
	}

	/* Listener that gets callbacks once a message is received */
	private StatusListener listener = new StatusListener() {
		@Override
		public void onStatus(Status status) 
		{
			String language = status.getLang();
			if(filterEnglish_ && !language.equals(english_))
			{
				return;
			}
			/* If the status is a retweet, add it to the queue */
			if(status.isRetweet() && language.equals(english_))
			{
				retweetQueue_.offer(status);
			}
		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

		@Override
		public void onTrackLimitationNotice(int limit) {}

		@Override
		public void onScrubGeo(long user, long upToStatus) {}

		@Override
		public void onStallWarning(StallWarning warning) {}

		@Override
		public void onException(Exception e) {}
	};

	public void startListening(String consumerKey, String consumerSecret, String token, String secret) throws InterruptedException {
		// Create an appropriately sized blocking queue
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(queueSize_);

		StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
		Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
		// Authentication auth = new BasicAuth(username, password);

		// Create a new BasicClient. By default gzip is enabled.
		BasicClient client = new ClientBuilder()
		.hosts(Constants.STREAM_HOST)
		.endpoint(endpoint)
		.authentication(auth)
		.processor(new StringDelimitedProcessor(queue))
		.build();

		// Create an executor service which will spawn threads to do the actual work of parsing the incoming messages and
		// calling the listeners on each message
		ExecutorService service = Executors.newFixedThreadPool(numThreads_);

		// Wrap our BasicClient with the twitter4j client
		Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(
				client, queue, Lists.newArrayList(listener), service);

		// Establish a connection
		t4jClient.connect();
		for (int threads = 0; threads < numThreads_; threads++) {
			// This must be called once per processing thread
			t4jClient.process();
		}
	}

}
