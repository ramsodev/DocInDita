package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.api.Documentation;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.wsdl.api.Fault;
import org.ow2.easywsdl.wsdl.api.Input;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.Output;
import org.ow2.easywsdl.wsdl.api.Part;
import org.ow2.easywsdl.wsdl.impl.wsdl11.PartImpl;

import net.ramso.doc.dita.BasicCreate;

public class CreateOperations extends BasicCreate {
	public CreateOperations(String serviceName) {
		super("Operaciones de " + serviceName);
		setTemplateFile("template/operations.vm");
	}

	public String create(List<Operation> operations) throws IOException {
		getContext().put("operations", operations);		
		run(getContext());
		return getFile_name();
	}

}
