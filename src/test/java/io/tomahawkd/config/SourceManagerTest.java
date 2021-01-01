package io.tomahawkd.config;

import io.tomahawkd.config.commandline.CommandlineSource;
import io.tomahawkd.config.sources.Source;
import io.tomahawkd.config.sources.SourceManager;
import io.tomahawkd.config.util.ClassManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

public class SourceManagerTest {

	@Test
	public void constructionTest() {
		SourceManager manager = SourceManager.get();
		Assert.assertNotNull(manager.getSource(CommandlineSource.class));
		ClassManager.createManager(null)
				.loadClasses(Source.class, null)
				.containsAll(
						manager.getSources().stream().map(Source::getClass).collect(Collectors.toSet()));
	}
}
