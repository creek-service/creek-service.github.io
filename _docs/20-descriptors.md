---
title: "Creek Descriptors"
permalink: /docs/descriptors/
description: "Discover the different types of descriptors used in Creek."
toc: true
---

Creek uses different types of descriptors to allow users to define metadata for their system components and resources.

## Component descriptors

Creek defines two types of components: aggregates and services, with the former being an abstraction, with a defined
public API, around a one or more of the latter. Both share a [common base type][componentDescriptor].

### Service descriptor

A [service descriptor][serviceDescriptor] defines the service's name, its Docker image, and [resource descriptors](#resource-descriptor)
for the external resources the service uses.

[Resource descriptors](#resource-descriptor) exposed by the service may be ones also exposed by the service's [aggregate 
descriptor](#aggregate-descriptor), and in which case these are public resources exposed as part of the aggregate's API, 
or ones defined inline by the service, and in which case they are resources internal to the aggregate.

**ProTip:** The best way to get your head around component and resource descriptors is an example.
Check out the [basic Kafka Streams tutorial][basicKsDemo] to see them in action.
{: .notice--info}

When a microservice is starting up, it initialised Creek by passing its service descriptor to [Creek to get a CreekContext][creekServiceEntryPoint].

The [Creek system tests][systemTest] discover component descriptors on the class path to allow it to start services, 
discover what 3rd party services are needed, like Kafka, and to work with external resources, such as Kafka topics.

### Aggregate descriptor

A Creek 'aggregate' is simply the public API of a logical grouping of services that together provide some business function, 
e.g. inventory tracking , or customer data, etc. The aggregate provides a higher level abstract, encapsulating the other
components it contains.

In Domain-driven-development nomenclature this would be known as a [Bounded Context][bcDDD].
{: .notice}

The aggregate descriptor defines the aggregate's name and its public API. The public API is defined as the set of
[resource descriptors](#resource-descriptor), detailing the resources services from other aggregates are allowed
to access, e.g. an aggregate may define output topics that others can consume.

[todo2]: add a protip here linking to the demo on linking aggregates once complete.

Normally, an aggregate contains services and those services are developed in the aggregate's own code repository.
However, it is also possible to use aggregates to group multiple other aggregates together, allowing for multiple
levels of abstraction and encapsulation.

## Resource descriptor

Resource descriptors are provided by Creek extensions, such as the [Creek Kafka extension][creekKafka],
which provides descriptors to define input, internal and output topics.

### Resource ownership

Resource descriptors also capture the concept of _ownership_. A resource, e.g. a Kafka topic, is almost always
conceptually _owned_ by a service, and hence an engineering team. 
Ownership implies who is responsible for the lifecycle of the resource and the data within.

Using the example of Kafka topics, service's often _own_ their output topics as these contain the data the
service is responsible for maintaining. Less common is for a service to _own_ an input topic. However, this
can occur, for example an alerts service might own its input topic, even though other service's produce alerts
to the topic.

Owned resources are managed by the service that owns them. For example, service's with owned Kafka topics will
ensure the topic exists, and any schemas registered, when the service starts up and Creek is initialised.

[todo]: why are descriptors interfaces and not classes...

[componentDescriptor]: https://github.com/creek-service/creek-platform/blob/main/metadata/src/main/java/org/creekservice/api/platform/metadata/ComponentDescriptor.java
[serviceDescriptor]: https://github.com/creek-service/creek-platform/blob/main/metadata/src/main/java/org/creekservice/api/platform/metadata/ServiceDescriptor.java
[aggDescriptor]: https://github.com/creek-service/creek-platform/blob/main/metadata/src/main/java/org/creekservice/api/platform/metadata/AggregateDescriptor.java
[creekServiceEntryPoint]: https://github.com/creek-service/creek-service/blob/main/context/src/main/java/org/creekservice/api/service/context/CreekServices.java
[creekKafka]: https://github.com/creek-service/creek-kafka
[systemTest]: https://github.com/creek-service/creek-system-test
[bcDDD]: https://martinfowler.com/bliki/BoundedContext.html
[basicKsDemo]: {{ "/basic-kafka-streams-demo" | relative_url }}

[todo]: http:// update links to docs, not repos, when docs exist
