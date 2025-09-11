---
title: "Shared schema: when to use and when _not_ to"
description: |
  Should multiple teams collaborate and use common shared schema?  
  Should the output of one team's services include types defined in another?
  These are important questions, and getting them wrong can lead to a hole heap of pain down the road.
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
In this post, we'll look at the cost and potential pitfalls and come up with some simple rules to avoid them.

## An example scenario

Let's say we have a team, doing the right thing and producing a `Users` _data product_, containing the attributes of the company's users.

A _data product_ is a curated set of data, that conforms to a known schema, which can be consumed by other teams.
The schemas of the product define a data-contract: an defined API, but for data, not code.
{: .notice--info}

This team, being good engineers, knowns it needs to ensure changes to the schemas are fully evolvable, so that changes don't break downstream consumers of the data.

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
      "type": "string"
    }
  ]
}
```

So, for example, a `User` may look like:

```json
{
  "userId": 257363658353,
  "fullName": "Miss Emily Stewart",
  "residentialAddress": "13 Main Street, London, UK"
}
```

A less experienced team is responsible for creating a `OpenOrders` data product, containing all the open orders a user has placed.

This team, being kind souls and knowing that many downstream teams will need to know the details of the users' who've placed the orders, 
_denormalises_ the user details into their data.

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
        "items": "acme.orders.OrderItem"
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
      "residentialAddress": "13 Main Street, London, UK"
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

The `OpenOrder` schema references the `User` schema, allowing an instance of an `OpenOrder` to include the full set of the `User` attributes
This removes the burden on downstream teams of having to join these two data sets together.

At first glance, this may look like a good approach: do the join in one place, and it's one many teams use when they first start adopting schemas and data products.

## The problem

The use of the `User` schema, owned and managed by the first team, in the `OpenOrder` schema, owned and managed by the second team, is a code smell (data smell? schema smell?)
But why? You may ask!

### Obese data

The `User` schemas in the example is tiny. A real world example would have much more information.
By including the `User` in the `OpenOrder`, the size of an `OpenOrder` can drastically increase.

Yes, _some_ downstream use cases may need _some_ of this extra information, but by including it _everyone_ needs to pay the price.

That's higher network, cpu and memory utilisation, potentially increased storage costs and certainly slower deserialization and higher latency for all: Yay!

### Stale data

What happens when the `User` data changes? Maybe Emily moves home, or gets married and changes her name. Now all of Emily's open orders are stale, containing incorrect information.

Either downstream teams are working with stale data, (and lets assume we all agree that's bad!), or the `OpenOrder` data needs republishing when the `User` data changes.

The republishing requires extra application complexity: rather than just _joining_ to the user data, it now needs to subscribe for changes too.

Republishing also increases the rate of change of the data, meaning _more_ data needs moving around.

That's higher network, cpu and memory utilisation, potentially increased storage costs and certainly slower deserialization and higher latency for all: Yay!

### Stale schema

In a well-engineered system, with correct use of shared schema, a team consuming a data product need not worry about keeping up with the latest version of the product's schema.
The only time they need to update their dependencies is when _there is something they need_ in a later version.

It's perfectly fine for a consuming team to use an old schema, for as long as they like. 
(Full schema compatability ensures all the data is compatible with their version of the schema.)

In this idyllic utopia, the schema and the data-contract they represent _decouple_ data producers and consumers.

When the `User` schema was embedded into the `OpenOrder` schema, it increased coupling between the two teams and products.
The `OpenOrder` schema references a specific version of the `User` schema. 
When the `User` schema changes, it needs explicitly updating in the `OpenOrder` schema, otherwise the user data embedded in the `OpenOrder` can be incomplete.

To demonstrate this, imagine a downstream consumer of the `OpenOrder` data responsible for delivering orders to customers.
Consider what happens when a new optional `deliveryAddress` field is added to the `User` schema. 
The delivery system is updated to the latest `User` schema and enhanced to route orders to the `deliveryAddress`, where its present.
Job done, and everyone can go home early, right?
Alas no, unless the `OpenOrder` schema is updated to embed the latest `User` data, orders will continue to be delivered to the `residentialAddress`,
because `OpenOrder` won't include the `deliveryAddress`.

A change which should have only involved a change to the `User` product and the delivery system now requires a change to the `OpenOrder` product too.

That's unnecessary and avoidable coupling!

## Don't embed, reference

We've seen what not to do, so what should we do?

Let's rejig the `OpenOrder` schema to include a _reference_ to the `User` data, i.e. just the `userId`, rather than _denormalising_ it:

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
        "items": "acme.orders.OrderItem"
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
Downstream team that need user data can join the order data to the user data, enriching it with just the `User` fields they require.

This leaves the `Users` data product as the source of truth for the user data, as it should be.

## Just the right amount of coupling

How much coupling is the right amount of coupling? Well, you might say, it's simple: a data product should:
 1. _never_ use schemas defined by another data product, and
 2. _never_ include denormalised data from another data product

While this is a good general position to start from, there are a few scenarios where it makes sense to break one or both of these rules.
So lets tone it down a bit:

As a good general position to start from, try to avoid referencing another product's schemas or denormalising another product's data in your own product.
Where you do, consider the implications for you and consuming teams.
{: .notice--info}

With some rough rules in place, let's see about breaking them...

### Let's talk about keys.

In the examples above, the `orderId` and `userId` fields are used to uniquely identify an order or user, respectively.
In database parlance, these are called _keys_.  The type of these keys in this example is a simple `long`, but that's not always the case.
Sometimes the keys are composites, made up of multiple other keys. 
For example, the unique identifier for an order might be just the `orderId`, or the combination of the `orderId` and `userId`.

The type of the key for a set of data almost never changes, because doing so would likely break all systems that interact with the data.
Certainly, in a well-engineered system, such as change would be avoided as it wouldn't be an evolvable change.
(Such a change in key would require a new data set, dual published for some time, while systems were migrated).
Hence, we can say that the type, a.k.a. schema, of a key won't evolve.

As a key never changes, it's the perfect candidate for being shared. Doing so can _improve_ the readability, type-safety and traceability of the data.

For examples sake, let's use `userId` as the key for `Users` and a composite key of `userId` and `orderId` for `OpenOrders`.

While its perfectly fine to leave `userId` as a simple `long`, you may choose to create a custom type, for example this Avro schema:

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

The `OpenOrder` key schema might look like:

```json
{
  "type": "record",
  "name": "OrderId",
  "namespace": "acme.orders",
  "fields": [
    {
      "name": "orderId",
      "type": "long"
    },
    {
      "name": "userId",
      "type": "acme.users.UserId"
    }
  ]
}
```

Notice, the `userId` field is of type `acme.users.UserId`. This is referencing a type from another data product. Gasp!
However, it's OK, as the referenced type is a key and hence its schema won't change.

`User` and `OpenOrder` schemas updated to use these new key types would look like:


```json
{
  "type": "record",
  "name": "User",
  "namespace": "acme.users",
  "fields": [
    {
      "name": "id",
      "type": "acme.users.UserId"
    },
    {
      "name": "fullName",
      "type": "string"
    },
    {
      "name": "residentialAddress",
      "type": "string"
    }
  ]
}
```

```json
{
  "type": "record",
  "name": "OpenOrder",
  "namespace": "acme.orders",
  "fields": [
    {
      "name": "id",
      "type": "acme.orders.OrderId"
    },
    {
      "name": "items",
      "type": {
        "type": "array",
        "items": "acme.orders.OrderItem"
      }
    }
  ]
}
```

Now, some readers may think wrapping primitives in a type is overkill. 

They may well be right, though the types do make it easier to understand where the key is coming from and allow a simple `id` field name to be unambiguous,
and will provide a level of type-safety when working with the data in some languages. 

Make your own judgement on the wrapped primitives. Hopefully, you'll agree the `OrderId` type makes more sense. 

### Value types

Similar to keys, there is an argument for allowing simple 'value' types to be shared. Consider a `Currency` enumeration, or a `Date` type. Simple types that wrap a single primitive or two.

Such shared schema can make it easier to work with the data, performing joins on common types, transforming input data to create new products, etc.

However, it's important to note that such types work great _until_ their schema needs to change.  Then you have a challenge, potentially involving getting everyone to update at the same time.

Consider, adding a new currency to the `Currency` enumeration would not be an evolvable change for most schema implementations...

So, if you're going to use shared schema like these, choose wisely which types to share.

## In conclusion...

Hopefully, this post has given you some ideas on how to use shared schemas in your data products without creating problems for yourself down the road.

If you're looking for a general rule on when to share or not, then how about:

1. Sharing and embedding key schemas : not a problem
2. Sharing and embedding value type schemas : tread carefully
3. Sharing and embedding value schemas : avoid like the plague!

Happy coding!










