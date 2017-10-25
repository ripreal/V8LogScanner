set PR_PATH=%CD%
SET PR_SERVICE_NAME=v8LogScanner.jar
SET PR_JAR=v8LogScanner.jar
SET START_CLASS=org.v8LogScanner.cmdScanner.AppIntitializer
SET START_METHOD=startService
SET STOP_CLASS=java.lang.System
SET STOP_METHOD=exit
rem ; separated values
SET STOP_PARAMS=0
rem ; separated values
SET JVM_OPTIONS=-Dapp.home=%PR_PATH%
prunsrv.exe //IS//%PR_SERVICE_NAME% --Install="%PR_PATH%\prunsrv.exe" --Jvm=auto --Startup=auto --StartMode=jvm --StartClass=%START_CLASS% --StartMethod=%START_METHOD% --StopMode=jvm --StopClass=%STOP_CLASS% --StopMethod=%STOP_METHOD% ++StopParams=%STOP_PARAMS% --Classpath="%PR_PATH%\%PR_JAR%" --DisplayName="%PR_SERVICE_NAME%" ++JvmOptions=%JVM_OPTIONS%