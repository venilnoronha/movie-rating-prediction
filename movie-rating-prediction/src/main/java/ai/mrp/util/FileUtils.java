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

package ai.mrp.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.ResourceLoader;

import lombok.SneakyThrows;

/**
 * {@link FileUtils} provides utilities for file-system I/O.
 *
 * @author Donnabell Dmello <ddmello@usc.edu>
 * @author Venil Noronha <vnoronha@usc.edu>
 */
public class FileUtils {

	private FileUtils() { }

	/**
	 * Loads a {@link File} resource from the given path.
	 *
	 * @param resourceLoader a {@link ResourceLoader} instance
	 * @param path the path of the file/directory to laod
	 * @return the {@link File} instance
	 * @throws IOException if file loading fails
	 */
	public static File loadFile(ResourceLoader resourceLoader, String path) throws IOException {
		return resourceLoader.getResource("classpath:" + path).getFile();
	}

	/**
	 * Reads and returns all lines of a file at a given {@link Path}.
	 *
	 * @param path the path of the file to be read
	 * @return the lines of the file
	 */
	@SneakyThrows
	public static List<String> readLines(Path path) {
		return Files.readAllLines(path);
	}

}
