---
title: "Tutorials"
layout: splash
permalink: /tutorials/
description: Unlock the basics and more advanced features of Creek and its extensions with this step-by-step tutorials
header:
  overlay_image: /assets/images/background-2.png
excerpt: >
  Quickly get up and running with Creek with these tutorials

row_quick_start:
  - image_path: /assets/images/tutorial-ks-basic.svg
    alt: "Basic Kafka Streams"
    title: "Basic Kafka Streams"
    url: "/basic-kafka-streams-demo/"
    btn_class: "btn--primary"
    btn_label: "<i class='fas fa-book-open'></i>&nbsp; View Tutorial"
    excerpt: |
      Get your head around the basics of what Creek is all about. In this tutorial you will learn about: 
       * Using the aggregate-template to bootstrap new aggregate repositories and services.
       * Defining service descriptors, including inputs and output topics.
       * Writing a simple Kafka Streams topology using Creek.
       * Writing system testing & unit tests.
       * Debugging your service code running inside a Docker container.
       * Capturing code coverage metrics.
  - image_path: /assets/images/tutorial-ks-connect-services.svg
    alt: "Connecting services"
    title: "Connecting services"
    url: "/ks-connected-services-demo/"
    btn_class: "btn--primary"
    btn_label: "<i class='fas fa-book-open'></i>&nbsp; View Tutorial"
    excerpt: |
      Learn how to use the outputs of one service as the inputs to another, within the same aggregate.
      In this tutorial you will learn about:
       * Linking services together within the same aggregate.
       * Writing system tests that test the combined functionality of multiple services.      
  - image_path: /assets/images/tutorial-ks-aggregate-api.svg
    alt: "Defining aggregate APIs"
    title: "Defining aggregate APIs"
    url: "/ks-aggregate-api-demo/"
    btn_class: "btn--primary"
    btn_label: "<i class='fas fa-book-open'></i>&nbsp; View Tutorial"
    excerpt: >
      Learn how to define the API an aggregate exposes to the world, encapsulating the services within.
      In this tutorial you will learn about:
       * Defining an aggregate's api in an aggregate descriptor.
       * Integrating with systems that don't use, or predate, Creek.
       * Integrating with other Creek based aggregates.
       * Publishing the api.
       * Data Products.
       * Managing API and schema evolution.

additional_ks:
  - image_path: /assets/images/tutorial-ks-state-stores.svg
    alt: "State Stores"
    title: "Kafka Streams: State stores and repartition topics"
    excerpt: |
      Learn how to define the metadata for Kafka Stream state stores and repartition topics.
      <br><br>**Coming soon...**
---

On this page you'll find tutorials to lead you through the different aspects and features of Creek.
New tutorials will be added over time, so check back in occasionally for updates.

<div class="feature__wrapper"></div>

## Kafka Streams tutorials

### Quick-start tutorial series

{% include feature_row id="row_quick_start" %}

### Other Kafka Streams tutorials

{% include feature_row id="additional_ks" %}