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

/**
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@Component
public class TrainerImpl implements Trainer<ReviewType> {

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private DataConfig dataConfig;

	@Override
	public DataModel<ReviewType> train() throws Exception {
		DataModel<ReviewType> dataModel = new DataModel<>(ReviewType.class);
		train(dataModel, ReviewType.POSITIVE, loadFile(dataConfig.getBaseDataDirectory() + dataConfig.getPositiveReviewsDirectory()));
		train(dataModel, ReviewType.NEGATIVE, loadFile(dataConfig.getBaseDataDirectory() + dataConfig.getNegativeReviewsDirectory()));
		return dataModel;
	}

	private void train(DataModel<ReviewType> dataModel, ReviewType type, File directory) throws IOException {
		Files
			.list(directory.toPath())
			.flatMap(p -> {
				try {
					return Files.lines(p);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			})
			.map(line -> line.toLowerCase().split(" "))
			.forEach(words -> {
				for (String word : words) {
					dataModel.put(type, word);
				}
			});
	}

	private File loadFile(String path) throws IOException {
		return resourceLoader.getResource("classpath:" + path).getFile();
	}

}
