package io.tomahawkd.config;

import io.tomahawkd.config.annotation.HiddenField;
import io.tomahawkd.config.annotation.SourceFrom;
import io.tomahawkd.config.sources.ConfigSource;
import io.tomahawkd.config.sources.SourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractConfig extends AbstractConfigDelegate implements Config {

	@HiddenField
	private final List<ConfigDelegate> delegates;

	public AbstractConfig() {
		delegates = new ArrayList<>();
		if (this.getClass().getAnnotation(SourceFrom.class) == null) {
			throw new RuntimeException(
					"Annotation SourceFrom not found in this class(" + this.getClass().getName() + ")");
		}
	}

	/**
	 * Add new delegate for config
	 *
	 * @param delegate delegate
	 */
	@Override
	public void addDelegate(@NotNull ConfigDelegate delegate) {
		delegates.add(delegate);
	}

	/**
	 * Get specific delegate by type.
	 *
	 * @param type Class of ArgDelegate
	 * @param <T> subclass of ArgDelegate
	 * @return delegate or null if not found
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T extends ConfigDelegate> T getDelegateByType(@NotNull Class<T> type) {
		Objects.requireNonNull(type);
		for (ConfigDelegate delegate : getDelegates()) {
			if (type.equals(delegate.getClass())) return (T) delegate;
		}
		return null;
	}

	/**
	 * Get specific delegate by type string.
	 * For those which cannot access its type class among different extensions.
	 * You may use {@link ConfigDelegate#getField(String, Class)} for field data
	 * acquirement.
	 *
	 * @param type full name type string including package
	 * @return delegate or null if not found
	 */
	@Override
	@Nullable
	public ConfigDelegate getDelegateByString(@NotNull String type) {
		Objects.requireNonNull(type);
		for (ConfigDelegate delegate : getDelegates()) {
			if (type.equals(delegate.getClass().getName())) return delegate;
		}
		return null;
	}

	/**
	 * pre config and apply default settings (which may needs calculation)
	 */
	@Override
	public final void preConfig() {
		selfPreConfig();
		for (ConfigDelegate delegate : getDelegates()) {
			delegate.preConfig();
		}
	}

	/**
	 * For those configs which needs pre-setup, override this function
	 * to get it setup
	 */
	protected void selfPreConfig() {
	}

	/**
	 * Config parse from source.
	 * You could delegate parse to ConfigDelegate or parse it directly
	 */
	@Override
	public final void parse() {
		preConfig();
		parse(SourceManager.get().getSource(
				this.getClass().getAnnotation(SourceFrom.class).value()));
		postParsing();
	}

	/**
	 * Config parse from source.
	 * You could delegate parse to ConfigDelegate or parse it directly
	 *
	 * @param source config source
	 */
	public abstract void parse(@NotNull ConfigSource source);

	/**
	 * post parsing procedure after all arguments is applied to
	 * correspond fields.
	 */
	@Override
	public final void postParsing() {
		selfPostParsing();
		for (ConfigDelegate delegate : getDelegates()) {
			delegate.postParsing();
		}
	}

	/**
	 * For those configs which needs post-setup, override this function
	 * to get it setup
	 */
	protected void selfPostParsing() {
	}

	/**
	 * Get all delegates
	 * @return delegates list
	 */
	@Override
	public final List<ConfigDelegate> getDelegates() {
		return delegates;
	}

	/**
	 * Initialize all config delegate
	 *
	 * @param list config delegate class list
	 */
	void initComponents(@NotNull Set<Class<? extends ConfigDelegate>> list) {
		Objects.requireNonNull(list);
		for (Class<? extends ConfigDelegate> d : list) {
			try {
				// already added
				if (this.getClass().equals(d)) continue;
				Constructor<? extends ConfigDelegate> dc = d.getDeclaredConstructor();
				dc.setAccessible(true);
				ConfigDelegate delegate = dc.newInstance();
				delegate.applyDelegate(this);
			} catch (NoSuchMethodException | SecurityException |
					InstantiationException | IllegalAccessException |
					IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(
						"Construct config delegate " + d.getName() + " failed.", e);
			}
		}
	}
}
