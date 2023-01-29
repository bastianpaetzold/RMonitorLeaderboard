package com.zacharyfox.rmonitor.config;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigurationManager {

	private static ConfigurationManager instance;

	private Properties properties;
	private Path propertiesPath;

	public ConfigurationManager() {
		properties = new Properties();
		propertiesPath = Paths.get("LeaderBoard.properties");

		loadConfig();
	}

	public void loadConfig() {
		if (Files.exists(propertiesPath)) {
			try (Reader reader = Files.newBufferedReader(propertiesPath)) {
				properties.load(reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveConfig() {
		try (BufferedWriter writer = Files.newBufferedWriter(propertiesPath)) {
			properties.store(writer, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getConfig(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	
	public int getConfig(String key, int defaultValue) {
		return Integer.parseInt(properties.getProperty(key, Integer.toString(defaultValue)));
	}
	
	public void setConfig(String key, String value) {
		properties.setProperty(key, value);
		saveConfig();
	}
	
	public void setConfig(String key, int value) {
		setConfig(key, Integer.toString(value));
	}

	public static ConfigurationManager getInstance() {
		if (instance == null) {
			instance = new ConfigurationManager();
		}
	
		return instance;
	}
}
