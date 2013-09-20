## Config4CLI

This little project provides a smaller converter from [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/) commandline parser to the [Typesafe Config](https://github.com/typesafehub/config).

## Usage

First you generate your cli options as usual with commons-cli. For example

```java
Options options = new Options();

Option debug = new Option("debug", "print debugging information");
Option help = new Option( "help", "print this message" );

options.addOption(debug);
options.addOption(help);
```

For more information look at the [usage page](http://commons.apache.org/proper/commons-cli/usage.html).

Your main method could now look like this

```java
public static void main(String[] args) {
    CommandLineParser basicParser = new BasicParser();
    CommandLine cmd = basicParser.parse(options, args);

    Config config = CommandLineConfig.fromCommandLine(cmd);
}
```

## Conversion options

Currently theres only one conversion strategy.

### non-arg options
 
`option-name = true`
 
Example call: `-debug`


### single-arg options
 
`option-name = argument`
 
Example call: `-file /absolute/path`

### property-arg options

```json
option-name {
    prop1 = val1
    prop2 = val2
}
```
Example call: -D prop1=val1 -D prop2=val2

### multi-arg options

```json
option-name {
    argName1 = val1
    argName2 = val2
}
```

Example call: `-position 23.6,12.4,1452,803`

The options object is defined as follows. Note that the `withArgName` parameter must
have the same structure as the actual commandline input. This includes separator and 
argument count.

```java
Option position = OptionBuilder.withArgName("longitude,latitude,altitude,speed") //
         .hasArgs(4) //
         .withValueSeparator(',') //
         .withDescription("use four arguments for something") //
         .create("position");
```

Conversion will result in 

```json
position {
    longitude = 23.6
    latitude = 12.4
    altitude = 1452
    speed = 803
}
```json

## License

The license is Apache 2.0, see LICENSE.txt.

## Links

* [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)
* [Typesafe Config](https://github.com/typesafehub/config)
