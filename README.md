# RSocket-test

## Build

from parent folder run: mvn clean install

## Start server
from parent folder run: gradlew run:service --args'rsocket'. the argument can also be netifi, but in that case you will need to start a netifi broker too. Please find the  instructions about Netifi below.

## Start client

from parent folder run: gradlew run:client --args"rsocket small". The first argument can be netifi and a broker has to be started as for the server. The second argument can be "big" as well.

## Start Netifi broker

Note: this step is only needed if you want to test the netifi example rather than the plain rsocket one. Run from anywhere:
docker run -p 7001:7001 -p 8001:8001 -p 8101:8101 -e BROKER_SERVER_OPTS=" '-Dnetifi.broker.ssl.disabled=true' '-Dnetifi.authentication.0.accessKey=9007199254740991'  '-Dnetifi.authentication.0.accessToken=kTBDVtfRBO4tHOnZzSyY5ym2kfY=' '-Dnetifi.broker.admin.accessKey=9007199254740991' '-Dnetifi.broker.admin.accessToken=kTBDVtfRBO4tHOnZzSyY5ym2kfY='" netifi/broker:1.6.10
