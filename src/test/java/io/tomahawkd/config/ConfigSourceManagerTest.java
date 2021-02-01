package io.tomahawkd.config;

import io.tomahawkd.config.commandline.CommandlineConfigSource;
import io.tomahawkd.config.sources.ConfigSource;
import io.tomahawkd.config.sources.SourceManager;
import io.tomahawkd.config.util.ClassManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

public class ConfigSourceManagerTest {

	@Test
	public void constructionTest() {
		SourceManager manager = SourceManager.get();
		Assert.assertNotNull(manager.getSource(CommandlineConfigSource.class));
		ClassManager.createManager(null)
				.loadClasses(ConfigSource.class, null)
				.containsAll(
						manager.getSources().stream().map(ConfigSource::getClass).collect(Collectors.toSet()));
	}
}
