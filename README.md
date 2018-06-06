Warp Service - a webservice to schedule fleet for hyper drive 
======================================================

Building locally
----------------
mvn clean install

Running locally
---------------
java -jar ./build/dependency/warp-service-1.0-SNAPSHOT.jar -Dlogging.configurationFile=./src/main/resources/log4j2.xml

Logs Location
-------------
./logs/app.log

REST Api Documentation (Swagger Documentation)
----------------------------------------------
-	Build the project
-	Run the project
-	Acess the swagger documentation at: http://localhost:8080/swagger-ui.html
 

Code Packages
-------------
* com.anamika.app.client - Clients for consuming dependant services (e.g. Hyperdrive & FleetNet)
* com.anamika.app.component - Components like AppRunner which starts the background task which monitors Missions 
* com.anamika.app.config - All configs (e.g. Thread Pool Config, DB Config, Swagger Doc Config, Jackson Config)
* com.anamika.app.error - MessageDTO json object returned by service for errors
* com.anamika.app.model - Models for persistant storage
* com.anamika.app.request - POJO used in Request Body
* com.anamika.app.service - All services used in the Warp REST Service
* com.anamika.app.utils - Util classes & constants
* com.anamika.app.web - All Rest Controllers

Key Features
------------
* Used [Spring Boot](https://spring.io/projects/spring-boot) for the REST server.
* Used [Project Lombok](https://github.com/rzwitserloot/lombok) to generate boiler-plate code.
* Used [Swagger](https://swagger.io/) & [SpringFox](http://springfox.github.io/springfox/) for REST Documentation.
* Used [flapdoodle](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo) for embedded Mongo DB for persistent storage.
* Used [WireMock](https://github.com/tomakehurst/wiremock) to mock dependent services like Hyperdrive & FleetNet.
* Used [javax.validation](https://mvnrepository.com/artifact/javax.validation) to validate REST Requests.
* Used [CacheLoader](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/cache/CacheLoader.html) to refresh URIs periodically to simulate HA environment.
* Used [guava-retrying](https://github.com/rholder/guava-retrying) to make communication with dependent service more resilient.
* Used [Dropwizard](https://metrics.dropwizard.io/4.0.0/) for metrics.

Key Program Flows
-----------------
* WarpServiceApplication is the entry point for the application.
* The rest requests are handled by WarpController Rest Controller.
* The WarpController calls the DefaultWarpService which schedules HyperDrive for the fleet.
* The persistent data is of two types. 1) Read only Mission Event Log 2) Fleet info with current Mission Details
* Access to Mission Event Log is controlled by MissionEventLogService.
* Access to Fleet data is controlled by FleetService.
* MissionService performs tasks like Schedule New Mission, Abort Mission, Complete Mission, Get All Existing Missions etc. Internally it uses MissionEventLogService & FleetService.
* MissionMonitoringService does the work of periodically monitoring all open missions and moving them to either complete or abort state.
