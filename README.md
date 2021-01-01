# JLightConfig

[![Build Status](https://travis-ci.org/Tomahawkd/JLightConfig.svg?branch=master)](https://travis-ci.org/Tomahawkd/JLightConfig.svg)
![Java CI with Maven](https://github.com/Tomahawkd/JLightConfig/workflows/Java%20CI%20with%20Maven/badge.svg)
[![CodeFactor](https://www.codefactor.io/repository/github/tomahawkd/jlightconfig/badge)](https://www.codefactor.io/repository/github/tomahawkd/jlightconfig)
![CodeQL](https://github.com/Tomahawkd/JLightConfig/workflows/CodeQL/badge.svg)
![Maven Package](https://github.com/Tomahawkd/JLightConfig/workflows/Maven%20Package/badge.svg)

## Introduction

A Java lightweight configuration framework for all kinds 
of configurations (commandline, files, etc.)

### Feature

- Zero configuration: No file configurations, all in codes
- Extensive: Just extends the abstract class or interface
- Lightweight: A few dependencies, make up a world

## Installation

### Maven
```xml
<dependency>
  <groupId>io.tomahawkd</groupId>
  <artifactId>jlightconfig</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Usage

Note: The example below is already integrated in this repo

### Step 1: Build up your config

- Extends abstract class `AbstactConfig` and define your parse method

Example (with dependency `com.beust.jcommander:1.69`):

```java
@SourceFrom(CommandlineSource.class)
public class CommandlineConfig extends AbstractConfig {

  @HiddenField
  private JCommander c;

  @Override
  public final void parse(@NotNull Source source) {
    c = JCommander.newBuilder().addObject(this)
             .addObject(getDelegates()).build();

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
```

### Step 2: Add delegates to your config (optional)

- Create delegates to parse and receive config data

- Use annotation `BelongsTo` to your Delegate bind your config and delegate

Note: You may use `AbstractConfigDelegate.getField(String key, Class<T> type)`
to get data from your configs or delegates

Example:

```java
@BelongsTo(CommandlineConfig.class)
public class TestDelegate extends AbstractConfigDelegate {

  @Parameter(names = {"-h", "--help"}, help = true,
             description = "Prints usage for all the existing commands.")
  private boolean help;

  public boolean isHelp() {
    return help;
  }
}
```

### Step 3: Declare the source of your config

- Implements interface `Source` and implements how data could be 
  received.
  
- Use annotation `SourceFrom` to your Config to bind source and config

Example:

```java
public class CommandlineSource implements Source {

  private String[] data;

  public void setData(String[] data) {
    this.data = data;
  }

  @Override
  public String[] getData() {
    return data;
  }
}
```

### Step 4: Bring up your system

- Use `SourceManager` and `ConfigManager` to run your project.

Example:

```java
class Test {
  public static void main(String[] args) {
    SourceManager sourceManager = SourceManager.get();
    ConfigManager configManager = ConfigManager.get();

    sourceManager.getSource(CommandlineSource.class).setData(args);
    configManager.parse();
    configManager.getConfig(CommandlineConfig.class)
        .getDelegateByType(TestDelegate.class).isHelp()
  }
}
```

## License

This repo is under Apache v2.0 license