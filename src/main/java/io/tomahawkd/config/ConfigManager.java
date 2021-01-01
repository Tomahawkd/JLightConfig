package io.tomahawkd.config;

import io.tomahawkd.config.annotation.BelongsTo;
import io.tomahawkd.config.util.ClassManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entry for config processing.
 */
public class ConfigManager {

	public static ConfigManager get() {
		return new ConfigManager(null);
	}

	public static ConfigManager get(@Nullable Collection<ClassLoader> classLoaders) {
		return new ConfigManager(classLoaders);
	}

	private ConfigManager(@Nullable Collection<ClassLoader> classLoaders) {

		// skip initialization if already initialized
		if (ManagerInstance.INSTANCE.initialized) return;

		if (classLoaders != null) {
			ManagerInstance.INSTANCE.classLoaders.addAll(classLoaders);
		}
		Set<Class<? extends ConfigDelegate>> delegates = ClassManager.createManager(classLoaders)
				.loadClassesWithAnnotation(ConfigDelegate.class, null, BelongsTo.class);

		ClassManager.createManager(classLoaders).loadClasses(AbstractConfig.class, null).stream()
				// filter abstract classes
				.filter(c -> !Modifier.isAbstract(c.getModifiers()))
				.map(c -> {

					try {
						// construct config
						Constructor<? extends AbstractConfig> constructor = c.getDeclaredConstructor();
						constructor.setAccessible(true);
						AbstractConfig config = constructor.newInstance();

						// add delegates to config
						config.initComponents(delegates.stream()
								.filter(d -> config.getClass().equals(d.getAnnotation(BelongsTo.class).value()))
								.collect(Collectors.toSet()));

						return config;
					} catch (InstantiationException | InvocationTargetException |
							NoSuchMethodException | IllegalAccessException e) {
						throw new RuntimeException("Construct config " + c.getName() + " failed.", e);
					}
				}).forEach(ManagerInstance.INSTANCE.configs::add);

		ManagerInstance.INSTANCE.initialized = true;
	}

	private enum ManagerInstance {

		INSTANCE;

		private final List<Config> configs;
		private final Set<ClassLoader> classLoaders;
		private boolean initialized = false;

		ManagerInstance() {
			configs = new ArrayList<>();
			classLoaders = new HashSet<>();
			classLoaders.add(ClasspathHelper.staticClassLoader());
			classLoaders.add(ClasspathHelper.contextClassLoader());
		}
	}

	/**
	 * Add exist config to config list
	 *
	 * @param config config
	 */
	public void add(Config config) {
		ManagerInstance.INSTANCE.configs.add(config);
	}

	/**
	 * Get configs for parsing and config accessing.
	 *
	 * @return config
	 */
	public List<Config> getConfigs() {
		return ManagerInstance.INSTANCE.configs;
	}

	/**
	 * Get specific config type of the config
	 *
	 * @param configClazz specific type class
	 * @param <T> type
	 * @return the config or null if not found
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends Config> T getConfig(Class<T> configClazz) {
		return (T) ManagerInstance.INSTANCE.configs.stream()
				.filter(c -> c.getClass().equals(configClazz))
				.findFirst().orElse(null);
	}

	/**
	 * Delegate method to {@link Config#parse()}
	 */
	public void parse() {
		ManagerInstance.INSTANCE.configs.forEach(Config::parse);
	}

	/**
	 * Delegate method to {@link Config#getDelegateByType(Class)}
	 *
	 * @param type Class of ArgDelegate
	 * @param <T>  subclass of ArgDelegate
	 * @return delegate or null if not found
	 */
	@Nullable
	public <T extends ConfigDelegate> T getDelegateByType(@NotNull Class<T> type) {
		BelongsTo annotation = type.getAnnotation(BelongsTo.class);
		if (annotation == null) return null;
		for (Config config : getConfigs()) {
			if (config.getClass().equals(annotation.value())) {
				return config.getDelegateByType(type);
			}
		}
		return null;
	}

	/**
	 * Delegate method to {@link Config#getDelegateByString(String)}
	 *
	 * @param type <b>full name</b> type string including package
	 * @return delegate or null if not found
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public ConfigDelegate getDelegateByString(String type) {
		Class<?> c = ClassManager.createManager(ManagerInstance.INSTANCE.classLoaders).loadClass(type);
		if (c == null || ConfigDelegate.class.isAssignableFrom(c)) return null;
		return getDelegateByType((Class<? extends ConfigDelegate>) c);
	}
}
