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

import ai.mrp.inf.Predictor;
import ai.mrp.inf.Trainer;
import ai.mrp.model.DataModel;
import ai.mrp.model.ReviewType;
import ai.mrp.util.ProfileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * An implementation of the Naive Bayes classification algorithm.
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@Slf4j
@Component
@Qualifier("NaiveBayes")
public class NaiveBayesPredictorImpl implements Predictor<ReviewType>, InitializingBean {

	@Autowired
	@Qualifier("NaiveBayes")
	private Trainer<DataModel<ReviewType>> trainer;

	private DataModel<ReviewType> dataModel;

	/**
	 * Trains the Naive Bayes data model.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.dataModel = trainer.train();
		// predict("i love this movie");
		// predict("this movie is simply awesome");
		// predict("its very bad");
		// predict("Not that great");
	}

	@SneakyThrows
	@Override
	public ReviewType predict(String sentence) {
		return ProfileUtils.profileExecution(() -> {
			String text = sentence.toLowerCase();
			double positiveScore = calcScore(text, ReviewType.POSITIVE);
			double negativeScore = calcScore(text, ReviewType.NEGATIVE);
			ReviewType predictedType = ReviewType.POSITIVE;
			if (negativeScore > positiveScore) {
				predictedType = ReviewType.NEGATIVE;
			}
			log.debug("Sentence: {}, positiveScore: {}, negativeScore: {}, predictedType: {}", text, positiveScore,
					negativeScore, predictedType);
			return predictedType;
		});
	}

	/**
	 * Calculates the score of a given input text and its type.
	 *
	 * @param text the input text for which the score is to be calculated
	 * @param type the class type
	 * @return the calculated score
	 */
	private double calcScore(String text, ReviewType type) {
		int totalNumberOfWords = dataModel.total(ReviewType.POSITIVE) + dataModel.total(ReviewType.NEGATIVE);
		double classProbability = (double) dataModel.total(type) / totalNumberOfWords;
		double score = classProbability;
		for (String word : text.split(" ")) {
			int wordCount = dataModel.get(type, word);
			if (wordCount > 0) {
				score *= (double) wordCount / totalNumberOfWords;
			}
		}
		return score;
	}

}
