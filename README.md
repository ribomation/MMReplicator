MM Replicator
============

A CA-Wily Introscope server web plugin, that can replicate Introscope
management modules.

Requirements
============

This webapp is an server-side plug-in for CA-Wily Introscope. Introscope is a commercial tool for
application performance management (APM) of (large) Java applications in production. In order to use
and/or compile this library you need to have a valid Introscope license.

This webapp has been developed and tested using Introscope version 8.

* Java 5+

* Introscope Server (EM) installed

Installation
============

Unpack the distribution ZIP and drop the (mmreplicator.war) WAR file into the
$INTROSCOPE_SERVER/webapps/ directory. Within a minute or so, it will be deployed
to the embedded Jetty of the EM. It can then be accessed using the URL

    http://MyIntroscopeHost:8081/mmreplicator

Assuming the embedded Jetty is deployed using the standard port 8081.

Usage
=====

Steps for replication are

* Create and prepare a template management module, with keywords (@KEYWORD@).

* Browse to the Replicator webapp (http://MyIntroscopeHost:8081/mmreplicator).

* Choose a template MM.

* Give the new MM a unique name and give values for all keywords.

* Generate it and within a minute the new MM will be deployed to the Introscope server.

It is also possible to script the actions, using tool such cURL (http://curl.haxx.se/)
