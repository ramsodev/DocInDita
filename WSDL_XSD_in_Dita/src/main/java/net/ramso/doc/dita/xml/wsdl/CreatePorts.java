package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;
import java.util.List;

import com.predic8.wsdl.Port;

import net.ramso.doc.dita.BasicCreate;

public class CreatePorts extends BasicCreate {
	public CreatePorts(String serviceName) {
		super(serviceName+"Ports","Ports del servicio " + serviceName);
		setTemplateFile("template/ports.vm");
	}

	public String create(List<Port> ports) throws IOException {
		getContext().put("ports", ports);
		run(getContext());
		return getFile_name();
	}

}
