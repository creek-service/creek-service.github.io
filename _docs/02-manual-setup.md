---
title: Manual Quick-Start Guide
permalink: /docs/manual-quick-start/
description: This guide takes you through the steps needed to manually integrate Creek into an existing or new repository.
toc: true
snippet_comment_prefix: "//"
---

This guide takes you through the steps needed to manually integrate Creek into an existing or new repository.

**ProTip:** We recommend using the [Aggregate template](/aggregate-template) to bootstrap new repositories as it brings with it a lot 
of really useful features beyond just adding dependencies on the Creek libraries. Things like building & publishing
of service docker container and system test code coverage. These can be configured manually, but manual is always 
more error prone.
{: .notice--info}

## Define aggregate descriptor

It is recommended that you define you aggregate's descriptor in its own module. 
By convention this module is called `api`, though the name is not important.

This `api` module defines the public API of the aggregate, and should be published to your artefact repository, 
so that other aggregates can access it.

### Dependencies

As this module is shares with other aggregates, its production dependencies should be kept to a minimum. 
Minimising dependencies minimises the chances of dependency hell. 
Where possible, limit production dependencies to Creek extension metadata libraries, such as `creek-kafka-metadata`.
Such libraries provide interface types that can be implemented to define resources the extension handles, such as Kafka topics.

For example, if the aggregate will have Kafka topics as part of its API, then its dependencies might look like:


```groovy
logging.captureStandardOutput LogLevel.INFO
println 'A message which is logged at INFO level'
```

<details open>
<summary>Groovy</summary>

```groovy
logging.captureStandardOutput LogLevel.INFO
println 'A message which is logged at INFO level'
```

</details>

<details>
<summary>Kotlin</summary>

```kotlin
logging.captureStandardOutput(LogLevel.INFO)
println("A message which is logged at INFO level")
```

</details>

{% highlight kotlin %}
{% include_snippet dependencies from examples/gradle-kotlin-example/api/build.gradle.kts %}
{% endhighlight %}

{% highlight groovy %}
{% include_snippet dependencies from examples/gradle-groovy-example/api/build.gradle %}
{% endhighlight %}

### Resource descriptor implementations

Extensions provide interfaces, rather than implementations, to minimise dependencies and to allow repositories
to customise the types as required.
Creek extensions guarantees backwards compatability on their metadata types within the same major version.
The `api` module should define implementations of the types.
{: .notice}

**ProTip:** The [Aggregate template](/aggregate-template) contains an example implementation of the Kafka extension's
metadata types
{: .notice--info}

### Aggregate descriptor implementation

Todo:
This module needs to depend on ...   keep it deps free!
This module needs to define implementations of...

## Define service descriptors

It is recommended that you define all your service descriptors in the same, separate, module. 
By convention this module is called `services`, though the name is not important.

This module should not be published. It is used internally by each microservice in the repository and by the 
system tests. This keeps micro-dependencies within the repository simple: having all the service descriptors 
in a dedicated module means each service's module and any system tests only need to depend on this one module,
and not each other.

The `services` module should depend on the module containing the aggregate descriptor, by convention the `api` module,
so that service descriptors can access the internal implementations of resource descriptors and the public instances
of resource descriptors defined in the aggregate descriptor.

Todo:
This module needs to define implementations of... (if not already defined in the api module)

## Initialising Creek in your service
### Service dependencies

To start using Creek in a microservice, first add dependencies on the `creek-service-context` library, which provides 
the service with a way to initialise Creek, and the [Creek extentions](/extensions) the service will need, e.g.  `creek-kafka-streams-extension`:




## Using Creek in your service
## Configuring system tests
## Writing system tests
## Configuring code coverage
## Debugging Docker based microservices



Todo: blog on how to debug docker based microservices

Bootstrap a new aggregate - Creek Service, write business logic, not boilerplate