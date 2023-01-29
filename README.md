RMonitor Leaderboard
====================

RMonitor Leaderboard is a swing application designed for displaying race summary information from an rmonitor feed over
tcp, such as the one provided by the MyLaps Orbits software.

![Screenshot](/docs/screenshot.png)

Protocol information was taken from here: http://www.imsatiming.com/software/Protocols/AMB%20RMonitor%20Timing%20Protocol.pdf

It displays summary information and a sortable table of the current competitors in the race. In addition, it will
provide an estimate of time or laps remaining (based on time to go / laps to go received from the rmonitor feed.)

In addition to the swing application, this repository contains library packages for handling rmonitor feed data.

Building the Application
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

Running the Application
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


Library Packages
----------------

### com.zacharyfox.rmonitor.entities

This package contains the models for the race and competitors.

### com.zacharyfox.rmonitor.message

This package contains classes for each type of message provided in the protocol, and a factory for creating the objects
from an ascii string. Example usage below (returns a Heartbeat):

	import com.zacharyfox.rmonitor.message.*
	
	String line = "$F,14,\"00:12:45\",\"13:34:23\",\"00:09:47\",\"Green\"";
	RMonitorMessage message = Factory.getMessage(line);

### com.zacharyfox.rmonitor.utils

#### Duration

Duration takes time values as strings ("00:01:23.456"), integers (milliseconds), or floats (seconds) supplied by the
feed and stores them as milliseconds.

	import com.zacharyfox.rmonitor.utils.Duration
	
	Duration duration = new Duration("00:01:23.456");

#### Connection

Connection extends Socket and contains a BufferedLineReader.

	import com.zacharyfox.rmonitor.utils.Connection;
	
	String ip = "127.0.0.1";
	Integer port = 50000;
	Connection connection = new Connection(ip, port);
	
	while ((line = connection.readLine()) != null) {
		System.out.println(line);
	}

