package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;
import java.util.List;

import org.ow2.easywsdl.schema.api.Documentation;
import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.easywsdl.wsdl.api.Endpoint;

import net.ramso.doc.dita.BasicCreate;

public class CreateEndPoints extends BasicCreate {
	public CreateEndPoints(String serviceName) {
		super("Endpoints de " + serviceName);
		setTemplateFile("template/endpoints.vm");
	}

	public String create(List<Endpoint> endpoints) throws IOException {
		getContext().put("endpoints", endpoints);
		Binding e = endpoints.get(0).getBinding();
		String n = e.getQName().getLocalPart();
		Documentation d = e.getDocumentation();
		BindingOperation o = e.getBindingOperations().get(0);
		Documentation d2 = o.getDocumentation();
		String n2 = o.getQName().getLocalPart();
		run(getContext());
		return getFile_name();
	}

}
