package producer

import java.util.Properties
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

object ProducerApp extends App {
  val config = ConfigFactory.load()
  val writeTopic = config.getString("kafka.write.topic")
  val writeBrokers = config.getString("kafka.write.bootstrap.servers")
  val writeGroupId = config.getString("kafka.write.group.id")

  val props = new Properties()
  props.put("bootstrap.servers", writeBrokers)
  props.put("key.serializer", classOf[StringSerializer])
  props.put("value.serializer", classOf[StringSerializer])

  println("bootstrap.servers: " + writeBrokers)

  val producer = new KafkaProducer[String, String](props)
  val record = new ProducerRecord[String, String](writeTopic, "key", "Hi, kafka " + new java.util.Date)
  producer.send(record).get()
  producer.close()
  println("produced.")
}
