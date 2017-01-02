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
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import ai.mrp.config.DataConfig;
import ai.mrp.inf.Trainer;
import ai.mrp.model.ReviewType;
import ai.mrp.util.FileUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

/**
 * {@link SVMTrainerImpl} implements logic to train the SVM classifier.
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@Component
@Qualifier("SVM")
public class SVMTrainerImpl implements Trainer<Instances> {

	/** The list of valid class types to predict. */
	private static final ArrayList<String> CLASSES;

	static {
		CLASSES = new ArrayList<>();
		CLASSES.add(ReviewType.POSITIVE.name());
		CLASSES.add(ReviewType.NEGATIVE.name());
	}

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private DataConfig dataConfig;

	@Override
	public Instances train() throws Exception {
		Instances instances = createInstances();
		loadData(instances, FileUtils.loadFile(resourceLoader,
			dataConfig.getBaseDataDirectory() + dataConfig.getPositiveReviewsDirectory()), ReviewType.POSITIVE);
		loadData(instances, FileUtils.loadFile(resourceLoader,
			dataConfig.getBaseDataDirectory() + dataConfig.getNegativeReviewsDirectory()), ReviewType.NEGATIVE);
		return instances;
	}

	/**
	 * Loads the training data into the model.
	 *
	 * @param instances the model into which the training data is to be loaded
	 * @param dir the directory from which the training data is to be loaded
	 * @param reviewType the class type of the training data to be loaded
	 * @throws IOException if loading fails
	 */
	private void loadData(Instances instances, File dir, ReviewType reviewType) throws IOException {
		Files
		.list(dir.toPath())
		.map(FileUtils::readLines)
		.map(lines -> lines.toString())
		.map(linesStr -> linesStr.substring(1, linesStr.length() - 1).toLowerCase())
		.map(text -> createInstance(instances, reviewType, text))
		.forEach(instances::add);
	}

	/**
	 * Creates a basic model.
	 *
	 * @return the model
	 */
	private Instances createInstances() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		attributes.add(new Attribute("text", (ArrayList<String>) null));
		attributes.add(new Attribute("@@class@@", CLASSES));
		Instances instances = new Instances("instances", attributes, 0);
		instances.setClassIndex(instances.numAttributes() - 1);
		return instances;
	}

	/**
	 * Creates a single instance.
	 *
	 * @param instances the model to which the instance belongs
	 * @param reviewType the class to which the instance belongs
	 * @param text the instance text
	 * @return the instance
	 */
	private DenseInstance createInstance(Instances instances, ReviewType reviewType,
			String text) {
		double[] attributes = new double[2];
		attributes[0] = instances.attribute(0).addStringValue(text);
		attributes[1] = CLASSES.indexOf(reviewType.name());
		return new DenseInstance(1, attributes);
	}

}
