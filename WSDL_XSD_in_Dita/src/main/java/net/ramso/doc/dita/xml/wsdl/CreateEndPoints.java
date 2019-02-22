package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;
import java.util.List;

import org.ow2.easywsdl.wsdl.api.Endpoint;

public class CreateEndPoints extends BasicCreate {
	public CreateEndPoints(String serviceName) {
		super("Endpoints de " + serviceName);
		setTemplateFile("template/endpoints.vm");
	}

	public String create(List<Endpoint> endpoints) throws IOException {
		getContext().put("endpoints", endpoints);
		run(getContext());
		return getFile_name() + "#" + getId();
	}

}
