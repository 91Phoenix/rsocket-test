package com.netifi.quickstart.client;

import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.netifi.quickstart.service.protobuf.HelloRequest;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;

public class RSocketManager {

	private static RSocket socket;

	private RSocketManager() {
	}

	protected static RSocket getRSocket() {
		if (socket == null) {
			//@formatter:off
			socket = RSocketFactory.connect()
					.transport(TcpClientTransport.create(7000))
					.start()
					.block();
			//@formatter:on
		}
		return socket;
	}

	protected static Function<HelloRequest, Publisher<?>> getRSocketFunctionalRequest() {
		RSocket rSocket = getRSocket();
		return request -> {
			return rSocket.requestResponse(DefaultPayload.create(request.toByteArray())).map(Payload::getDataUtf8);
		};
	}

}
