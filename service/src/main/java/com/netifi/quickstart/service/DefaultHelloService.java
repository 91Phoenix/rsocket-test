package com.netifi.quickstart.service;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.netifi.quickstart.service.protobuf.HelloRequest;
import com.netifi.quickstart.service.protobuf.HelloResponse;
import com.netifi.quickstart.service.protobuf.HelloService;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Mono;

/** Service that returns a hello message. */
public class DefaultHelloService implements HelloService {
	private static final Logger logger = LogManager.getLogger(DefaultHelloService.class);

	private final String serviceName;

	public DefaultHelloService(final String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public Mono<HelloResponse> sayHello(HelloRequest message, ByteBuf metadata) {
		
		// @formatter:off
		String reducedMessage = message.getName().chars()
				.limit(10)
				.mapToObj(c -> (char) c)
				.map(String::valueOf)
				.collect(Collectors.joining());
		//@formatter:on

		logger.info("received a message -> {}", reducedMessage);

		return Mono.fromCallable(() -> HelloResponse.newBuilder()
				.setMessage("Hello, " + reducedMessage + "! from " + serviceName).build());
	}
}
