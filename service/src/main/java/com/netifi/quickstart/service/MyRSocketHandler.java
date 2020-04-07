package com.netifi.quickstart.service;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

public final class MyRSocketHandler extends AbstractRSocket {
	
	private static final Logger LOG = LoggerFactory.getLogger(MyRSocketHandler.class);
	
	@Override
	public Mono<Payload> requestResponse(Payload payload) {
		String name = payload.getDataUtf8();
		
		// @formatter:off
		String collect = name.chars()
				.limit(10)
				.mapToObj(c -> (char) c)
				.map(String::valueOf)
				.collect(Collectors.joining());
		// @formatter:on
		
		if (collect == null || collect.isEmpty()) {
			collect = "You";
		}
		LOG.info(collect);
		return Mono.just(DefaultPayload.create(String.format("Hello, %s!", payload)));
	}
}