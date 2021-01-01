package io.tomahawkd.config.commandline;

import io.tomahawkd.config.sources.Source;

public class CommandlineSource implements Source {

	private String[] data;

	public void setData(String[] data) {
		this.data = data;
	}

	@Override
	public String[] getData() {
		return data;
	}
}
