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

import org.springframework.stereotype.Component;

import ai.mrp.inf.Predictor;
import ai.mrp.model.DataModel;
import ai.mrp.model.ReviewType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@Slf4j
@Component
public class PredictorImpl implements Predictor<ReviewType> {

	@Override
	public ReviewType predict(DataModel<ReviewType> dataModel, String sentence) {
		sentence = sentence.toLowerCase();
		double positiveScore = calcScore(dataModel, sentence, ReviewType.POSITIVE);
		double negativeScore = calcScore(dataModel, sentence, ReviewType.NEGATIVE);
		ReviewType predictedType = ReviewType.POSITIVE;
		if (negativeScore > positiveScore) {
			predictedType = ReviewType.NEGATIVE;
		}
		log.info("Sentence: {}, positiveScore: {}, negativeScore: {}, predictedType: {}", sentence, positiveScore,
				negativeScore, predictedType);
		return predictedType;
	}

	private double calcScore(DataModel<ReviewType> dataModel, String sentence, ReviewType type) {
		int totalNumberOfWords = dataModel.total(ReviewType.POSITIVE) + dataModel.total(ReviewType.NEGATIVE);
		double classProbability = (double) dataModel.total(type) / totalNumberOfWords; 
		double score = classProbability;
		for (String word : sentence.split(" ")) {
			int wordCount = dataModel.get(type, word);
			if (wordCount > 0) {
				score *= (double) wordCount / totalNumberOfWords;
			}
		}
		return score;
	}

}
