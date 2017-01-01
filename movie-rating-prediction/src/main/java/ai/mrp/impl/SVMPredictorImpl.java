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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import ai.mrp.config.DataConfig;
import ai.mrp.inf.Predictor;
import ai.mrp.model.ReviewType;
import ai.mrp.util.FileUtils;
import ai.mrp.util.ProfileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.stopwords.WordsFromFile;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@Slf4j
@Component
@Qualifier("SVM")
public class SVMPredictorImpl implements Predictor<ReviewType>, InitializingBean {

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

	private Instances trainingData;

	private Classifier classifier;

	/**
	 * Loads the training data as configured in {@link #dataConfig} and trains a
	 * 3-gram SVM classifier.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.trainingData = loadTrainingData();
		StringToWordVector stwvFilter = createFilter(this.trainingData);
		// Instances filterdInstances = Filter.useFilter(data, stwv);

		LibSVM svm = new LibSVM();
		svm.setKernelType(new SelectedTag(0, LibSVM.TAGS_KERNELTYPE));
		svm.setSVMType(new SelectedTag(0, LibSVM.TAGS_SVMTYPE));
		svm.setProbabilityEstimates(true);
		// svm.buildClassifier(filterdInstances);

		FilteredClassifier filteredClassifier = new FilteredClassifier();
		filteredClassifier.setFilter(stwvFilter);
		filteredClassifier.setClassifier(svm);
		filteredClassifier.buildClassifier(this.trainingData);
		this.classifier = filteredClassifier;

		// predict("nice cool amazing awesome beautiful");
		// predict("this movie is simply awesome");
		// predict("its very bad");
		// predict("Not that great");
	}

	/**
	 * Loads the training data.
	 *
	 * @return the training data
	 * @throws IOException if loading fails
	 */
	private Instances loadTrainingData() throws IOException {
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

	/**
	 * Creates a {@link StringToWordVector} filter with a 3-gram {@link Tokenizer}
	 * and stop word handling.
	 *
	 * @param instances the model which is to be filtered
	 * @return the filter
	 * @throws Exception if filter creation fails
	 */
	private StringToWordVector createFilter(Instances instances) throws Exception {
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMaxSize(3);

		WordsFromFile stopwordsHandler = new WordsFromFile();
		stopwordsHandler.setStopwords(FileUtils.loadFile(resourceLoader,
				dataConfig.getBaseDataDirectory() + dataConfig.getStopWordsDirectory()));

		StringToWordVector stwv = new StringToWordVector();
		stwv.setTokenizer(tokenizer);
		stwv.setTFTransform(true);
		stwv.setIDFTransform(true);
		stwv.setStopwordsHandler(stopwordsHandler);
		stwv.setLowerCaseTokens(true);
		stwv.setInputFormat(instances);
		return stwv;
	}

	@SneakyThrows
	@Override
	public ReviewType predict(String sentence) {
		return ProfileUtils.profileExecution(() -> {
			Instance newInstance = new DenseInstance(2);
			newInstance.setDataset(this.trainingData);
			newInstance.setValue(0, sentence.toLowerCase());
			newInstance.setClassValue(1);
			double result = this.classifier.classifyInstance(newInstance);
			String typeStr = this.trainingData.classAttribute().value((int) result);
			ReviewType predictedType = ReviewType.valueOf(typeStr);
			log.debug("Sentence: {}, predictedType: {}", sentence, predictedType);
			return predictedType;
		});

		// StopWatch sw = new StopWatch();
		// sw.start();
		// Instances clone = new Instances(data);
		// Instance testInstance = createInstance(clone, createClasses(), ReviewType.POSITIVE,
		//     "nice cool amazing awesome beautiful");
		// clone.add(testInstance);
		// Instances filterdTestInstances = Filter.useFilter(clone, stwv);
		// double result = svm.classifyInstance(filterdTestInstances.lastInstance());
		// System.out.println("\n\n\n" + filterdTestInstances.classAttribute().value((int) result) + "\n\n\n");
		// sw.stop();
		// System.out.println(sw.prettyPrint());
	}

}
