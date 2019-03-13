package net.ramso.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineProcessor {

	private Options options;
	private String[] names;
	private String[] desc;
	private boolean[] reqs;
	private boolean[] hasArgs;
	private String[] clas;

	public CommandLineProcessor() {
		super();
		init();
	}

	private String[] getProperties(String prefix) {
		final Properties p = ConfigurationManager.getProperties(prefix);
		final String[] values = new String[p.size()];
		for (final Entry<Object, Object> entry : p.entrySet()) {
			final String key = (String) entry.getKey();
			final int ini = key.indexOf('[') + 1;
			final int end = key.indexOf(']');
			final int i = Integer.parseInt(key.substring(ini, end));
			values[i] = (String) entry.getValue();
		}
		return values;
	}

	private void init() {
		this.options = new Options();
		this.names = getProperties(Constants.PREFIX_CMD_NAME);
		this.desc = getProperties(Constants.PREFIX_CMD_DESCRIPTION);
		this.reqs = toBoolean(getProperties(Constants.PREFIX_CMD_REQUIERED));
		this.hasArgs = toBoolean(getProperties(Constants.PREFIX_CMD_ARGUMENT));
		this.clas = getProperties(Constants.PREFIX_CMD_TYPE);
		for (int i = 0; i < this.names.length; i++) {
			final Option o = new Option(this.names[i], this.hasArgs[i], this.desc[i]);
			o.setRequired(this.reqs[i]);
			try {
				if (!this.clas[i].isEmpty()) {
					o.setType(Class.forName(this.clas[i]));
				}
			} catch (final ClassNotFoundException e) {
				LogManager.warn(BundleManager.getString("commons.CommandLineProcessor.cmd_instance_fail", this.names[i]), e); //$NON-NLS-1$ //$NON-NLS-2$
			}
			this.options.addOption(o);
		}
	}

	public List<String> parse(String[] args, int pMin, int pMax) throws ParseException {
		final CommandLineParser parser = new DefaultParser();
		final CommandLine cmdLine = parser.parse(this.options, args);
		if (cmdLine.hasOption("h")) { //$NON-NLS-1$
			printHelp();
			return new ArrayList<>();
		}
		for (int i = 0; i < this.names.length; i++) {
			if (cmdLine.hasOption(this.names[i])) {
				if (this.hasArgs[i]) {
					if (!this.clas[i].isEmpty()) {
						LogManager.debug(BundleManager.getString("commons.CommandLineProcessor.type_get", this.clas[i])); //$NON-NLS-1$
					}
					ConfigurationManager.set(this.names[i], cmdLine.getOptionValue(this.names[i]));
				} else {
					ConfigurationManager.set(this.names[i], "true"); //$NON-NLS-1$
				}
			} else if (this.hasArgs[i] && this.reqs[i]) {
				throw new org.apache.commons.cli.ParseException(
						  BundleManager.getString("commons.CommandLineProcessor.type_requiered",this.desc[i])); //$NON-NLS-1$
			}
		}
		final List<String> ps = cmdLine.getArgList();
		if ((ps.size() < pMin) || (ps.size() > pMax)) {
			throw new org.apache.commons.cli.ParseException(
					BundleManager.getString("commons.CommandLineProcessor.parameter_numer_error")); //$NON-NLS-1$
		}
		return ps;
	}

	public void printHelp() {
		final String header = BundleManager.getString("commons.CommandLineProcessor.help_header"); //$NON-NLS-1$
		final String footer = "\n"; //$NON-NLS-1$

		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(LogManager.getLogger().getName(), header, this.options, footer, true); //$NON-NLS-1$

	}

	private boolean[] toBoolean(String[] properties) {
		final boolean[] bols = new boolean[properties.length];
		for (int i = 0; i < properties.length; i++) {
			bols[i] = Boolean.parseBoolean(properties[i]);
		}
		return bols;
	}

}
