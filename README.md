RMonitor Leaderboard
====================

RMonitor Leaderboard is a swing application designed for displaying race summary information from an rmonitor feed over
tcp, such as the one provided by the MyLaps Orbits software.

![Screenshot](/docs/screenshot.png)

Protocol information was taken from here: http://www.imsatiming.com/software/Protocols/AMB%20RMonitor%20Timing%20Protocol.pdf

It displays summary information and a sortable table of the current competitors in the race. In addition, it will
provide an estimate of time or laps remaining (based on time to go / laps to go received from the rmonitor feed.)

Additionally the application supports a headless mode in order to run on a system without GUI. This can be useful in scenarios where you just want to receive the rmonitor feed "forward" it using the web server module without displaying it.

Building the application
------------------------

Requirements:
- Java 17
- Maven 3

Running

```
mvn clean package
```

produces beside others the following two files in the  _target_  directory:

- leaderboard-*.jar: Executable standalone JAR (requires Java 17 to run).
- leaderboard-*-jlink.zip: A so-called  _Java Run-Time Image_  (doesn't require Java to run). It is platform dependent and therefore only runs on the platform it was build for.

Downloading pre-built packages
------------------------------

### Releases

TODO

### Pre-release versions

Pre-release versions from the develop branch are available in the [package repository](https://github.com/bastianpaetzold/RMonitorLeaderboard/packages/1787619/versions).

Please note that there are no guarantees regarding the functionality of those versions as they can easily break between builds. Therefore don't use them in an "productive" environment.

Running the application
------------------------

### Standalone JAR

Requirements:
- Java 17

The standalone JAR can be executed directly using Java, either via the GUI (depends on your OS) or via the following command:

```
java -jar leaderboard.jar
```

### Java Run-Time Image

The image is just a zip file containing a directory structure that contains everything to run the application (no external Java required). Extract it somewhere and run the launcher script located in the  _bin_  directory. The name might differ depending on the OS it was build for. E.g. on Windows it is called  *launch_loaderboard.bat*.

Credits
-------

The original work was done by [Zachary Fox](https://github.com/zacharyfox) with some improvements by [Kai Höfler](https://github.com/kaihoefler).

License
-------

RMonitorLeaderboard is released under version of the [GNU General Public License](https://www.gnu.org/licenses/gpl-3.0.html).
