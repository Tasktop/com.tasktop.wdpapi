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

#pragma comment(lib,"crypt32.lib")


#include <jni.h>
#include <stdio.h>
#include <windows.h>
#include <wincrypt.h>
#include "com_tasktop_wdpapi_WindowsDataProtectionNativeWrapper.h"


DWORD createCryptProtectDataFlags(jboolean localMachine) {
    if (localMachine) {
        return CRYPTPROTECT_UI_FORBIDDEN | CRYPTPROTECT_LOCAL_MACHINE;
    }
    return CRYPTPROTECT_UI_FORBIDDEN;
}

DATA_BLOB createDataBlobFromBytes(JNIEnv *env, jbyteArray bytes) {
    DATA_BLOB blob;

    if (bytes == NULL) {
        blob.pbData = NULL;
        blob.cbData = 0;
    } else {
	    blob.pbData = (BYTE *) env->GetByteArrayElements(bytes, JNI_FALSE);
	    blob.cbData = (DWORD) env->GetArrayLength(bytes);
    }

    return blob;
}

jbyteArray createBytesFromDataBlob(JNIEnv *env, DATA_BLOB blob) {
    jbyteArray bytes = env->NewByteArray(blob.cbData);
    env->SetByteArrayRegion(bytes, 0, blob.cbData, (const jbyte *) blob.pbData);
    return bytes;
}


void freeDataBlobBytes(JNIEnv *env, jbyteArray bytes, DATA_BLOB blob) {
    if (bytes != NULL) {
        env->ReleaseByteArrayElements(bytes, (jbyte*) blob.pbData, JNI_ABORT);
    }
}

void throwWindowsDataProtectionException(JNIEnv *env) {
	DWORD errorCode = GetLastError();
    LPVOID messageBuffer;
	DWORD languageId = MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT);
	DWORD messageFlags = FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS;

    FormatMessage(messageFlags, NULL, errorCode, languageId, (LPTSTR) &messageBuffer, 0, NULL);

	size_t messageSize = strlen((const char*)messageBuffer)+30;
	char* string = (char*) malloc(messageSize);
	snprintf(string, messageSize, "%s (error code %d)", messageBuffer, errorCode);

    jclass exceptionType = env->FindClass("com/tasktop/wdpapi/WindowsDataProtectionException");
    env->ThrowNew(exceptionType, string);

	free(string);

    env->DeleteLocalRef(exceptionType);
    LocalFree(messageBuffer);
 }

/*
 * Class:     com_tasktop_wdpapi_WindowsDataProtectionNativeWrapper
 * Method:    encrypt
 * Signature: ([B[BZ)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_tasktop_wdpapi_WindowsDataProtectionNativeWrapper_encrypt
  (JNIEnv *env, jclass, jbyteArray inputBytes, jbyteArray entropyBytes, jboolean localMachine) {

  DATA_BLOB result;
  DATA_BLOB input = createDataBlobFromBytes(env,inputBytes);
  DATA_BLOB entropy = createDataBlobFromBytes(env,entropyBytes);
  DWORD flags = createCryptProtectDataFlags(localMachine);

  BOOL success = CryptProtectData(&input, L"Tasktop",&entropy, NULL, NULL,flags, &result);

  freeDataBlobBytes(env,inputBytes,input);
  freeDataBlobBytes(env,entropyBytes,entropy);

  if (!success) {
    throwWindowsDataProtectionException(env);
    return NULL;
  }
  return createBytesFromDataBlob(env,result);
}

/*
 * Class:     com_tasktop_wdpapi_WindowsDataProtectionNativeWrapper
 * Method:    decrypt
 * Signature: ([B[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_tasktop_wdpapi_WindowsDataProtectionNativeWrapper_decrypt
  (JNIEnv *env, jclass, jbyteArray inputBytes, jbyteArray entropyBytes) {

  DATA_BLOB result;
  DATA_BLOB input = createDataBlobFromBytes(env,inputBytes);
  DATA_BLOB entropy = createDataBlobFromBytes(env,entropyBytes);

  BOOL success = CryptUnprotectData(&input, (LPWSTR *) NULL, &entropy, NULL, NULL, CRYPTPROTECT_UI_FORBIDDEN, &result);

  freeDataBlobBytes(env,inputBytes,input);
  freeDataBlobBytes(env,entropyBytes,entropy);

  if (!success) {
    throwWindowsDataProtectionException(env);
    return NULL;
  }
  return createBytesFromDataBlob(env,result);
}
