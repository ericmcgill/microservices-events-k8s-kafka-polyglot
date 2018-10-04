package services.consumers

import java.util.concurrent._
import java.util.{Collections, Properties}

import kafka.utils.Logging
import models.{Metadata, OrderRequest}
import play.Logger
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import models.{Metadata, OrderRequest}
import play.api.libs.json.{JsError, JsSuccess, Json}
import services.producers.KafkaProducer

import scala.collection.JavaConversions._

class KafkaOrderFromCustomerListener extends Logging {

  Logger.info("KafkaOrderFromCustomerListener starting...")
  val brokers = "localhost:9092"
  val groupId = "ms-waiter"
  val topic = "orders-from-customers"
  val props = createConsumerConfig(brokers, groupId)
  val consumer = new KafkaConsumer[String, String](props)
  var executor: ExecutorService = null

  def shutdown() = {
    if (consumer != null)
      consumer.close()
    if (executor != null)
      executor.shutdown()
  }

  def createConsumerConfig(brokers: String, groupId: String): Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props
  }

  def run() = {
    consumer.subscribe(Collections.singletonList(this.topic))

    Executors.newSingleThreadExecutor.execute(    new Runnable {
      override def run(): Unit = {
        while (true) {
          val records = consumer.poll(1000)

          for (record <- records) {
            System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset())
            val orderFromCustomerJson = Json.parse(record.value()).validate[OrderRequest]

            orderFromCustomerJson match {
              case s: JsSuccess[OrderRequest] =>
                val topic = "order-in"
                val role = "waiter"
                val or = s.get
                val newMetadata = or.metadata.copy(contributors = or.metadata.contributors :+ role, role = role, cmd = topic)
                val newOrder = or.copy(metadata = newMetadata)
                val out = Json.toJson(newOrder).toString()
                Logger.info(s"Sending: $out")
                KafkaProducer.send(topic = topic, m = out)
              case e: JsError => println("Errors: " + JsError.toJson(e).toString())
            }

          }
        }
      }
    })
  }
}

object KafkaOrderFromCustomerListener {
  Logger.info("In companion object for KafkaOrderFromCustomerListener")
  val listener = new KafkaOrderFromCustomerListener()
  listener.run()
}

