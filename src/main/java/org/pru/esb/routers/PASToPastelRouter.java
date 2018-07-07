package org.pru.esb.routers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.springframework.stereotype.Component;

@Component
public class PASToPastelRouter extends RouteBuilder {
	@Override
	public void configure() throws Exception {

		from("file://data/PASInput").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {

				File filetoUpload = exchange.getIn().getBody(File.class);

				/*
				 * MultipartEntityBuilder entity = MultipartEntityBuilder.create();
				 * entity.addBinaryBody("file", filetoUpload); entity.addTextBody("name",
				 * "DDACC2.xlsx");
				 * 
				 * HttpEntity resultEntity = entity.build();
				 * exchange.getOut().setHeader(Exchange.CONTENT_TYPE,
				 * resultEntity.getContentType().getValue());
				 * exchange.getOut().setHeader(Exchange.HTTP_METHOD, constant("POST"));
				 * ByteArrayOutputStream out = new ByteArrayOutputStream();
				 * entity.build().writeTo(out); InputStream inputStream = new
				 * ByteArrayInputStream(out.toByteArray());
				 * exchange.getOut().setBody(inputStream);
				 */

				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
				multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				String filename = (String) exchange.getIn().getHeader(Exchange.FILE_NAME);
				File file = exchange.getIn().getBody(File.class);
				multipartEntityBuilder.addPart("file", new FileBody(file, ContentType.MULTIPART_FORM_DATA, filename));
/*				ByteArrayOutputStream out = new ByteArrayOutputStream();
				multipartEntityBuilder.build().writeTo(out);
				InputStream inputStream = new ByteArrayInputStream(out.toByteArray());*/
				exchange.getOut().setBody(multipartEntityBuilder.build());
			}
		}).setHeader(Exchange.HTTP_METHOD, constant("POST"))
				.toD("http://localhost:8080/Pastel/api/convertor/pas/pastel")
				.log(LoggingLevel.ERROR, "RESPONSE BODY ${body}").end();

		// Use the below for testing. The below one uses file available in the Pastel Integerator itself. Its converted and forwarded to easy pay.
		
		/*
		 * from("file://data/PASInput").setHeader(Exchange.HTTP_METHOD, constant("GET"))
		 * .toD("http://localhost:8080/Pastel/api/convertor/pas/pastel")
		 * .to("file://data/PASOutput?fileName=response.csv");
		 * from("file://data/PASOutput").to(
		 * "sftp://easypay@41.60.129.96:1122?password=345yp@yZ4mb!@");
		 */
	}

}
