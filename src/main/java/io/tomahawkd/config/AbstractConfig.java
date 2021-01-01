package io.tomahawkd.config;

import io.tomahawkd.config.annotation.HiddenField;
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
	}

	@Override
	public void addDelegate(@NotNull ConfigDelegate delegate) {
		delegates.add(delegate);
	}

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

	@Override
	@Nullable
	public ConfigDelegate getDelegateByString(@NotNull String type) {
		Objects.requireNonNull(type);
		for (ConfigDelegate delegate : getDelegates()) {
			if (type.equals(delegate.getClass().getName())) return delegate;
		}
		return null;
	}

	@Override
	public void postParsing() {
		for (ConfigDelegate delegate : getDelegates()) {
			delegate.postParsing();
		}
	}

	protected final List<ConfigDelegate> getDelegates() {
		return delegates;
	}

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
