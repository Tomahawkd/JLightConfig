package io.tomahawkd.config.commandline;

import io.tomahawkd.config.sources.ConfigSource;

public class CommandlineConfigSource implements ConfigSource {

	private String[] data;

	public void setData(String[] data) {
		this.data = data;
	}

	@Override
	public String[] getData() {
		return data;
	}
}
