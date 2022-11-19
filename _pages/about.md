---
permalink: /about/
title: "About"
---

Creek is an opinionated way of quickly building *and testing* an ecosystem of interconnected microservices.
While currently very much Kafka focused, its is built from the group up to be [extendable](/docs/extensions/).

[<i class='fas fa-cog'/>&nbsp; Get Started]({{ "/docs/quick-start-guide/" | relative_url }}){: .btn .btn--success .btn--large}

---

## Contact

The best way to get help or get in touch is to start a discussion on GitHub.
If unsure which Creek repository to start the discussion under, use [creek-service][creek-service-discussion]

---

## Creek Story

While working with a stealth startup, building a cloud based financial platform that leveraged the power
of [Kafka][kafka] and [Kafka Streams][kafka-streams] containerised microservices,
two things quickly became apparent.

First, developers were spending a _lot_ of time writing boilerplate code and dealing with low-level details
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

Being originally focused on Kafka Streams based microservices... a *creek* is a small stream;
'small' gives us 'micro(service)'; giving us and streams based microservice. Ta da!

[kafka]: https://kafka.apache.org/documentation/
[kafka-streams]: https://kafka.apache.org/documentation/streams/
[docker]: https://www.docker.com/
[creek-service-discussion]: https://github.com/creek-service/creek-service/discussions/new

Creek Service is designed, developed, and maintained by Andrew Coates. 
