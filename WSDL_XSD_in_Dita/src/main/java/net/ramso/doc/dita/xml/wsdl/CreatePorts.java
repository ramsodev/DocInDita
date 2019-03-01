package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;
import java.util.List;

import com.predic8.wsdl.BindingOperation;
import com.predic8.wsdl.Port;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.tools.Tools;

public class CreatePorts extends BasicCreate {
	public CreatePorts(String serviceName) {
		super(serviceName+Constants.SUFFIX_PORT,"Ports del servicio " + serviceName);
		setTemplateFile("template/ports.vm");
	}

	public String create(List<Port> ports) throws IOException {
		getContext().put("ports", ports);
		getContext().put("tools", Tools.class);
		run(getContext());
		return getFile_name();
	}

}
