---
title: "Shared schema: when to use and when _not_ to"
description: |
  Should multiple teams collaborate and use common shared schema?  
  Should the output of one team's services include types defined in another?
  These are important questions, and getting them wrong can lead to a whole heap of pain down the road.
header:
  image: /assets/images/shared-schema.png
categories:
  - articles
tags:
  - kafka
  - json
  - json-schema
  - avro
  - schema
toc: true
---

There are challenges when it comes to sharing schemas and data across architectural or organisational boundaries.
In this post, we'll look at the cost and potential pitfalls and come up with guidelines for when to use shared schema, and when not to.

## What is a shared schema?

In the context of this post, a shared schema is one used in multiple places, it is not a schema that's been shared to allow others to read data using it. 

This post will focus on how to share schemas between _data products_. 
However, for those not defining data products, the principles are equally applicable to sharing schemas across other architectural or organisational boundaries.
For example, sharing schema across different data-sets, or between teams, departments, companies, etc.

A _data product_ is a curated set of data, that conforms to a known schema, which others can consume.
The schemas of the product define a data-contract: an defined API, but for data, not code.
Thinking of your data in terms of being a product is a key principle of building a Data Mesh.
{: .notice--info}

When it comes to sharing a schema, these are two main questions to consider: 
1. **evolution**: does the schema change over time?
2. **ownership**: who is responsible for maintaining the schema and its evolution?

## tl;dr

For those just looking for the juice, here are some quick guidelines around using shared schema: 

| Schema you'd like to embed              | Recommendation            | Example                                                                                                                                                                        |
|-----------------------------------------|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Someone else's identifier / key schema  | ✅ - not a problem         | A `UserId`, a `ProductId`, etc. Think primary key columns in a DB, both single or multiple fields. OK to share as they don't evolve/change.                                    |
| Someone else's object / value schema    | ❌ - avoid like the plague | A full, rich `User` or `Product` object. The schema is highly likely to evolve/change, which causes many issues.                                                               |
| Common value types with a stable schema | ✅ - but, tread carefully  | `Currency`, `Country`, `EmailAddress`, etc. Think simple, common types, not provided by the schema implementation by default. OK to share as long as they don't evolve/change. |
| Your own schema                         | ✅ - not a problem         | Do as you like, share them, evolve them, remove them. As long as its evolvable.                                                                                                |


## An example scenario

Let's say we have a team, doing the right thing and producing a `Users` _data product_, containing the attributes of the company's users.

This team, being good engineers, knows it needs to ensure changes to the schemas are fully evolvable, so that changes don't break downstream consumers of the data.

For the sake of example, let's say the `Users` schema is something basic, like the following Avro schema:

```json
{
  "type": "record",
  "name": "User",
  "namespace": "acme.users",
  "fields": [
    {
      "name": "userId",
      "type": "long"
    },
    {
      "name": "fullName",
      "type": "string"
    },
    {
      "name": "residentialAddress",
      "type": "Address"
    }
  ]
}
```

With a simple `Address` type:


```json
{
  "type": "record",
  "name": "Address",
  "namespace": "acme.users",
  "fields": [
    {
      "name": "line1",
      "type": "string"
    },
    {
      "name": "line2",
      "type": "string"
    },
    {
      "name": "postCode",
      "type": "string"
    }
  ]
}
```

For example, a `User` may look like:

```json
{
  "userId": 257363658353,
  "fullName": "Miss Emily Stewart",
  "residentialAddress": {
    "line1": "13 Main Street",
    "line2": "London",
    "postCode": "SW13 6JF"    
  }
}
```

A less experienced team is responsible for creating an `OpenOrders` data product, containing all the open orders a user has placed.

This team, being kind souls and knowing that many downstream teams will need to know the details of the users who've placed the orders, _denormalises_ the user details into their data.

For the sake of example, let's say the `OpenOrders` schema is something basic, like the following Avro schema:

