package io.tomahawkd.config.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class ClassManager {

	private final Set<ClassLoader> classLoadersList;

	public static ClassManager getManager(@Nullable Collection<ClassLoader> classLoaders) {
		if (classLoaders == null) return new ClassManager();
		else return new ClassManager(classLoaders);
	}

	private ClassManager() {
		classLoadersList = new HashSet<>();
		classLoadersList.add(ClasspathHelper.staticClassLoader());
		classLoadersList.add(ClasspathHelper.contextClassLoader());
	}

	private ClassManager(Collection<ClassLoader> classLoaders) {
		this();
		classLoadersList.addAll(classLoaders);
	}

	public <T> Set<Class<? extends T>> loadClasses(@NotNull Class<T> superClass, @Nullable String packageName) {
		Package p;

		Objects.requireNonNull(superClass, "Super class type can't be null.");
		if (packageName == null || (p = Package.getPackage(packageName)) == null)
			return loadClasses(superClass, (Package) null);
		else return loadClasses(superClass, p);
	}

	private <T> Set<Class<? extends T>> loadClasses(@NotNull Class<T> superClass, @Nullable Package packageName) {
		ConfigurationBuilder config = new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(true))
				.setUrls(ClasspathHelper
						.forClassLoader(classLoadersList.toArray(new ClassLoader[0])));

		if (packageName != null) {
			config.filterInputsBy(
					new FilterBuilder().include(FilterBuilder.prefix(packageName.getName())));
		}

		config.addClassLoaders(classLoadersList);
		return new Reflections(config).getSubTypesOf(superClass);
	}

	public <T> Set<Class<? extends T>> loadClassesWithAnnotation(
			@NotNull Class<T> superClass,
			@Nullable String packageName,
			@NotNull Class<? extends Annotation> annotation) {
		return loadClasses(superClass, packageName)
				.stream().filter(c -> c.getAnnotation(annotation) != null).collect(Collectors.toSet());
	}

	@Nullable
	public Class<?> loadClass(String clazz) {
		Class<?> c = null;
		for (ClassLoader classLoader : classLoadersList) {
			try {
				c = classLoader.loadClass(clazz);
				break;
			} catch (ClassNotFoundException ignore) {
			}
		}

		return c;
	}
}
