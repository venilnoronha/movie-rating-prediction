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

package ai.mrp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * {@link DataConfig} provides configuration that is necessary for training
 * classifiers.
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@Data
@Configuration
@ConfigurationProperties("data")
public class DataConfig {

	/** The base data directory. */
	private String baseDataDirectory;

	/** The positive reviews sub-directory. */
	private String positiveReviewsDirectory;

	/** The negative reviews sub-directory. */
	private String negativeReviewsDirectory;

	/** The stop-words sub-directory. */
	private String stopWordsDirectory;

}
