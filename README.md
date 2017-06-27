Windows Data Protection API for Java
====================================

This project provides a minimal Java wrapper around the [Windows Data Protection](https://msdn.microsoft.com/en-us/library/ms995355.aspx) API with a permissive license.  

How to Use
==========

````
// at least once in your application
WindowsDataProtectionLoader.loadLibrary();


// protect some data
byte[] dataToProtect = "some super secret data".getBytes(StandardCharsets.UTF_8);
byte[] entropy = "some entropy".getBytes(StandardCharsets.UTF_8);

byte[] protectedData = WindowsDataProtection.encrypt(dataToProtect, entropy, true);

// ... later
byte[] unprotectedData = WindowsDataProtection.decrypt(protectedData, entropy);
String originalSecret = new String(unprotectedData, StandardCharsets.UTF_8);
````

DLL Loading
===========

This library loads native code in the form of a Windows DLL.

DLL loading is first attempted using the JNI path.  If the DLL is not found on the JNI path, it's extracted from the jar to a local temporary folder and loaded from there.  To avoid having the DLL extracted to a temporary folder, manually extract the DLL from the jar file to the desired location on your JNI path and then use the library.

Building
========

From the commandline:

`mvn clean package`

How To Release
--------------

Due to the platform-dependent nature of the build, releases must be done from a Windows machine.

Note that build will fail if sources are on a shared folder; if necessary, copy Git repository to a drive such as C:

From GitBash on Windows:

````
mvn -Possrh -Psign -Darguments=-Dgpg.passphrase=thesecret release:clean release:prepare -DpushChanges=false -DlocalCheckout=false
mvn -Possrh -Psign -Darguments=-Dgpg.passphrase=thesecret release:perform -DpushChanges=false -DlocalCheckout=false
````

Then push changes:

````
git push
````

License
=======

Copyright (c) 2017 Tasktop Technologies

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
