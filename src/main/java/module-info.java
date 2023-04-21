module com.zacharyfox.rmonitor.leaderboard {

	exports com.zacharyfox.rmonitor.leaderboard;
	exports com.zacharyfox.rmonitor.leaderboard.frames;
	exports com.zacharyfox.rmonitor.utils;
	exports com.zacharyfox.rmonitor.message;
	exports com.zacharyfox.rmonitor.entities;

	requires com.google.gson;
	requires transitive java.desktop;
	requires java.prefs;
	requires org.eclipse.jetty.server;
	requires com.miglayout.swing;
	requires info.picocli;
	requires ch.qos.logback.classic;
	requires org.apache.commons.text;

	opens com.zacharyfox.rmonitor.leaderboard to info.picocli;
	opens com.zacharyfox.rmonitor.entities to com.google.gson;
}