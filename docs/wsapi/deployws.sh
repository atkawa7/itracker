#!/bin/sh

export CLASSPATH=$CLASSPATH:./axis.jar:./axis-ant.jar:./commons-discovery.jar:./commons-logging.jar:./jaxrpc.jar:./log4j-1.2.4.jar:./saaj.jar:./wsdl4j.jar

java org.apache.axis.client.AdminClient -lhttp://$1/itracker/api/services/AdminService ../docs/wsapi/ws-dd.$2.xml

