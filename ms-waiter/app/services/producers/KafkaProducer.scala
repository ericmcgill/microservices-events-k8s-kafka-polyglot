package services.producers

import java.util.{Properties}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object KafkaProducer {

  def send(topic: String, m: String): Unit = {
    val brokers = "localhost:9092"
    val props = new Properties()
    props.put("bootstrap.servers", brokers)
    props.put("client.id", "ms-waiter")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    val data = new ProducerRecord[String, String](topic, m)

    //async
    //producer.send(data, (m,e) => {})
    //sync
    producer.send(data)
    producer.close()
  }



}
