# fiware-cosmos-orion-flink-connector

This is a Flink connector for the Fiware Orion Context Broker.
It provides a source for receiving NGSIv2 events in the shape of HTTP messages from subscriptions.
It also provides a sink for writing back to the Context Broker.

## Installation

Download the JAR from the latest release.
In your project directory run:
```
mvn install:install-file -Dfile=$(PATH_DOWNLOAD)/orion.flink.connector-1.0.jar -DgroupId=org.fiware.cosmos -DartifactId=orion.flink.connector -Dversion=1.0 -Dpackaging=jar
```

Add it to your `pom.xml` file inside the dependencies section
```
<dependency>
    <groupId>org.fiware.cosmos</groupId>
    <artifactId>orion.flink.connector</artifactId>
    <version>1.0</version>
</dependency>
```

## Usage
### OrionSource

* Import dependency
    ```
    import org.fiware.cosmos.orion.flink.connector.{OrionSource}
    ```
* Add source to Flink Environment
    ```
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val eventStream = env.addSource(new OrionSource(9001)
    ```
* Parse the received data

### OrionSink
* Import dependency
    ```
    import org.fiware.cosmos.orion.flink.connector.{OrionSink,OrionSinkObject,ContentType,HTTPMethod}
    ```
* Add sink to source
    ```
    val processedDataStream = eventStream
    OrionSink.addSink( processedDataStream )
    ```

