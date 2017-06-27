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

import static java.util.Objects.requireNonNull;

/**
 * Java API for Windows Data Protection {@code CryptProtectData} and
 * {@code CryptUnprotectData}.
 * <p>
 * Must call {@link WindowsDataProtectionLoader#loadLibrary()} prior to use.
 * </p>
 * 
 * @see WindowsDataProtectionLoader
 */
public class WindowsDataProtection {

	/**
	 * Encrypts the given input.
	 *
	 * @param input
	 *            the encrypted input
	 * @param entropy
	 *            the entropy
	 * @param localMachine
	 *            true if the encryption should be machine-scoped, otherwise
	 *            false
	 * @return the decrypted value
	 * @throws WindowsDataProtectionException
	 *             if the operation failed
	 */
	public static byte[] encrypt(byte[] input, byte[] entropy, boolean localMachine)
			throws WindowsDataProtectionException {
		requireNonNull(input, "Must provide input");
		requireNonNull(entropy, "Must provide entropy");

		return WindowsDataProtectionNativeWrapper.encrypt(input, entropy, localMachine);
	}

	/**
	 * Decrypts the given input.
	 *
	 * @param input
	 *            the encrypted input
	 * @param entropy
	 *            the entropy
	 * @return the decrypted value
	 * @throws WindowsDataProtectionException
	 *             if the operation failed
	 */
	public static byte[] decrypt(byte[] input, byte[] entropy) throws WindowsDataProtectionException {
		requireNonNull(input, "Must provide input");
		requireNonNull(entropy, "Must provide entropy");

		return WindowsDataProtectionNativeWrapper.decrypt(input, entropy);
	}

}
