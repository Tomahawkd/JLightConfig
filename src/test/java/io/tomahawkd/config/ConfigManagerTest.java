package io.tomahawkd.config;

import io.tomahawkd.config.annotation.BelongsTo;
import io.tomahawkd.config.commandline.CommandlineConfig;
import io.tomahawkd.config.commandline.CommandlineSource;
import io.tomahawkd.config.delegate.TestDelegate;
import io.tomahawkd.config.sources.SourceManager;
import io.tomahawkd.config.util.ClassManager;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigManagerTest {

	@Test
	public void constructionTest() {
		ConfigManager manager = ConfigManager.get();
		List<Config> configs = manager.getConfigs();
		ClassManager.createManager(null)
				.loadClasses(Config.class, null)
				.containsAll(
						configs.stream().map(Config::getClass).collect(Collectors.toSet()));

		ClassManager.createManager(null)
				.loadClasses(ConfigDelegate.class, null)
				.stream().filter(d -> d.getAnnotation(BelongsTo.class) != null)
				.forEach(d ->
						Assert.assertNotNull(
								configs.stream()
										.filter(c -> !Modifier.isAbstract(c.getClass().getModifiers()))
										.filter(c -> c.getClass()
												.equals(d.getAnnotation(BelongsTo.class).value()))
										.findFirst()
										.orElseThrow(() -> new RuntimeException("Class not found."))
										.getDelegateByType(d)));
	}

	@Test
	public void addConfigTest() {
		CommandlineConfig config = new CommandlineConfig();
		ConfigManager manager = ConfigManager.get();
		manager.add(config);
		Assert.assertTrue(manager.getConfigs().contains(config));
	}

	@Test
	public void getDelegateTest() {
		ConfigManager manager = ConfigManager.get();
		Assert.assertNotNull(manager.getConfig(CommandlineConfig.class));
		Assert.assertNotNull(
				manager.getConfig(CommandlineConfig.class).getDelegateByType(TestDelegate.class)
		);

		Assert.assertNotNull(
				manager.getConfig(CommandlineConfig.class).getDelegateByString(TestDelegate.class.getName())
		);
	}

	@Test
	public void parseTest() {
		String[] arg = {"--help"};
		SourceManager sourceManager = SourceManager.get();
		ConfigManager configManager = ConfigManager.get();

		sourceManager.getSource(CommandlineSource.class).setData(arg);
		configManager.parse();
		Assert.assertNotNull(configManager.getConfig(CommandlineConfig.class));
		Assert.assertNotNull(
				configManager.getConfig(CommandlineConfig.class).getDelegateByType(TestDelegate.class)
		);
		Assert.assertTrue(
				configManager.getConfig(CommandlineConfig.class).getDelegateByType(TestDelegate.class).isHelp()
		);
		Assert.assertTrue(
				configManager.getConfig(CommandlineConfig.class).getDelegateByType(TestDelegate.class)
				.getField("help", boolean.class)
		);
	}
}
