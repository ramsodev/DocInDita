package net.ramso.docindita.xml.wsdl;

import java.io.IOException;
import java.util.List;

import com.predic8.wsdl.Port;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;

public class CreatePorts extends BasicCreate {
	public CreatePorts(String serviceName) {
		super(serviceName + DitaConstants.SUFFIX_PORT, "Ports del servicio " + serviceName);
		setTemplateFile("template/ports.vm");
	}

	public String create(List<Port> ports) throws IOException {
		getContext().put("ports", ports);
		getContext().put("tools", DitaTools.class);
		run(getContext());
		return getFileName();
	}

}
