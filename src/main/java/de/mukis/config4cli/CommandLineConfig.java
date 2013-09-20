package de.mukis.config4cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Parses a {@linkplain CommandLine} into a {@linkplain Config} object.
 * 
 * @author muki
 * 
 */
public class CommandLineConfig {

    private CommandLineConfig() {
    }

    /**
     * <p>
     * Parsing rules
     * </p>
     * 
     * <p>
     * <h3>non-arg options</h3>
     * 
     * <pre>
     * option-name = true
     * </pre>
     * 
     * Example call: {@code -debug}
     * 
     * <h3>single-arg options</h3>
     * 
     * <pre>
     * option-name = argument
     * </pre>
     * 
     * Example call: {@code -file /absolute/path}
     * 
     * <h3>property-arg options</h3>
     * 
     * <pre>
     * option-name {
     *    prop1 = val1
     *    prop2 = val2   
     * }
     * </pre>
     * 
     * Example call: {@code -D prop1=val1 -D prop2=val2}
     * 
     * 
     * <h3>multi-arg options</h3>
     * 
     * <pre>
     * option-name {
     *    argName1 = val1
     *    argName2 = val2   
     * }
     * </pre>
     * 
     * <p>
     * Example call: {@code -position 23.6,12.4,1452,803}
     * </p>
     * 
     * Options object
     * 
     * <pre>
     * Option position = OptionBuilder.withArgName(&quot;longitude,latitude,altitude,speed&quot;) //
     *         .hasArgs(4) //
     *         .withValueSeparator(',') //
     *         .withDescription(&quot;use four arguments for something&quot;) //
     *         .create(&quot;position&quot;);
     * </pre>
     * 
     * Will result in:
     * 
     * <pre>
     * position {
     *    longitude = 23.6
     *    latitude = 12.4   
     *    altitude = 1452
     *    speed = 803
     * }
     * </pre>
     * 
     * </p>
     * 
     * 
     * @param cmd
     * @return
     * @throws ParseException
     */
    public static Config fromCommandLine(CommandLine cmd) throws ParseException {
        Map<String, Object> config = new HashMap<>();
        Option[] options = cmd.getOptions();
        for (Option opt : options) {
            switch (opt.getArgs()) {
            case -1:
                config.put(opt.getOpt(), Boolean.TRUE.toString());
                break;
            case 1:
                config.put(opt.getOpt(), opt.getValue());
                break;
            case 2:
                String path = opt.getOpt() + "." + opt.getValue(0);
                config.put(path, opt.getValue(1));
                break;
            default:
                String root = opt.getOpt();
                String sep = Character.valueOf(opt.getValueSeparator()).toString();
                String[] argNames = opt.getArgName().split(sep);

                if (argNames == null || argNames.length != opt.getArgs()) {
                    String msg = String.format(
                            "Argname must contain the same structure as arguments. However was %n argNames: %s %n values: %s",
                            Arrays.toString(argNames), Arrays.toString(opt.getValues()));
                    throw new ParseException(msg);
                }

                for (int i = 0; i < opt.getArgs(); i++) {
                    config.put(root + "." + argNames[i], opt.getValue(i));
                }
                break;
            }
        }
        return ConfigFactory.parseMap(config, "commandline");
    }
}
