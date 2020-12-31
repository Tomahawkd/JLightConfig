package io.tomahawkd.config.commandline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.tomahawkd.config.AbstractConfig;
import io.tomahawkd.config.annotation.HiddenField;
import io.tomahawkd.config.annotation.SourceFrom;

@SourceFrom(CommandlineSource.class)
public class CommandlineConfig extends AbstractConfig {

	@HiddenField
	private JCommander c;

	@Override
	public final void parse() {
		c = JCommander.newBuilder().addObject(this).addObject(getDelegates()).build();

		try {
			c.parse(args);
			postParsing();
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			c.usage();
			throw e;
		}
	}
}
