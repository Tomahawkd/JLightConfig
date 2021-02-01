package io.tomahawkd.config.sources;

public interface ConfigSource {

	/**
	 * Supplies config data
	 *
	 * @return config data
	 */
	Object getData();
}
