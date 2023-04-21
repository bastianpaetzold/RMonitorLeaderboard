for /f %%i in ('m2-location.bat') do set M2_PATH=%%i

call mvn dependency:get -DgroupId=org.apache.commons -DartifactId=commons-lang3 -Dversion=3.12.0 -Dpackaging=jar
call mvn dependency:get -DgroupId=org.apache.commons -DartifactId=commons-text -Dversion=1.10.0 -Dpackaging=jar

@echo on

set COMMONS_LANG_DIR=%M2_PATH%\org\apache\commons\commons-lang3\3.12.0
jdeps --generate-module-info . %COMMONS_LANG_DIR%\commons-lang3-3.12.0.jar
javac --patch-module org.apache.commons.lang3=%COMMONS_LANG_DIR%\commons-lang3-3.12.0.jar org.apache.commons.lang3\module-info.java
jar uf %COMMONS_LANG_DIR%\commons-lang3-3.12.0.jar -C org.apache.commons.lang3 module-info.class

rmdir org.apache.commons.lang3 /s /q

set COMMONS_TEXT_DIR=%M2_PATH%\org\apache\commons\commons-text\1.10.0
jdeps --module-path %COMMONS_LANG_DIR%\commons-lang3-3.12.0.jar --generate-module-info . %COMMONS_TEXT_DIR%\commons-text-1.10.0.jar
javac --module-path %COMMONS_LANG_DIR%\commons-lang3-3.12.0.jar --patch-module org.apache.commons.text=%COMMONS_TEXT_DIR%\commons-text-1.10.0.jar org.apache.commons.text\module-info.java
jar uf %COMMONS_TEXT_DIR%\commons-text-1.10.0.jar -C org.apache.commons.text module-info.class

rmdir org.apache.commons.text /s /q