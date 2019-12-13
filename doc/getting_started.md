This is a Flink connector for the FIWARE Orion Context Broker. It has two parts:

-   **`OrionSource`**: Source for receiving NGSI v2 events from subscriptions via HTTP.

-   **`NGSILDSource`**: Source for receiving NGSI-LD events from subscriptions via HTTP.

-   **`OrionSink`**: Sink for writing the processed data in Orion.

In order to install the connector, first you must download the JAR from the latest release.

In your project directory run:

```bash
mvn install:install-file -Dfile=$(PATH_DOWNLOAD)/orion.flink.connector-1.2.3.jar -DgroupId=org.fiware.cosmos -DartifactId=orion.flink.connector -Dversion=1.2.3 -Dpackaging=jar
```

Add it to your `pom.xml` file inside the dependencies section.

```xml
<dependency>
    <groupId>org.fiware.cosmos</groupId>
    <artifactId>orion.flink.connector</artifactId>
    <version>1.2.3</version>
</dependency>
```
