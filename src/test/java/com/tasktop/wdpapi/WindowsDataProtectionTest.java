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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WindowsDataProtectionTest {
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() {
		assumeRuntimeIsWindows();
		WindowsDataProtectionLoader.loadLibrary();
	}

	@Test
	public void encryptNullValue() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide input");
		WindowsDataProtection.encrypt(null, toBytes(""), false);
	}

	@Test
	public void encryptNullEntropy() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide entropy");
		WindowsDataProtection.encrypt(toBytes("a value"), null, false);
	}

	@Test
	public void decryptNullValue() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide input");
		WindowsDataProtection.decrypt(null, toBytes(""));
	}

	@Test
	public void decryptNullEntropy() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide entropy");
		WindowsDataProtection.decrypt(toBytes("a value"), null);
	}

	@Test
	public void encryptsAndDecryptsValues() {
		assertEncryptDecrypt("an input value", toBytes("some entropy"));
	}

	@Test
	public void encryptsAndDecryptsEmptyValues() {
		assertEncryptDecrypt("", toBytes("some entropy"));
	}

	@Test
	public void encryptsAndDecryptsValuesWith0LengthEntropy() {
		assertEncryptDecrypt("an input value", new byte[0]);
	}

	@Test
	public void encryptsAndDecryptsUnicodePlane0Values() {
		assertEncryptDecrypt("Horse Phone ♞☎", toBytes("some entropy"));
		assertEncryptDecrypt("`~!@#$%^&*()_+-=,.<>/?\\[]{}", toBytes("some entropy"));
	}

	@Test
	public void decryptThrowsExceptionOnInvalidInput() {
		byte[] bytes = toBytes("some value");
		byte[] entropy = toBytes("some entropy");

		thrown.expect(WindowsDataProtectionException.class);
		thrown.expectMessage("The parameter is incorrect.");
		thrown.expectMessage("(error code 87)");
		WindowsDataProtection.decrypt(bytes, entropy);
	}

	@Test
	public void decryptThrowsExceptionOnWrongEntropy() {
		String inputValue = "an input value";
		byte[] entropy = toBytes("some entropy");
		byte[] encrypted = WindowsDataProtection.encrypt(toBytes(inputValue), entropy, true);
		byte[] differentEntropy = toBytes("different entropy");

		thrown.expect(WindowsDataProtectionException.class);
		thrown.expectMessage("The data is invalid.");
		thrown.expectMessage("(error code 13)");
		WindowsDataProtection.decrypt(encrypted, differentEntropy);
	}

	private byte[] toBytes(String string) {
		return string.getBytes(StandardCharsets.UTF_8);
	}

	private String toString(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	private void assertEncryptDecrypt(String inputValue, byte[] entropy) {
		byte[] encrypted = WindowsDataProtection.encrypt(toBytes(inputValue), entropy, false);

		assertThat(encrypted).isNotNull();
		assertThat(encrypted).isNotEmpty();

		byte[] decrypted = WindowsDataProtection.decrypt(encrypted, entropy);

		assertThat(toString(decrypted)).isEqualTo(inputValue);
	}

	private void assumeRuntimeIsWindows() {
		boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("windows");
		assumeTrue("only runs on Windows", isWindows);
	}

}
