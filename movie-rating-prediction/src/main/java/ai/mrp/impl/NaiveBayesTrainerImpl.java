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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import ai.mrp.config.DataConfig;
import ai.mrp.inf.Trainer;
import ai.mrp.model.DataModel;
import ai.mrp.model.ReviewType;
import ai.mrp.util.FileUtils;

/**
 * {@link NaiveBayesTrainerImpl} implements logic to train the Naive Bayes
 * classifier.
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@Component
public class NaiveBayesTrainerImpl implements Trainer<ReviewType> {

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private DataConfig dataConfig;

	@Override
	public DataModel<ReviewType> train() throws Exception {
		DataModel<ReviewType> dataModel = new DataModel<>(ReviewType.class);
		train(dataModel, ReviewType.POSITIVE,
			FileUtils.loadFile(resourceLoader, dataConfig.getBaseDataDirectory() + dataConfig.getPositiveReviewsDirectory()));
		train(dataModel, ReviewType.NEGATIVE,
			FileUtils.loadFile(resourceLoader, dataConfig.getBaseDataDirectory() + dataConfig.getNegativeReviewsDirectory()));
		return dataModel;
	}

	/**
	 * Trains a given {@link DataModel} with the given class type, with the data
	 * at the given directory.
	 *
	 * @param dataModel the {@link DataModel} to train
	 * @param type the class type
	 * @param directory the directory from which data is to be loaded
	 * @throws IOException if data loading fails
	 */
	private void train(DataModel<ReviewType> dataModel, ReviewType type, File directory) throws IOException {
		Files
			.list(directory.toPath())
			.map(FileUtils::readLines)
			.map(lines -> lines.toString())
			.map(lineStr -> lineStr.substring(1, lineStr.length() - 1).toLowerCase())
			.map(lines -> lines.split(" "))
			.forEach(words -> {
				for (String word : words) {
					dataModel.put(type, word);
				}
			});
	}

}
