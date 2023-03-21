---
title: "New tutorial: Kafka Streams - Aggregate APIs"
description: |
   This is the third, and final, in the quick-start series of tutorials, aimed at demonstrating the ease of use, power & features of Creek.
   This tutorial covers how to define the API an aggregate exposes to the rest of an organisation, how to integrate with another aggregate
   and how to integrate with parts of a system that don't use, or predate, Creek. 
categories:
 - tutorial
 
tags:
  - kafka-streams
---

It gives me great pleasure to announce that the third, and final, tutorial in the quick-start series is now live :tada:.

The [Kafka Streams aggregate API tutorial]({{ "/ks-aggregate-api-demo/" | relative_url }}) builds upon the work done
in the first [Basic Kafka Streams tutorial]({{ "/basic-kafka-streams-demo/" | relative_url }}) to walk users through 
defining the API of an aggregate, wrapping parts of a system that don't use Creek in an aggregate, and how to 
integrate one aggregate with another.

Combined, its hoped the quick-start tutorial series will provide a great introduction to the power of Creek and how to use it
to build a tested, reliable microservice architecture quickly.

I'm very happy to announce this tutorial because it completes the series, but mainly because it means I can stop working
on documentation and tutorials for a moment and pivot to coding :smiley:!

Next on the list of tasks is [adding JSON support <i class="fas fa-external-link-alt"></i>](https://github.com/creek-service/creek-kafka/issues/25){:target="_blank"}
to Creek. This is a biggie in terms of effort and impact. Creek's not much use in a real-world situation util it's done.

Once JSON support is complete, Creek will be close to moving from alpha to beta release status.
Feel free to view the [MVP project board <i class="fas fa-external-link-alt"></i>](https://github.com/orgs/creek-service/projects/3){:target="_blank"}
to see what's remaining.

It's worth noting, while it
[isn't documented yet <i class="fas fa-external-link-alt"></i>](https://github.com/creek-service/creek-kafka/issues/33){:target="_blank"}
the serialisation formats used by Creek Kafka are totally customisable. JSON support is the first on the cards, but Avro, Protobuf, and others,
including organisation-specific serialisation formats are easily supportable. 

I'll update you once JSON support is out...