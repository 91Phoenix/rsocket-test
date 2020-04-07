package com.netifi.quickstart.client;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;

import com.netifi.quickstart.service.protobuf.HelloRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Main {

	private static final String RSOCKET = "rsocket";
	private static final String EMPTY_STRING = "";
	private static final String NETIFI = "netifi";
	private static final String SMALL = "small";
	private static final Logger logger = LogManager.getLogger(Main.class);

	public static void main(String... args) {

		/*
		 * ADAPT THE SERVER DEPENDING ON THE TEST YOU WISH TO RUN. YOU MIGHT NEED TO
		 * START NETIFI TOO.
		 * 
		 */

		TestType testType = null;
		if (args.length == 2) {
			try {
				testType = TestType.valueOf(args[0].toUpperCase());
			} catch (IllegalArgumentException e) {
				testType = TestType.RSOCKET;// default
			}
			String payloadType = args[1].equalsIgnoreCase(SMALL) ? EMPTY_STRING : Constant.GRANDE_STRINGA;
			logger.info("Using arguments: '{}' and '{}'", testType, payloadType);
			requestResponseTest(payloadType, testType);
		} else {
			logger.info("Please provide test type argument. Options: rsocket | netifi");
			logger.info("Please provide also payload type argument. Options: small | big");
		}

	}

	private static void requestResponseTest(String additionalPayload, TestType testType) {
		/*
		 * Request/Response 60 Seconds of interactions max speed with variable payload
		 * if you activate logging note that to log each response takes way longer than
		 * the time taken to perform all the interactions
		 */

		// type evaluation
		boolean isRSocketTest = testType == TestType.RSOCKET;
		// retrieve appropriate publisher
		Function<HelloRequest, Publisher<?>> clientRequestFunction = getPublisher(isRSocketTest);

		// @formatter:off
		List<?> resultSet = Flux.interval(Duration.ofNanos(30000)) //produce integers every 30000 ns
				.onBackpressureBuffer() // here two different backpressure strategies buggering vs dropping
//				.onBackpressureDrop(ci -> {
//					logger.info("dropping '{}'",ci);// backpressure support. See Reactive Manifesto
//					return ;
//				})
				.take(Duration.ofMinutes(1)) // limit integers production to 1 minute -> 2M integers considering the producer above
				.flatMap(buildRequest(additionalPayload)) //build request
				.flatMap(clientRequestFunction) //send request
				.collect(Collectors.toList()) //collect into list
				//.doOnNext(response -> logger.info("Got response '{}'", response)) //logging each response.
				.doFinally(response -> logger.info("done '{} test with payload size '{}'!",testType.name(),additionalPayload.length()))
				.block();
		// @formatter:on

		// verify that last element is the expected one
		int resultSetSize = resultSet.size();
		logger.info("responses '{}'", resultSetSize);
		logger.info("last response '{}'", resultSet.get(resultSetSize - 1));

		releaseResources(isRSocketTest);
	}

	private static Function<HelloRequest, Publisher<?>> getPublisher(boolean isRSocketTest) {
		Function<HelloRequest, Publisher<?>> performRequestFunction = null;

		if (isRSocketTest) {
			performRequestFunction = RSocketManager.getRSocketFunctionalRequest();
		} else {
			performRequestFunction = NetifiManager.getNitifiFunctionalRequest();
		}
		return performRequestFunction;
	}

	private static void releaseResources(boolean isRSocketTest) {
		if (isRSocketTest) {
			RSocketManager.getRSocket().dispose();
		} else {
			NetifiManager.getConnection().dispose();
		}
	}

	private static Function<Long, Mono<HelloRequest>> buildRequest(String additionalPayload) {
		return intervalCounter -> {
			return Mono.just(
					HelloRequest.newBuilder().setName(String.valueOf(intervalCounter) + additionalPayload).build());
		};
	}
}
