package AkkaDataStreams

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip, ZipWith}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.{IntegerDeserializer, StringDeserializer}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory


object AkkaStream4 extends App {

  implicit val system = ActorSystem("system")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher


  LoggerFactory
    .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
    .asInstanceOf[Logger]
    .setLevel(Level.ERROR)

  val config = ConfigFactory.load()
  val consumerConf = config.getConfig("akka.kafka.consumer")
  val consumerSettings = ConsumerSettings(consumerConf, new StringDeserializer(), new StringDeserializer())


  val consumer = Consumer
    .plainSource(consumerSettings, Subscriptions.topics("test"))
    .map { msg: ConsumerRecord[String, String] => Integer.parseInt(msg.value()) }


  val graph = GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    val input = builder.add(consumer)

    val multTwo = builder.add(Flow[Int].map(x => x * 2))
    val multTen = builder.add(Flow[Int].map(x => x * 10))
    val multThree = builder.add(Flow[Int].map(x => x * 3))

    val broadcast = builder.add(Broadcast[Int](3))

    val zip = builder.add(ZipWith.apply((A1: Int, A2: Int, A3: Int) => {
      A1 + A2 + A3
    }))

    input ~> broadcast
    broadcast.out(0) ~> multTwo ~> zip.in0
    broadcast.out(1) ~> multTen ~> zip.in1
    broadcast.out(2) ~> multThree ~> zip.in2


    val output = builder.add(Sink.foreach[(Int)](println))

    zip.out ~> output

    ClosedShape

  }

  RunnableGraph.fromGraph(graph).run()

}