```json
{
  "type": "record",
  "name": "OpenOrder",
  "namespace": "acme.orders",
  "fields": [
    {
      "name": "orderId",
      "type": "long"
    },
    {
      "name": "user",
      "type": "acme.users.User"
    },
    {
      "name": "items",
      "type": {
        "type": "array",
        "items": "OrderItem"
      }
    }
  ]
}
```

An order placed by Emily may look like:

```json
{
    "orderId": 123456789012,
    "user": {
      "userId": 257363658353,
      "fullName": "Miss Emily Stewart",
      "residentialAddress": {
        "line1": "13 Main Street",
        "line2": "London",
        "postCode": "SW13 6JF"
      }
    },
    "items": [
        {
          "productId": 123456789012,
          "quantity": 1
        },
        {
          "productId": 234567890123,
          "quantity": 2
        }
    ]
}
```

The `OpenOrder` schema references the `User` schema, allowing an instance of an `OpenOrder` to include the full set of the `User` attributes. This removes the burden on downstream teams of having to join these two data sets together.

At first glance, this may look like a good approach: do the join in one place, and it's an approach many teams use when first starting to adopt schemas and data products.

## The problem

The use of the `User` schema, owned and managed by the first team, in the `OpenOrder` schema, owned and managed by the second team, is a code smell (data smell? schema smell?)
But why? You may ask!

### Obese data

The `User` schema in the example is tiny. A real-world example would have much more information.
By including the `User` in the `OpenOrder`, the size of an `OpenOrder` can drastically increase.

Yes, _some_ downstream use cases may need _some_ of this extra information, but by including it _everyone_ needs to pay the price.

That's higher network, cpu and memory utilisation, potentially increased storage costs and certainly slower deserialization and higher latency for all: Yay!

### Stale data

What happens when the `User` data changes? Maybe Emily moves home, or gets married and changes her name. Now all of Emily's open orders are stale, containing incorrect information.

Either downstream teams are working with stale data (and let's assume we all agree that's bad!), or the `OpenOrder` data needs republishing when the `User` data changes.

The republishing requires extra application complexity: rather than just _joining_ to the user data, it now needs to subscribe for changes too.

Republishing increases the rate at which the data changes, meaning _more_ data needs moving around and consumed by downstream teams.

That's higher network, cpu and memory utilisation, potentially increased storage costs and certainly slower deserialization and higher latency for all: Yay!

### Stale schema

In a well-engineered system, with correct use of shared schema, a team consuming a data product need not worry about keeping up with the latest version of the product's schema.
The only time they need to update their dependencies is when _there is something they need_ in a later version.

It's perfectly fine for a consuming team to use an old schema for as long as they like. 
(Full schema compatibility ensures all the data is compatible with their version of the schema.)

In this idyllic utopia, the schema and the data-contract they represent _decouple_ data producers and consumers.

When the `User` schema was embedded into the `OpenOrder` schema, it increased coupling between the two teams and products.
The `OpenOrder` schema references a specific version of the `User` schema. 
When the `User` schema changes, it needs explicitly updating in the `OpenOrder` schema, otherwise the user data embedded in the `OpenOrder` can be incomplete.

To demonstrate this, imagine a downstream consumer of the `OpenOrder` data responsible for delivering orders to customers. Consider what happens when a new optional `deliveryAddress` field is added to the `User` schema. 
The delivery system is updated to the latest `User` schema and enhanced to route orders to the `deliveryAddress` where it's present. Job done, and everyone can go home early, right?

Alas no! Unless the `OpenOrder` schema is updated to embed the latest `User` data, orders will continue to be delivered to the `residentialAddress`, because `OpenOrder` won't include the `deliveryAddress`.

A change that should have only involved a change to the `User` product and the delivery system now requires a change to the `OpenOrder` product too.

That's unnecessary and avoidable coupling!

Note, adding the `deliveryAddress` field would make the `Address` schema a shared schema, i.e. used in multiple places.
However, using your own schema in this way is perfectly fine, as you control its evolution.

