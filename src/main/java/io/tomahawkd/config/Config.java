package io.tomahawkd.config;

import io.tomahawkd.config.sources.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Config for configuring preference from commandline.
 *
 * You may need {@link io.tomahawkd.config.annotation.SourceFrom} to declare
 * the config source
 */
public interface Config {

	/**
	 * Add new delegate for config
	 *
	 * @param delegate delegate
	 */
	void addDelegate(@NotNull ConfigDelegate delegate);

	/**
	 * Get all delegates
	 * @return delegates list
	 */
	List<ConfigDelegate> getDelegates();

	/**
	 * Get specific delegate by type.
	 *
	 * @param type Class of ArgDelegate
	 * @param <T> subclass of ArgDelegate
	 * @return delegate or null if not found
	 */
	@Nullable
	<T extends ConfigDelegate> T getDelegateByType(@NotNull Class<T> type);

	/**
	 * Get specific delegate by type string.
	 * For those which cannot access its type class among different extensions.
	 * You may use {@link ConfigDelegate#getField(String, Class)} for field data
	 * acquirement.
	 *
	 * @param type full name type string including package
	 * @return delegate or null if not found
	 */
	@Nullable
	ConfigDelegate getDelegateByString(@NotNull String type);

	/**
	 * Config parse from source.
	 * You could delegate parse to ConfigDelegate or parse it directly
	 */
	void parse();

	/**
	 * Config parse from source.
	 * You could delegate parse to ConfigDelegate or parse it directly
	 *
	 * @param source config source
	 */
	void parse(Source source);
}
