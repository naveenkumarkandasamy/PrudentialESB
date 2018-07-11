package org.pru.esb.routers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PASToPastelRouter extends RouteBuilder {
	static final Logger logger = Logger.getLogger(PASToPastelRouter.class);
	static final String PASTEL_TRANSLATOR_WEBSERVICE_ENDPOINT = "http4://localhost:8080/Pastel/api/convertor/pas/pastel";
	static final String FOLDER_WITH_PASTEL_FORMAT = "file://data/PASOutput";
	static final String EASY_PAY_FTP_ENDPOINT = "sftp://easypay@41.60.129.96:1122?password=345yp@yZ4mb!@";
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY_MM_dd_HH_mm_ss");
	private String dateAsString = simpleDateFormat.format(new Date());

	@Override
	public void configure() throws Exception {

		// Route 1

		from("file://data/PASInput").log("Recieved File From PAS").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				logger.debug("File Recieved from PAS");
				File filetoUpload = exchange.getIn().getBody(File.class);
				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
				multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				String filename = (String) exchange.getIn().getHeader(Exchange.FILE_NAME);
				File file = exchange.getIn().getBody(File.class);
				multipartEntityBuilder.addPart("file", new FileBody(file, ContentType.MULTIPART_FORM_DATA, filename));
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				multipartEntityBuilder.build().writeTo(out);
				InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
				exchange.getOut().setBody(multipartEntityBuilder.build());

			}

		}).log("Sending File to Pastel Integator").setHeader(Exchange.HTTP_METHOD, constant("POST"))
				.toD(PASTEL_TRANSLATOR_WEBSERVICE_ENDPOINT).to(FOLDER_WITH_PASTEL_FORMAT + "?fileName=response_" + dateAsString + ".csv")
				.log("Recieved File from Pastel Integator"+dateAsString).end();

		// Route 2

		from(FOLDER_WITH_PASTEL_FORMAT).to(EASY_PAY_FTP_ENDPOINT).log("Sent to EasyPayIntegator").end();

		// Use the below for testing. The below one uses file available in the Pastel
		// Integerator itself. Its converted and forwarded to easy pay.

		/*
		 * from("file://data/PASInput").setHeader(Exchange.HTTP_METHOD, constant("GET"))
		 * .toD("http://localhost:8080/Pastel/api/convertor/pas/pastel")
		 * .to("file://data/PASOutput?fileName=response.csv");
		 * from("file://data/PASOutput").to(
		 * "sftp://easypay@41.60.129.96:1122?password=345yp@yZ4mb!@");
		 */
	}

}
