package de.mukis.config4cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

import com.typesafe.config.Config;

public class ConfigBasicParserTest {

    private BasicParser basicParser;

    @Before
    public void setUp() throws Exception {
        basicParser = new BasicParser();
    }

    @Test
    public void testBooleanArgument() throws ParseException {
        String[] args = { "-debug" };

        Option debug = new Option("debug", "print debugging information");
        CommandLine cmd = basicParser.parse(options(debug), args);

        Config config = CommandLineConfig.fromCommandLine(cmd);
        assertTrue("Option 'debug' not parsed", config.hasPath("debug"));
        assertTrue("Option 'debug' not been correctly parsed", config.getBoolean("debug"));
    }

    @Test
    @SuppressWarnings("static-access")
    public void testSingleArgumentString() throws ParseException {
        String[] args = { "-logfile", "/absolute/path" };

        Option logfile = OptionBuilder.withArgName("file") //
                .hasArg() //
                .withDescription("use given file for log") //
                .create("logfile");
        CommandLine cmd = basicParser.parse(options(logfile), args);

        Config config = CommandLineConfig.fromCommandLine(cmd);
        assertTrue("Option 'logfile' not parsed", config.hasPath("logfile"));
        assertEquals("Option 'logfile' not been correctly parsed", "/absolute/path", config.getString("logfile"));
    }

    @Test
    @SuppressWarnings("static-access")
    public void testSingleArgumentDouble() throws ParseException {
        String[] args = { "-precision", "1.523" };

        Option precision = OptionBuilder.withArgName("double") //
                .hasArg() //
                .withDescription("use given precision for calculation") //
                .create("precision");
        CommandLine cmd = basicParser.parse(options(precision), args);

        Config config = CommandLineConfig.fromCommandLine(cmd);
        assertTrue("Option 'precision' not parsed", config.hasPath("precision"));
        assertEquals("Option 'precision' not been correctly parsed", 1.523, config.getDouble("precision"), 0.0);
    }

    @Test
    @SuppressWarnings("static-access")
    public void testPropertyArgument() throws ParseException {
        String[] args = { "-D", "property=value" };

        Option property = OptionBuilder.withArgName("property=value") //
                .hasArgs(2) //
                .withValueSeparator() // uses '=' by default
                .withDescription("use value for given property") //
                .create("D");
        CommandLine cmd = basicParser.parse(options(property), args);

        Config config = CommandLineConfig.fromCommandLine(cmd);
        assertTrue("Option 'D' not parsed", config.hasPath("D"));
        Config d = config.getConfig("D");
        assertTrue("Option 'property' not parsed", d.hasPath("property"));
        assertEquals("Option 'property' not been correctly parsed", "value", d.getString("property"));

        // Two arguments
        String[] args2 = { "-D", "file=/absolute/path", "-D", "precision=1.523" };
        cmd = basicParser.parse(options(property), args2);
        config = CommandLineConfig.fromCommandLine(cmd);
        assertTrue("Option 'D' not parsed", config.hasPath("D"));
        d = config.getConfig("D");
        assertTrue("Option 'file' not parsed", d.hasPath("file"));
        assertEquals("Option 'file' not been correctly parsed", "/absolute/path", d.getString("file"));
        assertTrue("Option 'precision' not parsed", d.hasPath("precision"));
        assertEquals("Option 'precision' not been correctly parsed", 1.523, d.getDouble("precision"), 0.0);
    }

    @Test
    @SuppressWarnings("static-access")
    public void testMultipleArguments() throws ParseException {
        String[] args = { "-position", "23.6,12.4,1452,803" };
        Option position = OptionBuilder.withArgName("longitude,latitude,altitude,speed") //
                .hasArgs(4) //
                .withValueSeparator(',') //
                .withDescription("use four arguments for something") //
                .create("position");

        CommandLine cmd = basicParser.parse(options(position), args);
        Config config = CommandLineConfig.fromCommandLine(cmd);
        assertTrue("Option 'position' not parsed", config.hasPath("position"));

        Config pos = config.getConfig("position");
        assertTrue("Option 'longitude' not parsed", pos.hasPath("longitude"));
        assertTrue("Option 'latitude' not parsed", pos.hasPath("latitude"));
        assertTrue("Option 'altitude' not parsed", pos.hasPath("altitude"));
        assertTrue("Option 'speed' not parsed", pos.hasPath("speed"));

        assertEquals("Option 'longitude' not been correctly parsed", 23.6, pos.getDouble("longitude"), 0.0);
        assertEquals("Option 'latitude' not been correctly parsed", 12.4, pos.getDouble("latitude"), 0.0);
        assertEquals("Option 'altitude' not been correctly parsed", 1452, pos.getDouble("altitude"), 0.0);
        assertEquals("Option 'speed' not been correctly parsed", 803, pos.getDouble("speed"), 0.0);
    }

    @Test(expected = ParseException.class)
    @SuppressWarnings("static-access")
    public void testMultipleArgumentsWrongArgnames() throws ParseException {
        String[] args = { "-position", "23.6,12.4,1452,803" };
        Option position = OptionBuilder
        // !!wrong separator!!
                .withArgName("longitude;latitude;altitude;speed") //
                .hasArgs(4) //
                .withValueSeparator(',') //
                .withDescription("use four arguments for something") //
                .create("position");

        CommandLine cmd = basicParser.parse(options(position), args);

        // Throws exception because argName is not correct
        CommandLineConfig.fromCommandLine(cmd);
    }

    private Options options(Option... options) {
        Options returns = new Options();
        for (Option option : options) {
            returns.addOption(option);
        }
        return returns;
    }

}
