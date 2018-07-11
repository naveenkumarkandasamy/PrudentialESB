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

public class PaymentRouter extends RouteBuilder {
	static final Logger logger = Logger.getLogger(PASToPastelRouter.class);
	static final String PAYMENT_INBOUND = "http4://10.4.15.7:9060/payment/inbound";
	static final String PAYMENT_OUTBOUND = "http4://10.4.15.7:9060/payment/outbound";
	static final String PAYMENT_OUTPUT_FOLDER = "file://data/PaymentOutput";
	static final String BANK_OUTPUT_FOLDER = "file://data/BankOutput";
	static final String EASY_PAY_FTP_ENDPOINT = "sftp://easypay@41.60.129.96:1122?password=345yp@yZ4mb!@";
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY_MM_dd_HH_mm_ss");
	private String dateAsString = simpleDateFormat.format(new Date());

	@Override
	public void configure() throws Exception {

		// Route 1

		from("file://data/PaymentInput").log("Recieved file From IL").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				// logger.debug("File Recieved from IL");
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

		}).log("Sending file to Payment Integator").setHeader(Exchange.HTTP_METHOD, constant("POST"))
				.toD(PAYMENT_OUTBOUND).to(PAYMENT_OUTPUT_FOLDER + "?fileName=response_" + dateAsString + ".txt")
				.log("Received file From Payment Integator. To be sent to Bank.").end();

		from("file://data/BankInput").log("Recieved file From Bank").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				// logger.debug("File Recieved from IL");
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

		}).log("Sending file to Payment Integator").setHeader(Exchange.HTTP_METHOD, constant("POST"))
				.toD(PAYMENT_INBOUND).to(BANK_OUTPUT_FOLDER + "?fileName=response_" + dateAsString + ".xls").log("Recieved file from Payment Integator. To be sent to IL.").end();

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
