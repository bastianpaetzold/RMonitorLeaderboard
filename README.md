# RMonitor Leaderboard

RMonitor Leaderboard is a swing application designed for displaying race summary information from an rmonitor feed over
tcp, such as the one provided by the MyLaps Orbits software.

![Screenshot](/docs/screenshot.png)

Protocol information was taken from here: <http://www.imsatiming.com/software/Protocols/AMB%20RMonitor%20Timing%20Protocol.pdf>

It displays summary information and a sortable table of the current competitors in the race. In addition, it will
provide an estimate of time or laps remaining (based on time to go / laps to go received from the rmonitor feed.)

Additionally the application supports a headless mode in order to run on a system without GUI. This can be useful in scenarios where you just want to receive the rmonitor feed "forward" it using the web server module without displaying it.

## Download

Pre-packaged binaries can be downloaded from <https://github.com/bastianpaetzold/RMonitorLeaderboard/releases>

The following packages are provided:

- rmonitorleaderboard_*.jar: Executable standalone JAR (requires a Java 17 installation to run).
- rmonitorleaderboard_*_linux.zip: Linux specific application image which does not need a separate Java installation to run.
- rmonitorleaderboard_*_windows.zip: Windows specific application image which does not need a separate Java installation to run.
- rmonitorleaderboard_*_linux.deb: Debian package which installs the application image.
- rmonitorleaderboard_*_windows.msi: Windows MSI package which installs the application image.

## (De)Installation

### Installer

#### Windows

Executing the MSI package will start a wizard which can be used to configure the installation (e.g. install location or shortcuts).

In order to remove the installation run the installer again and select _Remove_.

#### Linux

To install the debian package (using apt) run the following command:

```bash
apt install <path/to/package>
```

To remove it run:

```bash
apt remove rmonitorleaderboard
```

### Application Image

Download and extract the appliation image for your OS.

Depending on your OS there will be an executable to run the application either located in the root directory of the application image or the _bin_ subdirectory.

### Standalone JAR

Install Java 17 (e.g. from <https://adoptium.net/temurin/releases/>) and download the standalone JAR.

## Usage

```text
Usage: rmonitorleaderboard [-hV] [--headless] [-c [--host=<host>]
                           [--port=<port>]
                           [--client-stream-encoding=<streamEncoding>]
                           [--client-max-retries=<maxRetries>]
                           [--client-retry-timeout=<retryTimeout>]] [-s
                           [--server-host=<host>] [--server-port=<port>]] [-p
                           [--player-speedup=<speedup>] [--player-port=<port>]
                           --player-file=<filePath>
                           [--player-file-encoding=<fileEncoding>]
                           [--player-stream-encoding=<streamEncoding>]] [-r
                           --recorder-file=<filePath>
                           [--recorder-file-encoding=<fileEncoding>]]
  -c, --start-client         Start the client and connect it to a remote race
                               monitor.
      --client-max-retries=<maxRetries>
                             Number of retries if the remote connection fails.
                               Set to -1 for unlimited retries. Default: 3
      --client-retry-timeout=<retryTimeout>
                             Timeout in ms between retries. Default: 10000
      --client-stream-encoding=<streamEncoding>
                             Encoding of the stream client receives. Default:
                               Default encoding on this system (JVM)
  -h, --help                 Show this help message and exit.
      --headless             Start the application in headless mode, without
                               GUI.
      --host, --remote-host=<host>
                             Remote host to connect to. Default: 127.0.0.1
  -p, --start-player         Start the player.
      --player-file=<filePath>
                             Path to the file which the player should use to
                               read messages from.
      --player-file-encoding=<fileEncoding>
                             Encoding of the file that the player reads the
                               messages from. Default: Default encoding on this
                               system (JVM)
      --player-port=<port>   Port on which the player should listen for client
                               connections. Default: 50000
      --player-speedup=<speedup>
                             Speedup that the player should use when playing
                               the messages. Default: 2
      --player-stream-encoding=<streamEncoding>
                             Encoding of the stream that the player plays to
                               the client. Default: Default encoding on this
                               system (JVM)
      --port, --remote-port=<port>
                             Remote port to connect to. Default: 50000
  -r, --start-recorder       Start the Recorder.
      --recorder-file=<filePath>
                             Path to the file which the recorder should use to
                               write messages into.
      --recorder-file-encoding=<fileEncoding>
                             Encoding of the file that the recorder uses to
                               write messages. Default: Default encoding on
                               this system (JVM)
  -s, --start-server         Start the web server.
      --server-host=<host>   Host to bind the web server to. Default: 0.0.0.0
      --server-port=<port>   Port to bind the web server to. Default: 8080
  -V, --version              Print version information and exit.
```

## Building From Source

Requirements:

- Java 17
- Maven 3

In order to build the application run the following command:

```bash
mvn clean package
```

This creates the following files in the  _target_  directory:

- leaderboard-*.jar: Executable standalone JAR (requires an Java 17 installation to run).
- leaderboard-*-app-image.zip: A zipped application image which can be run without a Java installation (OS specific).
- *.deb/exe: An installer which contains the application image (OS specific)

## Credits

The original work was done by [Zachary Fox](https://github.com/zacharyfox) with some improvements by [Kai Höfler](https://github.com/kaihoefler).

## License

RMonitorLeaderboard is released under version of the [GNU General Public License](https://www.gnu.org/licenses/gpl-3.0.html).
