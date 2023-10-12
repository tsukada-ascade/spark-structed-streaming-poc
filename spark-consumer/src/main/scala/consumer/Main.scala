package consumer

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder

object ConsumerApp extends App with StrictLogging {
  val config = ConfigFactory.load()
  val appName = config.getString("appName")
  var logLevel = config.getString("logLevel")
  val readTopic = config.getString("kafka.read.topic")
  val readBrokers = config.getString("kafka.read.bootstrap.servers")

  val spark = SparkSession
    .builder()
    .appName(appName)
    .getOrCreate()

  spark.sparkContext.setLogLevel(logLevel)

  import spark.implicits._

  logger.info("kafka read brokers: " + readBrokers)
  logger.info("kafka read topic: " + readTopic)

  val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", readBrokers)
    .option("subscribe", readTopic)
    .option("includeHeaders", true)
    .load()

  df.select(col("value").as("data"), col("headers").as("headers"))
    .select("data", "headers")
    .writeStream
    .foreachBatch((bds:Dataset[Row], bid:Long) => {
      bds.foreach(row => {
        // Receive data successfully, but **no spans recorded by otel javaagent**.

        // Since this is a just sample, some processing for `row` is omitted.
        // logger.info("row: " + row)

        // Make dummy HTTP get request to nginx-opentracing. Auto instrumentation
        // by otel javaagent **is worked**.
        val config = ConfigFactory.load()
        val url = config.getString("http.url")
        val request = new HttpGet(url)
        val response = HttpClientBuilder.create().build().execute(request)
        val status = response.getStatusLine().getStatusCode()
        logger.info("response status: " + status)
      })
    })
    .start()
    .awaitTermination()
}
