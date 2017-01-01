/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.mrp.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ai.mrp.config.TwitterConfig;
import ai.mrp.inf.ClassificationEngine;
import ai.mrp.inf.Predictor;
import ai.mrp.model.ReviewType;
import lombok.extern.slf4j.Slf4j;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Implementation of the {@link ClassificationEngine} which connects to Twitter
 * and classifies data that is streamed.
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@Slf4j
@Component
public class StreamingTweetsClassificationEngine implements ClassificationEngine, InitializingBean {

	@Autowired
	@Qualifier("NaiveBayes")
	private Predictor<ReviewType> nbPredictor;

	@Autowired
	@Qualifier("SVM")
	private Predictor<ReviewType> svmPredictor;

	@Autowired
	private TwitterConfig twitterConf;

	private TwitterStream streamInstance;

	/**
	 * Creates the stream instance and configures the stream listeners.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(twitterConf.getConsumerKey());
		configurationBuilder.setOAuthConsumerSecret(twitterConf.getConsumerSecret());
		Configuration configuration = configurationBuilder.build();
	    TwitterStreamFactory streamFactory = new TwitterStreamFactory(configuration);
	    AccessToken accessToken = new AccessToken(twitterConf.getAccessToken(), twitterConf.getAccessTokenSecret());
	    TwitterStream streamInstance = streamFactory.getInstance(accessToken);
	    streamInstance.addListener(new StatusAdapter() {
	    	@Override public void onStatus(Status status) {
	    		if (!status.isRetweet()) {
		    		ReviewType reviewType = nbPredictor.predict(status.getText());
		    		log.info("NB -- {}: {}", reviewType, status.getText());
	    		}
	    	}
	    });
	    streamInstance.addListener(new StatusAdapter() {
	    	@Override public void onStatus(Status status) {
	    		if (!status.isRetweet()) {
		    		ReviewType reviewType = svmPredictor.predict(status.getText());
		    		log.info("SVM -- {}: {}", reviewType, status.getText());
	    		}
	    	}
	    });
	    this.streamInstance = streamInstance;
	}

	/**
	 * Starts streaming data from Twitter.
	 */
	@Override
	public void start() {
	    streamInstance.filter(new FilterQuery("#lalaland", "#hiddenfigures", "#whyhim"));
	}

	/**
	 * Shuts down the streaming connection.
	 */
	@Override
	public void stop() {
		streamInstance.shutdown();
	}

}
