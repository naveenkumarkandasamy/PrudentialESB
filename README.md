# PrudentialESB

Below steps has 4 main sections. Pastel Integerator setup, ESB setup, Workflow details and Next steps. 

Pastel Integrator:

Tools required:
Eclipse, Tomcat, Java8, Maven.

Stack:
Java 8, Spring-mvc, Maven, Apache POI, Jackson CSV mapper.

Git: https://github.com/naveenkumarkandasamy/Pastel-Integerator.git

Setup:
Add project to eclipse. Update Maven Do maven install. Copy the war file to tomcat and start the tomcat server. 

APIs:
Get - http://localhost:8080/Pastel/api/convertor/pas/pastel
For testing conversion. PAS input file - DDAC.xlsx from resources folder is taken, converted to Pastel format and responded. 
Post - http://localhost:8080/Pastel/api/convertor/pas/pastel
Actual webservice exposed. PAS input file from resources folder can be added as form multipart and requested through postman. Its converted to Pastel format and responded.

------------------------------------------------------------------------

Fuse - Apache Camel ESB:

Git: https://github.com/naveenkumarkandasamy/PrudentialESB.git

Tools Required:
Red Hat JBoss Developer Studio. 

Setup and Running:
Add project to JBDS. Update Maven. Run as Local Camel Context. Libraries installed through Maven. It may take 20-30 mins. 

Application Stack:
Fuse, Apache Camel, Spring Boot, Maven, MySql(to be added). Everything configured in maven. This stack gives us an advantage in running the ESB standalone. Easy to setup as we need only IDE. So picked this. Apache-Karaf requires complex steps for even running hello world. 

Router Configuration and Flow:
PASToPastelRouter.java has configurations for accepting PAS file in ESB. Send it to Pastel Integrator and consume the response from integrator. Save it in ESB. FTP to Easy Pay ftp server.

------------------------------
Detailed workflow:
1. DDACC.xlsx from pastel-intgerator resources folder can be placed in folder fuse\data\PASInput of ESB work space.
2. File gone through processor. Attached to http entity as MultiPart file. 
3. Sent to Pastel Integrator through POST. 
4. Integrator consumes file, converts it to CSV.
5. CSV file responded back.
6. ESB accepts the file and saved in 
7. File picked up from that folder and ftpéd to Easy Pay Integrator. fuse\data\PASOutput of ESB work space.

----------------------------------
Next Steps:

1. Posting file as multi-part to Integrator from ESB is not working. PASToPastelRouter.java has the code. The same file has commented lines, which has code to simulate the entire workflow with get request. It works fine till sending the file to Easy Pay. 
2. Integrator code cleanup. Using modelmapper or something similar to map the PAS model to Pastel model - to have it configuration based. 
3. File name should not be static in this entire process at ESB & Integrator. Should change it to date + incremental naming. 
4. Adding DB at ESB to track the status of each file received till its sent to easy pay. 
5. Moving IPs and Passwords to properties or DB. 
6. Actual models of PAS and Pastel need to be changed. Current translation is based on our assumption. We still don't have details of both file structures and mapping information.
