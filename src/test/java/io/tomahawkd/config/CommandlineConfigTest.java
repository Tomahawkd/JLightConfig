package io.tomahawkd.config;

import io.tomahawkd.config.commandline.CommandlineConfig;
import io.tomahawkd.config.commandline.CommandlineConfigSource;
import io.tomahawkd.config.delegate.TestDelegate;
import org.junit.Assert;
import org.junit.Test;

public class CommandlineConfigTest {

	@Test
	public void parseTest() {
		CommandlineConfig config = new CommandlineConfig();
		CommandlineConfigSource source = new CommandlineConfigSource();
		source.setData(new String[]{"--help"});
		config.addDelegate(new TestDelegate());
		
		config.parse(source);
		Assert.assertTrue(config.getDelegateByType(TestDelegate.class).isHelp());
	}
}
