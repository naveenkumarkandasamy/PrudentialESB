package org.pru.esb.routers;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.pru.esb.beans.Policy;
import org.springframework.stereotype.Component;

@Component
public class EPOSRouter extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		restConfiguration().component("restlet").host("localhost").port("8080").bindingMode(RestBindingMode.auto);

		rest("/api").post("/application").consumes("application/json").type(Policy.class).to("direct:InsertPolicy");

		from("direct:InsertPolicy").log("Inserted new Policy").bean("PolicyMapper", "getPolicy")
				.to("sqlComponent:{{sql.insertPolicy}}");
	}
}