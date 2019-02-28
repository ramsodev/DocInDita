package net.ramso.doc.dita.xml;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.ramso.doc.Config;
import net.ramso.tools.CommandLineProcessor;

class ToolsTest {

	private CommandLineProcessor cmd;

	@BeforeEach
	void setUp() throws Exception {
		Config.start();
	}

	@Test
	void testCommandLineProcessor() {
		try {
		cmd = new CommandLineProcessor();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void testPrintHelp() {
		fail("Not yet implemented");
	}

	@Test
	void testParse() {
		fail("Not yet implemented");
	}

	@Test
	void testGetValue() {
		fail("Not yet implemented");
	}

}