Defining your own shared schema for use _within_ your own product is perfectly fine, as you control its evolution.
{: .notice--info}

## A good approach

We've seen what not to do, so what should we do?

### Don't embed, reference

As a general rule, don't embed another product's schema, or data, into your own: reference it instead.

Let's rejig the `OpenOrder` schema to include a _reference_ to the `User` data, i.e. just a `userId`, rather than _denormalising_ it:

```json
{
  "type": "record",
  "name": "OpenOrder",
  "namespace": "acme.orders",
  "fields": [
    {
      "name": "orderId",
      "type": "long"
    },
    {
      "name": "userId",
      "type": "long"
    },
    {
      "name": "items",
      "type": {
        "type": "array",
        "items": "OrderItem"
      }
    }
  ]
}
```

An order placed by Emily might now look like:

```json
{
    "orderId": 123456789012,
    "userId": 257363658353,
    "items": [
        {
          "productId": 123456789012,
          "quantity": 1
        },
        {
          "productId": 234567890123,
          "quantity": 2
        }
    ]
}
```

That's a much smaller payload! Downstream teams will thank you for it.
Downstream teams that need user data can join the order data to the user data, enriching it with just the `User` fields they require.

This leaves the `Users` data product as the source of truth for the user data, as it should be.

Of course, there are times when data needs to be denormalised for performance reasons.
That's fine, but the scope of that data should be kept as small as possible, ideally as an implementation detail within a single team or system.
When sharing across an architectural or organisational boundary, such denormalisation should be avoided.

### Just the right amount of coupling

How much coupling is the right amount of coupling? Well, you might say it's simple: a data product should:
 1. _never_ use schemas defined by another data product, and
 2. _never_ include denormalised data from another data product

While this is a good general position to start from, there are a few scenarios where it makes sense to break one or both of these rules. So let's tone it down a bit:

As a good general position to start from, try to avoid referencing another product's schemas or denormalising another product's data in your own product. Where you do, consider the implications for you and consuming teams.
{: .notice--info}

With some rough rules in place, let's see about breaking them...

#### Let's talk about keys.

In the examples above, the `orderId` and `userId` fields are used to uniquely identify an order or user, respectively.

In database parlance, these identifiers would be primary _keys_.

The type of the keys in the above example are a simple `long`s, but that's not always the case. 
Sometimes the keys are composites, i.e. made up of multiple fields, often other keys. 

For example, an `OrderItem` might be uniquely identified by a combination of `orderId` and `productId`:

```json
{
  "type": "record",
  "name": "OrderItem",
  "namespace": "acme.orders",
  "fields": [
    {
      "name": "orderId",
      "type": "long"
    },
    {
      "name": "productId",
      "type": "long"
    },
    {
      "name": "quantity",
      "type": "int"
    }
  ]
}
```

The thing about keys is that the set of fields they contain almost never change. 
Altering the set of fields is not an evolvable change, doing so would break systems that interact with the data.
In a well-engineered system, such a requirement would require a new data set to be curated with a _new_ key schema, dual-published for some time, while systems were migrated.

As a key never changes, it's the perfect candidate for being shared. Doing so can actually _improve_ the readability, type-safety, and traceability of the data.

##### Simple keys

While it's perfectly fine to leave simple ids like `userId` or `orderId` as simple `long`s, you may choose to create a custom type, for example this Avro schema:

```json
{
  "type": "record",
  "name": "UserId",
  "namespace": "acme.users",
  "fields": [
    {
      "name": "id",
      "type": "long"
    }
  ]
}
```

This use of an Avro record to represent the `id` of a `User` comes with a, albeit small, serialization cost!
{: .notice--warning}

If we also define an `OrderId` schema as:

```json
{
  "type": "record",
  "name": "OrderId",
  "namespace": "acme.orders",
  "fields": [
    {
      "name": "id",
      "type": "long"
    }
  ]
}
```

Then we can update the `OpenOrder` schema to use these new key types:

