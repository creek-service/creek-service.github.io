---
title: "Creek Extensions"
permalink: /extensions/
layout: splash
excerpt: "Expand Creek's functionality with extensions"
header:
  overlay_color: "#000"
  overlay_filter: "0.5"
  overlay_image: /assets/images/maxim-tajer-x3S1aGQNgro-unsplash.jpg
  caption: "Photo credit: [**Unsplash**](https://unsplash.com)"
  actions:
    - label: "<i class='fas fa-cog fa-spin'></i>&nbsp; Build your own"
      url: /docs/extensions/
intro:
  excerpt: "Expand Creek's functionality with extensions"

kafka_row:
  - image_path: /assets/images/apache-kafka-logo.png
    alt: "Apache Kafka Logo"
    title: "Creek Kafka"
    excerpt: |
      Build &amp; test services that utilise Kafka Clients to consume and produce data to and from Kafka.
      <p>Apache Kafka is a real-time data streaming technology capable of handling trillions of events per day.
    url: https://github.com/creek-service/creek-kafka/tree/main/client-extension # todo: differentiate this and the kafka link above.
    btn_label: "<i class='fas fa-solid fa-book-open'></i>&nbsp; Read More"
    btn_class: "btn--primary"

kafka_streams_row:
  - image_path: /assets/images/apache-kafka-streams-logo.png
    alt: "Apache Kafka Logo"
    title: "Creek Kafka Streams"
    excerpt: |
      Build &amp; test Kafka Streams based microservices.
      <p>Kafka Streams layers advanced event stream processing on top of Kafka.
    url: https://github.com/creek-service/creek-kafka/tree/main/streams-extension # todo: differentiate this and the kafka link above.
    btn_label: "<i class='fas fa-solid fa-book-open'></i>&nbsp; Read More"
    btn_class: "btn--primary"
---

## Available extensions

{% include feature_row id="kafka_row" type="left" %}
{% include feature_row id="kafka_streams_row" type="right" %}

Don't see the extension you need? Why not [build your own]({{ "/docs/extensions" | relative_url}})!
And if you have an extension you'd like listed here, then please [get in touch][serviceDiscussion]!

[serviceDiscussion]: https://github.com/creek-service/creek-service/discussions/new