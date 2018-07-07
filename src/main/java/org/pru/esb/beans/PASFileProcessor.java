package org.pru.esb.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class PASFileProcessor implements Processor {
	public void process(Exchange exchange) throws Exception {
		String originalFileContent = (String) exchange.getIn().getBody(String.class);
		String upperCaseFileContent = originalFileContent.toUpperCase();
		exchange.getIn().setBody(upperCaseFileContent);
	}
}