```json
{
  "type": "record",
  "name": "OpenOrder",
  "namespace": "acme.orders",
  "fields": [
    {
      "name": "orderId",
      "type": "OrderId"
    },
    {
      "name": "userId",
      "type": "acme.users.UserId"
    },
    {
      "name": "items",
      "type": {
        "type": "array",
        "items": "OrderItem"
      }
    }
  ]
}
```

Notice the `userId` field is of type `acme.users.UserId`. This is referencing a type from another data product. Gasp!
However, it's OK, as the referenced type is a key and hence its schema won't change.

Now, some readers may think wrapping primitives in a type is overkill.

They may well be right, though the types do make it easier to understand where the key is coming from and allow a simple `id` field name to be unambiguous,
and will provide a level of type-safety when working with the data in some languages.

Make your own judgment on the wrapped primitives. 

##### Compound keys

Compound keys are a perfect candidate for being shared, as they make schema and code more readable than having multiple key fields everywhere.

For example, let's define an `OrderItemId` schema as a combination of `orderId` and `productId`:

```json
{
  "type": "record",
  "name": "OrderItemId",
  "namespace": "acme.orders",
  "fields": [
    {
      "name": "orderId",
      "type": "OrderId"
    },
    {
      "name": "productId",
      "type": "acme.products.productId"
    }
  ]
}
```

Notice how the `productId` field of this key schema is referencing a key type from another data product.
As this is a stable key schema, its fine to share it.

With `OrderItemId` in place, an `OrderItem` schema can be defined as:

```json
{
  "type": "record",
  "name": "OrderItem",
  "namespace": "acme.orders",
  "fields": [
    {
      "name": "id",
      "type": "OrderItemId"
    },
    {
      "name": "quantity",
      "type": "int"
    }
  ]
}
```

#### Value types

Similar to keys, there is an argument for allowing simple 'value' types to be shared. 

Maybe it makes sense in your company to have a `Currency` type that wraps an ISO currency code, or `Country` type that similarly wraps a ISO country code.
Both examples of a value type that wraps a single primitive field. 

Maybe it would be nice to have a `Money` type, which combines a `currency` and `amount` field, or a `Date` type that wraps `year`, `month` and `day` fields.

Such shared schema can make life easier: easier to perform joins on a common type, easier transforming input data to create new products, etc.

However, it's important to note that such types work great _until_ their schema needs to change.
Then you have a challenge, potentially involving getting everyone to update at the same time and a coordinated system-wide release.
There's that increased coupling again!

Common simple types can add lots of benefits, but only if their schema are stable.
{: .notice--info}

Consider the implications if your company had a shared `Currency` enumeration and you needed to add a new value:
I'm not aware of any schema implementations, Avro, Proto, JSON, etc, where adding a value to a enumeration is an evolvable change...

Enumerations make terrible shared types, because changing them is not an evolvable change.
{: .notice--warning}

So, if you're going to use shared schema like these, choose wisely which types to share.

## In conclusion...

Hopefully, this post has given you some ideas on how to use shared schemas in your data products without creating problems for yourself down the road.

Before signing off, let's have a reminder of the guidance from the beginning of this post and make it a little more data-product centric: 


| Schema you'd like to use in your product  | Recommendation           | Example                                                                                                                                                                                                           |
|-------------------------------------------|--------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Another product's identifier / key schema | ✅: not a problem         | A `UserId` or `ProductId`, etc. Think primary key columns in a DB, both single or multiple fields. OK to share as they don't evolve/change.                                                                       |
| Another product's object / value schema   | ❌: avoid like the plague | A full, rich `User` or `Product` object. The schema is highly likely to evolve/change, which causes issues                                                                                                        |
| Common value types with a stable schema   | ✅: but, tread carefully  | `Currency` containing an ISO-4217 code, `EmailAddress` wrapping a string, etc. Think simple, common types, not provided by the schema implementation by default. OK to share as long as they don't evolve/change. |
| The product's own schema                  | ✅: not a problem         | Anything you like, as you control its evolution.                                                                                                                                                                  |


Happy coding!
