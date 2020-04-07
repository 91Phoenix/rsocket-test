package com.netifi.quickstart.client;

import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.netifi.broker.BrokerClient;
import com.netifi.broker.rsocket.BrokerSocket;
import com.netifi.common.tags.Tags;
import com.netifi.quickstart.service.protobuf.HelloRequest;
import com.netifi.quickstart.service.protobuf.HelloServiceClient;

public class NetifiManager {

	private static HelloServiceClient client;
	private static BrokerSocket connession;

	private NetifiManager() {
	}

	private static HelloServiceClient getClient() {

		if (client == null) {

		// @formatter:off
		BrokerClient netifi = BrokerClient.tcp().group("quickstart.clients") // Group name of client
				.destination("client1") // Name of this client instance
				.accessKey(9007199254740991L) // to generate them have a look at netifi documentation
				.accessToken("kTBDVtfRBO4tHOnZzSyY5ym2kfY=").host("localhost") // Netifi Broker host
				.port(8001) // Netifi Broker Port
				.disableSsl() // Disabled for testing purposes
				.build();
		// @formatter:on

			// Connect to Netifi Platform
			connession = netifi.groupServiceSocket("quickstart.services.helloservices", Tags.empty());
			// Create Client to Communicate with the HelloService (included example service)
			
			client = new HelloServiceClient(connession);
		}
		return client;
	}
	
	protected static BrokerSocket getConnection() {
		if(connession== null) {
			getClient();
		}
		return connession;
	}

	protected static Function<HelloRequest, Publisher<?>> getNitifiFunctionalRequest() {
		HelloServiceClient client = getClient();
		return request -> {
			return client.sayHello(request);
		};
	}

}
