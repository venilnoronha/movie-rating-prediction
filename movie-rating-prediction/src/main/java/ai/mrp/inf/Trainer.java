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

import ai.mrp.model.DataModel;

/**
 * Implementations of {@link Trainer} provide methods for training and creating
 * {@link DataModel}s.
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
public interface Trainer<C extends Enum<C>> {

	/**
	 * Trains and creates a {@link DataModel}.
	 * 
	 * @return the {@link DataModel}
	 * @throws Exception if training fails
	 */
	public DataModel<C> train() throws Exception;

}
