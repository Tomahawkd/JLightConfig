package io.tomahawkd.config;

/**
 * Config paring delegate
 *
 * You may need {@link io.tomahawkd.config.annotation.BelongsTo} to declare
 * this delegate belongs to which config
 */
public interface ConfigDelegate {

	/**
	 * apply self to config
	 *
	 * @param config config to apply
	 */
	void applyDelegate(Config config);

	/**
	 * pre config and apply default settings (which may needs calculation)
	 */
	void preConfig();

	/**
	 * post parsing procedure after all arguments is applied to
	 * correspond fields.
	 */
	void postParsing();

	/**
	 * Acquire field data. Generally used after {@link Config#getDelegateByString(String)}
	 * This may usually used when you could not access the type of the class.
	 *
	 * @param key field name
	 * @param type field type
	 * @param <T> field class type
	 * @return field data
	 */
	<T> T getField(String key, Class<T> type);
}
