package io.tomahawkd.config.sources;

import io.tomahawkd.config.util.ClassManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Entry for Source management
 */
public class SourceManager {

	/**
	 * Get source manager with default classloaders.
	 *
	 * @return source manager
	 */
	public static SourceManager get() {
		return new SourceManager(null);
	}

	/**
	 * Get source manager with specific classloaders
	 *
	 * Be aware that if it is already initialized, the newly passed classloader list
	 * could be ignored.
	 *
	 * @param classLoaders classloader list
	 * @return source manager
	 */
	public static SourceManager get(Collection<ClassLoader> classLoaders) {
		return new SourceManager(classLoaders);
	}

	/**
	 * Source manager constructor
	 *
	 * @param classLoaders classloader list
	 */
	private SourceManager(Collection<ClassLoader> classLoaders) {
		// skip initialization if already initialized
		if (SourceManager.ManagerInstance.INSTANCE.initialized) return;

		ClassManager.createManager(classLoaders).loadClasses(Source.class, null).stream()
				// filter abstract classes
				.filter(c -> !Modifier.isAbstract(c.getModifiers()))
				.map(c -> {

					try {
						// construct source
						Constructor<? extends Source> constructor = c.getDeclaredConstructor();
						constructor.setAccessible(true);
						return constructor.newInstance();
					} catch (InstantiationException | InvocationTargetException |
							NoSuchMethodException | IllegalAccessException e) {
						throw new RuntimeException("Construct source " + c.getName() + " failed.", e);
					}
				}).forEach(ManagerInstance.INSTANCE.sources::add);

		ManagerInstance.INSTANCE.initialized = true;
	}

	/**
	 * Internal data instance (Singleton)
	 */
	private enum ManagerInstance {
		INSTANCE;

		private final List<Source> sources;
		private boolean initialized = false;

		ManagerInstance() {
			sources = new ArrayList<>();
		}
	}

	/**
	 * Get source list
	 * @return source list
	 */
	public List<Source> getSources() {
		return ManagerInstance.INSTANCE.sources;
	}

	/**
	 * Get specific source type of the config
	 *
	 * @param sourceClazz specific type class
	 * @param <T> type
	 * @return the source or null if not found
	 */
	@SuppressWarnings("unchecked")
	public <T extends Source> T getSource(Class<T> sourceClazz) throws NoSuchElementException {
		return (T) ManagerInstance.INSTANCE.sources.stream()
				.filter(s -> s.getClass().equals(sourceClazz))
				.findFirst().orElseThrow(() ->
						new NoSuchElementException(
								"Source class " + sourceClazz.getName() +
										" not found."));
	}
}
