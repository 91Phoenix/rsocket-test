package com.netifi.quickstart.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netifi.broker.BrokerClient;
import com.netifi.common.tags.Tags;
import com.netifi.quickstart.service.protobuf.HelloServiceServer;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.publisher.Mono;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String... args) throws Exception {
		
		if(args.length ==1) {
			String testType = args[0];
			LOG.info("using arguments: '{}'",testType);
			if(testType.equalsIgnoreCase("rsocket")) {
				RSocketServer();
			}else if(testType.equalsIgnoreCase("netifi")){
				NetifiServer();
			}
		}else {
			LOG.info("Please provide test type argument. Options: rsocket | netifi ");
		}
		
	}

	private static void NetifiServer() throws InterruptedException {
		String serviceName = "helloservice-" + UUID.randomUUID().toString();
		
		// @formatter:off
		// Build Netifi Broker Connection
		BrokerClient netifi = BrokerClient.tcp()
		        .group("quickstart.services.helloservices") // Group name of service
		        .destination(serviceName)
		        .accessKey(9007199254740991L)//read documentation for getting key and token
		        .accessToken("kTBDVtfRBO4tHOnZzSyY5ym2kfY=")
		        .host("localhost") // Netifi Broker Host
		        .port(8001) // Netifi Broker Port
		        .disableSsl() // Disabled for ssl for testing purposes
		        .build();
		// @formatter:on

		// Add Service to Respond to Requests
		netifi.addService(
				new HelloServiceServer(new DefaultHelloService(serviceName), Optional.empty(), Optional.empty()));

		// Connect to Netifi Platform
		netifi.groupServiceSocket("quickstart.services.helloservices", Tags.empty());

		// Keep the Service Running
		Thread.currentThread().join();
	}

	private static void RSocketServer() throws InterruptedException {
		
		// @formatter:off
		RSocketFactory.receive()
			.frameDecoder(PayloadDecoder.DEFAULT)
			.acceptor(new MyRSocketAcceptor())
			.transport(TcpServerTransport.create(7000))
			.start()
			.block();
		// @formatter:on

		LOG.info("RSocket server started on port: 7000");

		Thread.currentThread().join();
	}
	
	private static final class MyRSocketAcceptor implements SocketAcceptor {
		@Override
		public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
			return Mono.just(new MyRSocketHandler());
		}
	}
}
