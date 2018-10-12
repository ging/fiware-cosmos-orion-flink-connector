# fiware-cosmos-orion-flink-connector

[![](https://img.shields.io/badge/FIWARE-Processing\Analysis-88a1ce.svg?label=FIWARE&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABsAAAAVCAYAAAC33pUlAAAABHNCSVQICAgIfAhkiAAAA8NJREFUSEuVlUtIFlEUx+eO+j3Uz8wSLLJ3pBiBUljRu1WLCAKXbXpQEUFERSQF0aKVFAUVrSJalNXGgmphFEhQiZEIPQwKLbEUK7VvZrRvbr8zzjfNl4/swplz7rn/8z/33HtmRhn/MWzbXmloHVeG0a+VSmAXorXS+oehVD9+0zDN9mgk8n0sWtYnHo5tT9daH4BsM+THQC8naK02jCZ83/HlKaVSzBey1sm8BP9nnUpdjOfl/Qyzj5ust6cnO5FItJLoJqB6yJ4QuNcjVOohegpihshS4F6S7DTVVlNtFFxzNBa7kcaEwUGcbVnH8xOJD67WG9n1NILuKtOsQG9FngOc+lciic1iQ8uQGhJ1kVAKKXUs60RoQ5km93IfaREvuoFj7PZsy9rGXE9G/NhBsDOJ63Acp1J82eFU7OIVO1OxWGwpSU5hb0GqfMydMHYSdiMVnncNY5Vy3VbwRUEydvEaRxmAOSSqJMlJISTxS9YWTYLcg3B253xsPkc5lXk3XLlwrPLuDPKDqDIutzYaj3eweMkPeCCahO3+fEIF8SfLtg/5oI3Mh0ylKM4YRBaYzuBgPuRnBYD3mmhA1X5Aka8NKl4nNz7BaKTzSgsLCzWbvyo4eK9r15WwLKRAmmCXXDoA1kaG2F4jWFbgkxUnlcrB/xj5iHxFPiBN4JekY4nZ6ccOiQ87hgwhe+TOdogT1nfpgEDTvYAucIwHxBfNyhpGrR+F8x00WD33VCNTOr/Wd+9C51Ben7S0ZJUq3qZJ2OkZz+cL87ZfWuePlwRcHZjeUMxFwTrJZAJfSvyWZc1VgORTY8rBcubetdiOk+CO+jPOcCRTF+oZ0okUIyuQeSNL/lPrulg8flhmJHmE2gBpE9xrJNkwpN4rQIIyujGoELCQz8ggG38iGzjKkXufJ2Klun1iu65bnJub2yut3xbEK3UvsDEInCmvA6YjMeE1bCn8F9JBe1eAnS2JksmkIlEDfi8R46kkEkMWdqOv+AvS9rcp2bvk8OAESvgox7h4aWNMLd32jSMLvuwDAwORSE7Oe3ZRKrFwvYGrPOBJ2nZ20Op/mqKNzgraOTPt6Bnx5citUINIczX/jUw3xGL2+ia8KAvsvp0ePoL5hXkXO5YvQYSFAiqcJX8E/gyX8QUvv8eh9XUq3h7mE9tLJoNKqnhHXmCO+dtJ4ybSkH1jc9XRaHTMz1tATBe2UEkeAdKu/zWIkUbZxD+veLxEQhhUFmbnvOezsJrk+zmqMo6vIL2OXzPvQ8v7dgtpoQnkF/LP8Ruu9zXdJHg4igAAAABJRU5ErkJgggA=)](https://www.fiware.org/developers/catalogue/)
![License](https://img.shields.io/github/license/ging/fiware-cosmos-orion-flink-connector.svg)
[![Documentation badge](https://readthedocs.org/projects/fiware-cosmos-orion-flink-connector/badge/?version=latest)](http://fiware-cosmos-orion-flink-connector.rtfd.io)
[![](https://img.shields.io/badge/tag-fiware--cosmos-orange.svg?logo=stackoverflow)](http://stackoverflow.com/questions/tagged/fiware-cosmos) 

This is a Flink connector for the Fiware Orion Context Broker.
It has two parts:
 * **`OrionSource`**: Source for receiving NGSIv2 events in the shape of HTTP messages from subscriptions.
 * **`OrionSink`**: Sink for writing back to the Context Broker.

## Installation

Download the JAR from the latest release.
In your project directory run:
```bash
mvn install:install-file -Dfile=$(PATH_DOWNLOAD)/orion.flink.connector-1.0.jar -DgroupId=org.fiware.cosmos -DartifactId=orion.flink.connector -Dversion=1.0 -Dpackaging=jar
```

Add it to your `pom.xml` file inside the dependencies section
```xml
<dependency>
    <groupId>org.fiware.cosmos</groupId>
    <artifactId>orion.flink.connector</artifactId>
    <version>1.0</version>
</dependency>
```

## Usage
### OrionSource

* Import dependency
    ```scala
    import org.fiware.cosmos.orion.flink.connector.{OrionSource}
    ```
* Add source to Flink Environment. Indicate what port you want to listen to (e.g. 9001)
    ```scala
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val eventStream = env.addSource(new OrionSource(9001))
    ```
* Parse the received data
    ```scala
    val processedDataStream = eventStream.
        .flatMap(event => event.entities)
        // ...processing
    ```
    The received data is a DataStream of objects of the class **`NgsiEvent`**. This class has the following attributes:
    * **`creationTime`**: Timestamp of arrival.
    * **`service`**: Fiware service extracted from the HTTP headers.
    * **`servicePath`**: Fiware service path extracted from the HTTP headers.
    * **`entities`**: Sequence of entites included in the message. Each entity has the following attributes:
      * **`id`**: Identifier of the entity.
      * **`type`**: Node type.
      * **`attrs`**: Map of attributes in which the key is the attribute name and the value is an object with the following properties:
        * **`type`**: Type of value (Float, Int,...).
        * **`value`**: Value of the attribute.
        * **`metadata`**: Additional metadata.


### OrionSink
* Import dependency
    ```scala
    import org.fiware.cosmos.orion.flink.connector.{OrionSink,OrionSinkObject,ContentType,HTTPMethod}
    ```
* Add sink to source
    ```scala
    val processedDataStream = eventStream.
     // ... processing
     .map(obj =>
        new OrionSinkObject(
            "{\"temperature_avg\": { \"value\":"+obj.temperature+", \"type\": \"Float\"}}", // Stringified JSON message
            "http://context-broker-url:8080/v2/entities/Room1", // URL
            ContentType.JSON, // Content type
            HTTPMethod.POST) // HTTP method
     )

    OrionSink.addSink( processedDataStream )
    ```
The sink accepts a `DataStream` of objects of the class **`OrionSinkObject`**. This class has 4 attributes:
 - **`content`**: Message content in String format. If it is a JSON, you need to make sure to stringify it before sending it.
 - **`url`**: URL to which the message should be sent.
 - **`contentType`**: Type of HTTP content of the message. It can be `ContentType.JSON` or `ContentType.Plain`.
 - **`method`**: HTTP method of the message. It can be `HTTPMethod.POST`, `HTTPMethod.PUT` or `HTTPMethod.PATCH`.

 ## Production
>**Warning** :warning:
>
>When packaging your code in a JAR, it is common to exclude dependencies like Flink and Scala since they are typically provided by the execution environment. Nevertheless, it is necessary to include this connector in your packaged code, since it is not part of the Flink distribution.
