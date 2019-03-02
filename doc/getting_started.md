This is a Flink connector for the Fiware Orion Context Broker. It has two parts:

-   **`OrionSource`**: Source for receiving NGSIv2 events in the shape of HTTP messages from subscriptions.

-   **`OrionSink`**: Sink for writing back to the Context Broker.

In order to install the connector, first you must download the JAR from the latest release.

In your project directory run:

```bash
mvn install:install-file -Dfile=$(PATH_DOWNLOAD)/orion.flink.connector-1.0.jar -DgroupId=org.fiware.cosmos -DartifactId=orion.flink.connector -Dversion=1.0 -Dpackaging=jar
```

Add it to your `pom.xml` file inside the dependencies section.

```xml
<dependency>
    <groupId>org.fiware.cosmos</groupId>
    <artifactId>orion.flink.connector</artifactId>
    <version>1.0</version>
</dependency>
```
