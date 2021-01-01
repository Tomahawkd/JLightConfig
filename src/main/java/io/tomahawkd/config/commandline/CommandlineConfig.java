package io.tomahawkd.config.commandline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.tomahawkd.config.AbstractConfig;
import io.tomahawkd.config.annotation.HiddenField;
import io.tomahawkd.config.annotation.SourceFrom;
import io.tomahawkd.config.sources.Source;
import org.jetbrains.annotations.NotNull;

@SourceFrom(CommandlineSource.class)
public class CommandlineConfig extends AbstractConfig {

	@HiddenField
	private JCommander c;

	@Override
	public final void parse(@NotNull Source source) {
		c = JCommander.newBuilder().addObject(this).addObject(getDelegates()).build();

		try {
			c.parse((String[]) source.getData());
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			c.usage();
			throw e;
		}
	}

	public String usage() {
		StringBuilder builder = new StringBuilder();
		c.usage(builder);
		return builder.toString();
	}
}
