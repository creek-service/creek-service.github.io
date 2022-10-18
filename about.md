---
title: About
permalink: /about/
layout: default
---

While working with a stealth startup, building a cloud based financial platform that leveraged the power 
of [Kafka][kafka] and [Kafka Streams][kafka-streams] based containerised microservices, 
two things quickly became apparent.

First, developers were spending a LOT of time writing boilerplate code and dealing with low-level details
when creating new services or extending existing ones, often more time than they spent writing the business logic
of the service.

Second, developers were either relying on just unit testing, or where having to cobble together handwritten
functional tests that span up [Docker][docker] contains for external services, such as Kafka brokers.
These functional tests did not test specific classes, not the Docker container images the build spat out,
and certainly not multiple services working together.

There had to be a better way!

Creek was born to provide a better way of _quickly_ building _and testing_ containerised Kafka Streams based 
microservices. 

## What's with the name?

Being originally focused on Kafka Streams based microservices... a *creek* is a small, a.k.a. micro, stream.  

[kafka]: https://kafka.apache.org/documentation/
[kafka-streams]: https://kafka.apache.org/documentation/streams/
[docker]: https://www.docker.com/