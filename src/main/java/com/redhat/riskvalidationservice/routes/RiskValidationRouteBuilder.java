package com.redhat.riskvalidationservice.routes;

import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;

import java.util.logging.Logger;

public class RiskValidationRouteBuilder extends RouteBuilder {

	private static final Logger LOG = Logger.getLogger(RiskValidationRouteBuilder.class.getName());

	private String kafkaBootstrap = "my-cluster-kafka-brokers:9092";
	private String kafkaCreditTransferCreditorTopic = "sensu";
	private String consumerMaxPollRecords ="50000";
	private String consumerCount = "3";
	private String consumerSeekTo = "beginning";
	private String consumerGroup = "invokeansible";
	private String consumerGroup2 = "risk";




	@Override
	public void configure() throws Exception {
		try {
			System.out.println("starting account validation service");


			KafkaComponent kafka = new KafkaComponent();
			kafka.setBrokers(kafkaBootstrap);
			this.getContext().addComponent("kafka", kafka);

			from("kafka:" + "failed-decision" + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
					+ consumerMaxPollRecords + "&seekTo=" + "beginning"
					+ "&groupId=" + "process")
					.process(new AnotherEnricher())
					.to("kafka:"+"process-defn"+ "?brokers=" + kafkaBootstrap);


		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final class AnotherEnricher implements org.apache.camel.Processor {

		@Override
		public void process(Exchange exchange) throws Exception {

			com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();


			exchange.getIn().setBody("{\"data\":\"Rule failed for node1\"}");

		}
	}

}
