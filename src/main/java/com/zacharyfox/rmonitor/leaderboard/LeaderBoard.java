package com.zacharyfox.rmonitor.leaderboard;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

import javax.swing.UIManager;

import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.config.ConfigurationManager;
import com.zacharyfox.rmonitor.leaderboard.frames.ConnectFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.MainFrame;
import com.zacharyfox.rmonitor.utils.JsonServer;
import com.zacharyfox.rmonitor.utils.Player;
import com.zacharyfox.rmonitor.utils.Recorder;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "leaderboard", mixinStandardHelpOptions = true, version = "leaderboard 0.0.1-SNAPSHOT")
public class LeaderBoard implements Callable<Integer> {

	@Option(names = "--headless", description = "Start the application in headless mode, without GUI.")
	private boolean headless;

	@ArgGroup(exclusive = false)
	private ClientGroup clientGroup;

	static class ClientGroup {

		@Option(names = { "-c", "--start-client" }, required = true, description = "Start the client and connect it to a remote race monitor.")
		boolean start;

		@Option(names = { "--host", "--remote-host" }, description = "Remote host to connect to.")
		Optional<String> host;

		@Option(names = { "--port", "--remote-port" }, description = "Remote port to connect to.")
		Optional<Integer> port;
	}

	@ArgGroup(exclusive = false)
	private ServerGroup serverGroup;

	static class ServerGroup {

		@Option(names = { "-s", "--start-server" }, required = true, description = "Start the web server.")
		boolean start;

		@Option(names = "--server-host", description = "Host to bind the web server to.")
		Optional<String> host;

		@Option(names = "--server-port", description = "Port to bind the web server to.")
		Optional<Integer> port;
	}

	@ArgGroup(exclusive = false)
	private PlayerGroup playerGroup;

	static class PlayerGroup {

		@Option(names = { "-p", "--start-player" }, required = true, description = "Start the player.")
		boolean start;

		@Option(names = "--player-port", description = "Port on which the player should listen for client connections.")
		Optional<Integer> port;

		@Option(names = "--player-file", required = true, description = "Path to the file which the player should use to read messages from.")
		Optional<Path> path;
	}
	
	@ArgGroup(exclusive = false)
	private RecorderGroup recorderGroup;

	static class RecorderGroup {

		@Option(names = { "-r", "--start-recorder" }, required = true, description = "Start the Recorder.")
		boolean start;

		@Option(names = "--recorder-file", required = true, description = "Path to the file which the recorder should use to write messages into.")
		Optional<Path> path;
	}

	@Override
	public Integer call() throws Exception {
		 ConfigurationManager.getInstance().loadConfig();

		if (serverGroup != null && serverGroup.start) {
			JsonServer server = JsonServer.getInstance();
			serverGroup.host.ifPresent(server::setHost);
			serverGroup.port.ifPresent(server::setPort);
			server.start();
		}
		
		if (playerGroup != null && playerGroup.start) {
			Player player = Player.getInstance();
			playerGroup.port.ifPresent(player::setPort);
			playerGroup.path.ifPresent(player::setFilePath);
			player.start();
		}
		
		if (recorderGroup != null && recorderGroup.start) {
			Recorder recorder = Recorder.getInstance();
			recorderGroup.path.ifPresent(recorder::setPath);
			recorder.start();
		}

		if (headless || (clientGroup != null && clientGroup.start)) {
			RMonitorClient client = RMonitorClient.getInstance();
			if (clientGroup != null) {
				clientGroup.host.ifPresent(client::setHost);
				clientGroup.port.ifPresent(client::setPort);
			}
			client.start();
		}

		if (!headless) {
			startGUI();
		}

		return 0;
	}

	private void startGUI() {
		try {
			System.setProperty("apple.awt.fullscreenhidecursor", "true");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "RMonitorLeaderboard");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			MainFrame window = new MainFrame();
			window.setVisible(true);
			ConnectFrame newFrame = ConnectFrame.getInstance();
			newFrame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int status = new CommandLine(new LeaderBoard()).execute(args);
		if (status != 0) {
			System.exit(status);
		}
	}

}
