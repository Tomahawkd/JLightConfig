package io.tomahawkd.config.commandline;

import com.beust.jcommander.JCommander;
import io.tomahawkd.config.AbstractConfig;
import io.tomahawkd.config.annotation.HiddenField;
import io.tomahawkd.config.annotation.SourceFrom;
import io.tomahawkd.config.sources.ConfigSource;
import org.jetbrains.annotations.NotNull;

@SourceFrom(CommandlineConfigSource.class)
public class CommandlineConfig extends AbstractConfig {

	@HiddenField
	private JCommander c;

	@Override
	public final void parse(@NotNull ConfigSource source) {
		c = JCommander.newBuilder().addObject(this).addObject(getDelegates()).build();
		c.parse((String[]) source.getData());
	}

	public String usage() {
		StringBuilder builder = new StringBuilder();
		c.usage(builder);
		return builder.toString();
	}
}
