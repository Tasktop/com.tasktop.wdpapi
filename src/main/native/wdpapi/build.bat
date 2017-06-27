cl -o tasktop_wdpapi.dll com_tasktop_wdpapi_WindowsDataProtectionNativeWrapper.cpp /I "C:\Program Files\Java\jdk1.8.0_131\include" /I "C:\Program Files\Java\jdk1.8.0_131\include\win32" /link /DLL

copy tasktop_wdpapi.dll "..\..\..\..\target\classes" /Y
