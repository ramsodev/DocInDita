package net.ramso.docindita.xml;

import java.io.File;

public abstract class BaseTest {
	protected void clean() {
		clean(Config.getOutputDir());
	}

	protected void clean(String path) {
		final File dir = new File(path);
		if (dir.exists()) {
			for (final File file : dir.listFiles()) {
				if (file.isDirectory()) {
					clean(file.getAbsolutePath());
				}
				file.delete();
			}
		}
	}
}
