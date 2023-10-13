# Spark-structed-streaming-poc

This is a small system for reproducing [issue#9638](https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/9638). Spark structed streaming part is written by Scala.

## Summary

To reproduce issue#9638, start the containers shown below:

  1. producer: send data to kafka broker.
  2. broker, zookeepr: kafka broker.
  3. sparkmaster: master of spark cluster and consumer.
  4. sparkworker1,2: worker of spark cluster.
  5. nginx: accepts http requests from spark cluster.
  6. jaeger: runs all of jaeger backend components.

Enable auto instrumentation using opentelemetry's java agent for the producer and
spark parts. Also, enable opentracing for the nginx part.

When send some data from producer to broker, spark cluster received the data and
finally makes an http request to nginx.

Then, expected that the span shown below will be recorded.

  - a. span of producer
  - b. span of consumer(spark part)
  - c. span of http GET(spark part)
  - d. span of nginx

Actually, when run this repro, a, c, and d are recorded, but b is not recorded.

## Requirements to reproduce

To run this repro, the commands shown below must be installed.

  - make
  - wget
  - docker.io or docker-ce
  - docker-compose-plugin or docker-compose-v2

And, since will be using the docker CLI, expected that add user to docker group.
```
  $ sudo gpasswd -a <user> docker
```
After adding a user to docker group, the user may need to logout once.

## Steps to reproduce

### Build jar files

```
  $ make
```

If the build complete successfully, it will generate two jars shown below.
```
producer/target/scala-2.12/ProducerApp.jar
spark-consumer/target/scala-2.12/ConsumerApp.jar
```

### Start containers

```
  $ make up
  [...]
  [+] Running 10/10
   ? Network poc-net         Created                                     0.1s
   ? Container producer      Started                                     0.8s
   ? Container jaeger        Started                                     1.0s
   ? Container zookeeper     Started                                     0.6s
   ? Container nginx         Started                                     0.8s
   ? Container broker        Started                                     1.0s
   ? Container init-broker   Started                                     1.4s
   ? Container sparkmaster   Started                                     2.1s
   ? Container sparkworker2  Started                                     3.0s
   ? Container sparkworker1  Started                                     3.0s
```

### Confirm spark batch execution is started

The logs shown below is output every 10 seconds.

```
  $ docker logs sparkmaster -f
  ...
  INFO MicroBatchExecution: Streaming query made progress: { ...
```

### Produce some data

```
  $ docker exec -it producer bash
  root@0588c6083499:/# java -javaagent:/tmp/opentelemetry-javaagent.jar -jar /app/ProducerApp.jar
```

If the produce successfully, logs shown below(`produced` is outputed):
```
  ...
  [main] INFO org.apache.kafka.common.metrics.Metrics - Metrics reporters closed
  [main] INFO org.apache.kafka.common.utils.AppInfoParser - App info kafka.producer for producer-1 unregistered
  produced.
```

### Confirm recorded span from jaeger UI

Jaeger frontend is running on port 16686. Please access.

```
http://<your ip>:16686
```

Probably, the span of the consumer receive operation from kafka is not recorded in jaeger.

  1. `producer.service sample-topic publish`
  2. Span of receive operation is not recorded
  3. `spark-consumer.service GET`
  4. `nginx-opentracing.service nginx-opentracing`

### Exit containers

```
  $ make down
```
