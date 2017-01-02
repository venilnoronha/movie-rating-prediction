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

import java.util.Date;
import java.util.EnumMap;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Verbatim data transfer object.
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@Data
@AllArgsConstructor
public class Verbatim {

	/** This is the source of the verbatim. E.g. Twitter. */
	private String source;
	
	/** This is the actual message content. */
	private String text;
	
	/** This is the author who sent the message. */
	private String authorName;
	
	/** This is the user name of the author for the source type. */
	private String userName;
	
	/** This is the user picture of the author for the source type. */
	private String userPic;
	
	/** This is the date on which the message was posted on the source. */
	private Date datePosted;
	
	/** This is the sentiment map with key as different classifier types. */
	private EnumMap<ClassifierType, ReviewType> sentiment;

}
