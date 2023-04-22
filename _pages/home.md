---
title: "Creek Service: write business logic, not boilerplate"
layout: splash
permalink: /
header:
  overlay_image: /assets/images/background-1.png
  actions:
    - label: "<i class='fas fa-cog'></i> Get started"
      url: "/docs/quick-start-guide/"
    - label: "<i class='fas fa-sharp fa-solid fa-cube'></i> v0.4.1 Preview Release"
      url: "/releases/2023/04/22/v0.4.1-released.html"
excerpt: |
  Quickly build, test and link an ecosystem of microservices,  
  using Apache Kafka® clients, Kafka Streams and more...
row_1:
  - image_path: /assets/images/feature-accelerate.svg
    alt: "Quick to develop"
    title: "Accelerate microservice development"
    excerpt: |
      Bootstrapping a repository to host new microservices, or adding a microservice to an existing repository,
      can be a time-consuming exercise taking hours, if not days. With Creek, it takes just minutes.
   
      Creek handles the boilerplate, allowing engineers to focus on the business logic.
        
      Creek's lightweight Java libraries help with topic provisioning, schema management, and serde creation; 
      and its template repositories and workflows take the leg work out of bootstrapping new repositories and services.
row_2:
  - image_path: /assets/images/feature-test.svg
    alt: "Improve testing"
    title: "Increase test coverage and effectiveness"
    excerpt: |
      Creek makes blackbox testing of your microservices easy. 

      Write easy to understand system tests in YAML, which test your services running in local Docker containers.
      By testing the actual Docker containers you'll deploy into production, 
      you can be confident the business logic meets your spec.  
      
      Test an individual service or several services working together to deliver business functionality. 
    
row_3:
  - image_path: /assets/images/feature-debug.svg
    alt: "Debug Docker"
    title: "Debug service code running inside Docker containers"
    excerpt: |
      Creek tests your services running in local Docker containers.
      When tests are failing, often the quickest way to determine what is going wrong is to attach a debugger.
      However, attaching a debugger to a process running inside a Docker container isn't trivial.

      Creek can configure your services and their Docker containers to automatically connect to the debugger
      on start up. Making debugging your services as easy as setting a breakpoint in your IDE.

row_4:
  - image_path: /assets/images/feature-teams.svg
    alt: "Collaborate"
    title: "Collaborate across teams and departments"
    excerpt: |
      One of the biggest challenges larger organisations often encounter is enabling cross department / team
      engineering work in a structured, evolvable way.

      Creek promotes design patterns that break complex organisational architectures into encapsulated 
      (business) units, with clear distinction between public APIs and internal state.

row_5:
  - image_path: /assets/images/feature-opinionated.svg
    alt: "Opinionated"
    title: "Bring structure to your Kafka cluster"
    excerpt: |
      Apache Kafka® isn't opinionated: it will allow you to create many topics and store any data. 
      This makes Kafka powerful and flexible, and makes it really easy to get your clusters in an unmaintainable mess. 

      Creek is opinionated just enough to encourage teams to do things the right way, 
      from clearly defined public/private topics to registering schemas for topic payloads.
  
row_6:
  - image_path: /assets/images/feature-extendable.svg
    alt: "Extendable"
    title: "Kafka focused with aspirations beyond"
    excerpt: |
      Creek is currently focused on supporting a microservice architecture built on top of Apache Kafka®.
      While we're not alone in thinking Kafka is amazing and should be the central nervous system of your organisation,
      enterprise systems will ultimately need to integrate with a wide range of technologies.

      Creek is built from the ground up to be extendable. This may be as simple as a custom Kafka data formats,
      to extending Creek to work with _and test_ RESTful APIs, database, cloud services or in-house technologies used in your enterprise.
  
row_7:
  - image_path: /assets/images/feature-cost.svg
    alt: "Open Source Software"
    title: "Open Source and Free to use"
    url: https://github.com/creek-service
    btn_class: "btn--primary"
    btn_label: "<i class='fab fa-fw fa-github'></i>&nbsp; View on GitHub"
    excerpt: "Creek is open-source software, hosted on GitHub, and free to use under the Apache 2.0 License."
---

{% include feature_row id="row_1" type="left" %}
{% include feature_row id="row_2" type="right" %}
{% include feature_row id="row_3" type="left" %}
{% include feature_row id="row_4" type="right" %}
{% include feature_row id="row_5" type="left" %}
{% include feature_row id="row_6" type="right" %}
{% include feature_row id="row_7" type="left" %}
