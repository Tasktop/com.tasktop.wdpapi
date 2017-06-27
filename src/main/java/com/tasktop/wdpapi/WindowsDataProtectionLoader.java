/*******************************************************************************
 * Copyright (c) 2017 Tasktop Technologies.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.tasktop.wdpapi;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Loads native libraries needed for use of {@link WindowsDataProtection}.
 * 
 * @see #loadLibrary()
 */
public class WindowsDataProtectionLoader {

	private static final AtomicBoolean loadAttempted = new AtomicBoolean();

	private static final String LIBRARY_NAME = "tasktop_wdpapi";
	private static final String DLL_FILE_NAME = LIBRARY_NAME + ".dll";
	private static final String DLL_RESOURCE = "/" + DLL_FILE_NAME;

	/**
	 * Loads the native libraries needed for use of
	 * {@link WindowsDataProtection}.
	 * <p>
	 * It's safe to call this method more than once. Only the first invocation
	 * has any effect.
	 * </p>
	 */
	public static void loadLibrary() {
		if (loadAttempted.compareAndSet(false, true)) {
			try {
				System.load(DLL_FILE_NAME);
			} catch (UnsatisfiedLinkError firstError) {
				Path dllFile = locateDllFile();
				System.load(dllFile.toFile().getAbsolutePath());
			}
		}
	}

	private static Path locateDllFile() {
		URL resource = WindowsDataProtectionLoader.class.getResource(DLL_RESOURCE);
		if (resource == null) {
			throw new IllegalStateException(format("Cannot find DLL: {0}", DLL_RESOURCE));
		}
		try {
			Path directory = Files.createTempDirectory(WindowsDataProtectionLoader.class.getSimpleName());
			Path dllFile = directory.resolve(DLL_FILE_NAME);
			try (InputStream in = resource.openStream()) {
				Files.copy(in, dllFile);
			}
			return dllFile;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private WindowsDataProtectionLoader() {
		// prevent instantiation
	}
}
