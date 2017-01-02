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

package ai.mrp.inf;

/**
 * Implementations of {@link Trainer} provide methods for training and creating
 * data sets.
 * 
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 *
 * @param <T> the type of trained data set
 */
public interface Trainer<T> {

	/**
	 * Trains and creates a data set.
	 * 
	 * @return the data set
	 * @throws Exception if training fails
	 */
	public T train() throws Exception;

}
