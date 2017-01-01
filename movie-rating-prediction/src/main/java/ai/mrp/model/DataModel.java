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

package ai.mrp.model;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * {@link DataModel} is a concrete model for storing trained data sets.
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
public class DataModel<C extends Enum<C>> {

	private EnumMap<C, ClassDataModel> classData;
	private EnumMap<C, Integer> numWords;

	public DataModel(Class<C> clazz) {
		this.classData = new EnumMap<>(clazz);
		this.numWords = new EnumMap<>(clazz);
	}

	public int total(C type) {
		Integer classWords = numWords.get(type);
		if (classWords == null) {
			classWords = 0;
		}
		return classWords;
	}

	public int get(C type, String word) {
		ClassDataModel classDataModel = getOrCreate(type);
		Integer count = classDataModel.counts.get(word);
		if (count == null) {
			count = 0;
		}
		return count;
	}

	public void put(C type, String word) {
		Integer count = get(type, word);
		count++;
		ClassDataModel classDataModel = getOrCreate(type);
		classDataModel.counts.put(word, count);

		Integer classWords = numWords.get(type);
		if (classWords == null) {
			classWords = 0;
		}
		classWords++;
		numWords.put(type, classWords);
	}

	private ClassDataModel getOrCreate(C type) {
		ClassDataModel classDataModel = classData.get(type);
		if (classDataModel == null) {
			classDataModel = new ClassDataModel();
			classData.put(type, classDataModel);
		}
		return classDataModel;
	}

	@Data
	private static final class ClassDataModel {

		private Map<String, Integer> counts;

		private ClassDataModel() {
			this.counts = new HashMap<>();
		}

	}

}
