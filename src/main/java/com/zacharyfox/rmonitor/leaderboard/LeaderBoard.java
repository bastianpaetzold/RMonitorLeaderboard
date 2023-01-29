package com.zacharyfox.rmonitor.leaderboard;

import java.util.Optional;
import java.util.concurrent.Callable;

import javax.swing.UIManager;

import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.leaderboard.frames.ConnectFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.MainFrame;
import com.zacharyfox.rmonitor.utils.JsonServer;

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

	@Override
	public Integer call() throws Exception {
		if (headless || (clientGroup != null && clientGroup.start)) {
			RMonitorClient client = RMonitorClient.getInstance();
			if (clientGroup != null) {
				clientGroup.host.ifPresent(client::setHost);
				clientGroup.port.ifPresent(client::setPort);
			}
			client.start();
		}

		if (serverGroup != null && serverGroup.start) {
			JsonServer server = new JsonServer();
			serverGroup.host.ifPresent(server::setHost);
			serverGroup.port.ifPresent(server::setPort);
			server.start();
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
			MainFrame window = new MainFrame("LeaderBoard.properties");
			window.setVisible(true);
			ConnectFrame newFrame = ConnectFrame.getInstance(window);
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
