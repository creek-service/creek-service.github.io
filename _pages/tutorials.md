---
title: "Tutorials"
layout: splash
permalink: /tutorials/
description: Unlock the basics and more advanced features of Creek and its extensions with this step-by-step tutorials
header:
  overlay_image: /assets/images/background-2.png
excerpt: >
  Quickly get up and running with Creek with these tutorials

row_1:
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
    alt: "Service composition"
    title: "Service composition"
    url: "/ks-connected-services-demo/"
    btn_class: "btn--primary"
    btn_label: "<i class='fas fa-book-open'></i>&nbsp; View Tutorial"
    excerpt: |
      Learn how to use the outputs of one service as the inputs to another, within the same aggregate.
      In this tutorial you will learn about:
       * Linking services together within the same aggregate.
       * Writing system tests that test the combined functionality of multiple services.      
  - image_path: /assets/images/tutorial-ks-connect-aggregates.svg
    alt: "Aggregate composition"
    title: "Aggregate composition"
    excerpt: >
      Learn how to compose aggregates together to build a microservice ecosystem.
      <br><br>**Coming soon...**
---

On this page you'll find tutorials to lead you through the different aspects and features of Creek.
New tutorials will be added over time, so check back in occasionally for updates.

<div class="feature__wrapper"></div>

## Kafka Streams tutorials

{% include feature_row id="row_1" %}
