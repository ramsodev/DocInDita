package net.ramso.tools;

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

import net.ramso.docindita.db.Config;

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
			final int ini = key.indexOf("[") + 1;
			final int end = key.indexOf("]");
			final int i = Integer.parseInt(key.substring(ini, end));
			values[i] = (String) entry.getValue();
		}
		return values;
	}

	private void init() {
		options = new Options();
		names = getProperties(Constants.PREFIX_CMD_NAME);
		desc = getProperties(Constants.PREFIX_CMD_DESCRIPTION);
		reqs = toBoolean(getProperties(Constants.PREFIX_CMD_REQUIERED));
		hasArgs = toBoolean(getProperties(Constants.PREFIX_CMD_ARGUMENT));
		clas = getProperties(Constants.PREFIX_CMD_TYPE);
		for (int i = 0; i < names.length; i++) {
			final Option o = new Option(names[i], hasArgs[i], desc[i]);
			o.setRequired(reqs[i]);
			try {
				if (!clas[i].isEmpty()) {
					o.setType(Class.forName(clas[i]));
				}
			} catch (final ClassNotFoundException e) {
				LogManager.warn("No se pudo instaciar el tipo para la opcion " + names[i] + " de la linea de mandatos",
						e);
			}
			options.addOption(o);
		}
	}

	public List<String> parse(String[] args, int pMin, int pMax) throws ParseException {
		final CommandLineParser parser = new DefaultParser();
		final CommandLine cmdLine = parser.parse(options, args);
		if (cmdLine.hasOption("h")) {
			printHelp();
			return null;
		}
		try {
			for (int i = 0; i < names.length; i++) {
				if (cmdLine.hasOption(names[i])) {
					if (hasArgs[i]) {
						Config.set(names[i], cmdLine.getOptionValue(names[i]));
					} else {
						Config.set(names[i], "true");
					}
				} else {
					if (hasArgs[i]) {
						if (reqs[i])
							throw new org.apache.commons.cli.ParseException(desc[i] + " es obligatorio");
					}
					// else {
					// Config.set(names[i], "false");
					// }
				}
			}
		} catch (final ConfigurationException e) {
			LogManager.error("Parametro erroneo", e);
			throw new org.apache.commons.cli.ParseException(e.getMessage());
		}
		final List<String> ps = cmdLine.getArgList();
		if ((ps.size() < pMin) || (ps.size() > pMax))
			throw new org.apache.commons.cli.ParseException("Numero de parametros no opcionales erroneo");
		return ps;
	}

	public void printHelp() {
		final String header = "Genera la documentación de un WSDL, XSD o WADL indicado en la entrada\n\n";
		final String footer = "\n";

		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("XMLDocInDiata", header, options, footer, true);

	}

	private boolean[] toBoolean(String[] properties) {
		final boolean[] bols = new boolean[properties.length];
		for (int i = 0; i < properties.length; i++) {
			bols[i] = Boolean.parseBoolean(properties[i]);
		}
		return bols;
	}

}
