---
title: What is Creek?
permalink: /docs/what-is-creek/
description: Understand what Creek Service is, its design goals and its architecture.
toc: true
---

Creek is a set of Java libraries, development patterns and GitHub template repositories designed to enable engineers 
to quickly develop, test and debug an ecosystem of interconnected, yet decoupled, microservices on top of Apache Kafka® 
and other technologies.

Creek helps organisations move towards using Kafka as the central nervous system running at the core of their organisation.
Creek can help organisations automate integrating Kafka into their Data Mesh.

## Project status

The project is currently in **alpha release** and in active development.

It is still missing a few key pieces of functionality before its ready for general release.
For example, JSON serialization is currently being worked on, and windowed-key support will come next.
See the [MVP Project board <i class="fas fa-external-link-alt"></i>][mvp]{:target="_blank"} for more info.

In the meantime, feel free to run through the [quick-start tutorial series][quickStart] to get a feel for what Creek is all about.

## Who is Creek for?

Creek is aimed primarily as a tool for Java engineers, or engineers using other JVM languages... 
though there are plans to move [beyond Java <i class="fas fa-external-link-alt"></i>][beyondJava]{:target="_blank"}.

It is currently focused on building microservices on top of Apache Kafka® though, again... 
there are plans to move [beyond Kafka <i class="fas fa-external-link-alt"></i>][beyondKafka]{:target="_blank"}.

[Creek system tests][systemTest] allow defining acceptance criteria for business logic in YAML. 
More technically capable business analysts and product owners can to define test cases as part of requirements capture.

## What is Creek's design philosophy? 

Creek is opinionated. It's design philosophy is to put in place patterns and best practice to make it easy for engineers
to _do the right thing_ and harder to do wrong.

Creek is powerful, yet flexible. It's design philosophy is to provide functionality to speed up development,
while maintaining flexibility and empowering engineers. 
Creek works _with_ a technology, making it easier to use, rather than wrapping, encapsulating or hiding the technology.

Creek is about rapid development of business functionality. As every good engineer knows, repeated tasks should be automated.
Creek provides template repositories and automated workflows to take the hard work out of repetitive tasks, such as creating
an empty microservice.

Creek is about producing services that work. Perhaps the biggest part of Creek is its 
[system tests][systemTest]: YAML based black-box functional testing of services running in Docker containers. 
The system tests make debugging of, and capturing code coverage metrics from, services in Docker containers easy.

Creek is designed to be extendable. It's currently focused on Kafka, but that's [just the start <i class="fas fa-external-link-alt"></i>.][beyondKafka]{:target="_blank"}.

## FAQ

### Can I use Creek without GitHub?

We recognise that not everyone uses GitHub. While Creek comes with GitHub template repositories to make creating a new repository a breeze,
it's totally possible to use Creek outside of GitHub. The GitHub workflows call simple scripts, and it would be a fairly simple task
to migrate the template to another provider, or to use them from your local machine.

### Can I use Creek with Maven / other build tool?

Of course! Creek internally uses Gradle, and uses Gradle for its template repositories. However, if your organisation
uses Maven, or any other build tool, then Creek can work with that. At its core, Creek is just Java libraries.

If you want to make use of the template repositories, then we'd recommend taking a copy of ours, and updating to use
your build tool of choice, and creating templates for your organisation or project.

### Can I use Creek without the template repos?

The [aggregate template repository <i class="fas fa-external-link-alt"></i>][aggTemp]{:target="_blank"} is designed 
to make creating a new repository, to host your microservices, a doddle.
However, there's nothing in there you can't replicate yourself. The template sets up a lot of cool features for you, like
[system tests <i class="fas fa-external-link-alt"></i>][aggSystemTest]{:target="_blank"} and 
[debugging services in docker contains <i class="fas fa-external-link-alt"></i>][serviceDebug]{:target="_blank"}, 
but by all means... use Creek how-ever you like.

### Do I have to use all of Creek?

Creek is designed so that you can pick and choose the parts you want to use. 

Sure, using the [aggregate template repository <i class="fas fa-external-link-alt"></i>][aggTemp]{:target="_blank"}
gets you set up to use the core features. But if you only want to use, for example, system tests to test an existing
service, then that's OK. Just define the service's descriptor, apply a system-test build plugin and get defining those
system tests :smile:.

### Can I use a language other than Java?

Being initially focused on Kafka Streams based microservices, Creek is understandably Java-centric at the moment.
That said, it should work well with other JVM based languages... feel free to raise issues on GitHub if it doesn't.

If you're not using a JVM based language, then Creek currently isn't for you. Though there is [work planned to move 
beyond the JVM <i class="fas fa-external-link-alt"></i>][beyondJava]{:target="_blank"}.

In the meantime, it is _feasible_ to use the [Creek system tests][systemTest] to test a non-JVM microservice, 
though we've not tried it. After all, the system tests just start Docker containers. It shouldn't matter what language
the service within the Docker container is using. To make the service discoverable by the system tests, a
[service descriptor][serviceDescriptor] would need to be written for the microservice, in Java or another JVM language.

[beyondJava]: https://github.com/creek-service/creek-service/issues/17
[beyondKafka]: https://github.com/creek-service/creek-service/issues/18
[systemTest]: /creek-system-test/
[quickStart]: /tutorials/#quick-start-tutorial-series
[mvp]: https://github.com/orgs/creek-service/projects/3
[aggTemp]: /aggregate-template/
[aggSystemTest]: /aggregate-template/features/system-testing
[serviceDebug]: /basic-kafka-streams-demo/debugging
[serviceDescriptor]: /docs/descriptors/