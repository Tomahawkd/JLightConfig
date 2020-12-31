package io.tomahawkd.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * Config for configuring preference from commandline.
 */
public interface Config {

	/**
	 * Add new delegate for parsing additional commandline args
	 *
	 * @param delegate arg parsing delegate
	 */
	void addDelegate(@NotNull ConfigDelegate delegate);

	/**
	 * Get specific delegate by type.
	 *
	 * @param type Class of ArgDelegate
	 * @param <T> subclass of ArgDelegate
	 * @return delegate
	 * @throws NoSuchElementException Throws exception when the specific type of delegate is not fond
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
	 * @return delegate
	 */
	@Nullable
	ConfigDelegate getDelegateByString(@NotNull String type);

	/**
	 * Parse commandline args
	 */
	void parse();
}
