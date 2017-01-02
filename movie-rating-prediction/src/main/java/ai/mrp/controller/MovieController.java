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

package ai.mrp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.mrp.inf.ClassificationEngine;

/**
 * Implements logic for controlling movie review data source. 
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
@RestController
@RequestMapping(path = "movie")
public class MovieController {

	@Autowired
	private ClassificationEngine engine;

	/**
	 * Updates the data source with the movie name. 
	 * 
	 * @param name the name of the movie
	 */
	@RequestMapping(path = "update")
	public void update(@RequestParam("name") String name) {
		String tag = "#" + name.replaceAll(" ", "").toLowerCase();
		engine.update(tag);
	}

}
