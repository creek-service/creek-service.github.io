---
title: Evolving JSON Schemas - Part I
description: |
  The default JSON schema evolution rules provided by Confluent's Schema Registry make evolving JSON schemas clunky at best.
  In this two part series, we look at why, and if there is a better way. This first part covers the 'why'.
header:
  image: /assets/images/json.png
categories:
  - articles
tags:
  - kafka
  - json
  - json-schema
  - serde
toc: true
---

Confluent's Schema Registry's rules for evolving JSON schemas are so limiting as to be basically unusable.
In this two-part series we'll look at why its unusable and then, in the [second part]({% post_url 2024-01-09-json-schema-evolution-part-2 %}),
how we can leverage Confluent's JSON schema registry extension to build a more useful evolution model.

## A brief history of evolution

No, not the darwinian sort of evolution. Here, we're talking about schema evolution and JSON schema evolution in particular.

Recommended reading before reading this article would be our article on [JSON Schema Validators]({% post_url 2023-11-14-json-validator-comparison %}),
which gives some good background on Schemas in general, 
Robert Yokota's article on [Understanding JSON Schema Compatability][Yokata_article],
which goes in-depth into the specifics of how JSON schema compatability works, and maybe Confluent's own documentation on
[JSON Schema compatibility rules](https://docs.confluent.io/platform/current/schema-registry/fundamentals/serdes-develop/serdes-json.html#json-schema-compatibility-rules).

If that seems like a lot of reading, or if you've previously read these and just need a refresher, then the gist of all 
of the above can be boiled down to the following:

* _Backward compatibility_ means that **readers** with a **newer** schema can correctly parse data written using an older schema,
i.e. **new schemas can read old data**. 

* _Forwards compatibility_ means that **readers** with an **older** schema can correctly parse data written using a newer schema.
i.e. **old schemas can read new data**.

* _Full compatibility_ means both being _forward_ and _backwards_ compatible. 

* Confluent's Schema Registry differentiates between a schema being _forwards_ or _backwards_ compatible with its neighbours,
  or _transitively_ compatible with all schema versions that come before it or after it.
  (The rest of this article will discuss _transitively_ compatible schema changes).

* We recommend that all the schemas used to describe data in a Kafka topic should be _fully compatible_, or `FULL_TRANSITIVE` in Schema Registry terminology,
  as data in Kafka topics can be around for a long time.
  * Transitive _Backwards compatible_ allows Consumers to read data produced with an older schema, either because they were updated before the producing app(s),
  or because they were lagging during deployment, or because they need to be able to rewind and reprocess old data in the topic, etc.
  * Transitive _Forwards compatible_ allows Consumers to read data produced with a newer schema, either because they were updated after the producing app(s),
  or because you want the ability to roll back a bad deployment, which can leave data in topics produced by newer schemas, etc.

## Is Confluent's JSON Schema evolution fit for purpose?

Looking at the posts on StackOverflow and GitHub it seems there is some confusion. 
There's lots of talk about not being able to evolve schemas in a meaningful way, especially if your aim is _full_ compatability.
People are running into `PROPERTY_ADDED_TO_OPEN_CONTENT_MODEL` and `PROPERTY_REMOVED_FROM_CLOSED_CONTENT_MODEL` errors even 
when performing changes they expect to be compatible.

While we're likely all familiar and comfortable with the standard schema evolution rules for required properties
seen with other schema types, e.g. 
* Not being able to remove _required_ properties in a _forwards_ compatible way: 
  remember, that's old schemas reading new data. 
  Old schemas that still require the property can't read new data that may not contain the property.
* Not being able to add _required_ properties in a _backwards_ compatible way: 
  remember, that's new schema reading old data. 
  New schema requiring a new property can't read old data that may not contain it.
* Combining the previous two means with _Full_ compatability _required_ properties can neither be added nor removed.

We also intuitively expect adding and removing optional properties to be _fully_ compatible.
After all, they're optional, right? Optional properties can be added and removed in any other schema type I can think of.

Unfortunately, this is not how the Confluent has implemented it's JSONs schemas compatability checks in the Schema Registry.
{: .notice--warning}

It's this inability to be able to add and remove optional properties, when looking for _full_ compatability, 
that's causing people so much confusion. So lets look into what the schema registry is doing and why that results
in this unintuitive functionality.

The diagram below shows how the schema registry performs compatibility checks when a new schema version `v4` 
is being added.

{% include figure image_path="/assets/images/json-schema-evolution-confluent.svg" alt="Confluent's JSON schema evolution" %}

- `FORWARD_TRANSITIVE` checks each existing schema can read data produced by the new schema. 
- `BACKWARDS_TRANSITIVE` checks the new schema can read data produced by each old schema.
- `FULL_TRANSITIVE` compatibility performs both checks.

While this pattern seems sensible and matches that used with other schema types, 
this pattern causes a problem with JSON Schema, due to how JSON Schema compatibility works, and specifically due to what JSON schema calls _content models_.
[Yokota's article][Yokata_article] goes into some detail on JSON Schema compatability and content models. 

Let's look at each content model and its suitability to the above pattern of compatibility checks.

### Evolving closed content model

A closed content model, i.e. one with `additionalProperties` set to `false` and no `patternProperties`, 
means the data can only contain the properties defined in the Schema; no additional properties are allowed.

```json
{
  "type": "object",
  "properties": {
    "foo": { "type": "integer" },
    "bar": { "type": "string" }
  },
  "additionalProperties": false
}
```

If we evolve a closed schema by adding a new optional property, then new data could have this new field. 
The old schema would reject this, breaking forwards compatability. 
However, the new schema can read all the old data, so the change is backwards compatible.

If we evolve a closed schema by removing an existing optional property, then old data could still have this property.
The new schema would reject this, breaking backwards compatability.
However, the old schema can read any new data, so the change is forwards compatible.

Adding & removing required properties always breaks forwards and backwards compatibility for closed models.

With this model its also forward compatible to change an optional property to required, 
and backwards compatible to change a required property to optional.

So, for a closed content model the following table summarizes valid changes:

|                      | Forward Compatible<br>Old schema / new data | Backwards Compatible<br>New schema / old data | Fully Compatible |
|----------------------|---------------------------------------------|-----------------------------------------------|------------------|
| Add required         | :x:                                         | :x:                                           | :x:              |
| Add optional         | :x:                                         | :heavy_check_mark:                            | :x:              |
| Remove required      | :x:                                         | :x:                                           | :x:              |
| Remove optional      | :heavy_check_mark:                          | :x:                                           | :x:              |
| Optional -> required | :heavy_check_mark:                          | :x:                                           | :x:              |
| Required -> Optional | :x:                                         | :heavy_check_mark:                            | :x:              |

As you can see, the _full compatability_ column is all :x:'s, as an operation must have a :heavy_check_mark: in both the forward and backwards compatability columns to be fully compatible.
As the closed-content model doesn't allow _any_ operations under full compatability, we can say:

A closed content model is too restrictive and can not be used to evolve JSON schemas in the Confluent schema registry in a fully compatible way.
{: .notice--warning}

### Evolving open content model

An open content model, i.e. one with `additionalProperties` set to `true`, but still no `patternProperties`, 
means the data can contain the properties defined in the Schema, and any additional properties _of any type_.

```json
{
  "type": "object",
  "properties": {
    "foo": { "type": "integer" },
    "bar": { "type": "string" }
  },
  "additionalProperties": true
}
```

If we evolve an open schema by adding a new required or optional property, then, because an open model allows the data to contain additional properties,
it could be possible that there is existing data containing a property with the same name, but a different type, to the new property.
The new schema wouldn't be able to read such old data, breaking backwards compatibility.
However, old schemas can read new data, as they will ignore the new property, so long as the old schema does not itself contain a property with the same name and different type, so the change is forward compatible, 

If we evolve an open schema by removing an existing required or optional property, then the new data could contain a property with
the same name as the removed property, but with a different type. 
The old schemas wouldn't be able to read this new data, breaking forwards compatibility.
However, the new schemas can read the old data, so the change is backwards compatible. 

Like with closed content models, for open models it's also forward compatible to change an optional property to required,
and backwards compatible to change a required property to optional.

For an open content model the following table summarizes valid changes: 

|                      | Forward Compatible<br>Old schema / new data | Backwards Compatible<br>New schema / old data | Fully Compatible |
|----------------------|---------------------------------------------|-----------------------------------------------|------------------|
| Add required         | :heavy_check_mark:                          | :x:                                           | :x:              |
| Add optional         | :heavy_check_mark:                          | :x:                                           | :x:              |
| Remove required      | :x:                                         | :heavy_check_mark:                            | :x:              |
| Remove optional      | :x:                                         | :heavy_check_mark:                            | :x:              |
| Optional -> required | :heavy_check_mark:                          | :x:                                           | :x:              |
| Required -> Optional | :x:                                         | :heavy_check_mark:                            | :x:              |

More green ticks here than with the closed model. However, again, if we require _full_ compatibility, then there are no valid operations. Leading us to the conclusion:

An open content model is too open and can not be used to evolve JSON schemas in the Confluent schema registry in a fully compatible way.
{: .notice--warning}

### Evolving partially-open content models

If neither closed nor open contents models offer us a way to evolve JSON schemas, then that only leaves partially-open 
content models. A partially-open model either has a more complex schema for `additionalProperties`, or uses `patternProperties`, 
to restrict the schema of additional properties.

The following schema restricts additional properties to being of type `string`:

```json
{
  "type": "object",
  "properties": {
    "foo": { "type": "integer" },
    "bar": { "type": "string" }
  },
  "additionalProperties": { "type": "string" }
}
```

While this can allow optional fields of a matching type to be added and removed in a _fully_ compatible way, 
it restricts the type of those properties to a single schema type, making it impractical.

The following schema restricts additional properties to specific types based on the name of the property:

```json
{
  "type": "object",
  "properties": {
    "i_foo": { "type": "integer" },
    "s_bar": { "type": "string" }
  },
  "patternProperties": {
    "^i_": { "type": "integer" },
    "^s_": { "type": "string" }
  },
  "additionalProperties": false
}
```

Surely this they must allow full compatibility?

[Yokata's article][Yokata_article] goes into this in more detail and seems to be suggesting this is the way to building a chain of _fully_ compatible schema changes.

To our mind, this solution is just too clunky, restrictive and verbose. Not only would `patternProperties` need to include elements for each type supported by JSON Schema,
it would also need to restrict properties on any nested `object` properties and handle `array`s. Our best stab at such a schema would be:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Verbose and restrictive partially open content model",
  "$ref": "#/definitions/obj",
  "definitions": {
    "obj": {
      "type": "object",
      "additionalProperties": false,
      "patternProperties": {
       "^i_": { "type": "integer" },
       "^n_": { "type": "number" },
       "^s_": { "type": "string" },
       "^b_": { "type": "boolean" },
       "^o_": { "$ref": "#/definitions/obj"},
       "^ai_": { "type": "array", "items": {"type": "integer"} },
       "^an_": { "type": "array", "items": {"type": "number" } },
       "^as_": { "type": "array", "items": {"type": "string" } },
       "^ab_": { "type": "array", "items": {"type": "boolean" } },
       "^ao_": { "type": "array", "items": {"$ref": "#/definitions/obj"} }
      }
    }
  }
}
```

The above doesn't actually define any properties. This is just setting up the rules for mapping property names to types.
If you make a mistake in setting this up... you can't go back later and fix it, as that would break compatability.

Even if you can live with such a verbose schema, there are additional issues to consider:
 - the solution puts restrictions on the names of properties. This isn't going to work for projects where you're not in _full_ control of the names of properties.
 - the solution would not be able to take advantage of any new types added to the JSON Schema standard in the future, as they wouldn't have an appropriate mappings in `patternProperties`.
 - the solution probably falls foul of other edge cases. Such as changing to a `format`, etc.

Strictly speaking, we think it may be possible to produce a compatible timeline of schema changes using the partially-open content model, for use-cases where you control the names of properties.
But, it wouldn't be pretty and with all these issues combined, as far as we are concerned:

A partially-open content model is too unwieldy & restrictive to be used to evolve JSON schemas in the Confluent schema registry in a fully compatible way.
{: .notice--warning}

## Summary

Hopefully, this article has gone some way to explain why using strict JSON Schema compatability checks, with either 
closed, open or partially-open content models, doesn't result in a workable solution for evolving the JSON schemas
used to describe the data in your Kafka topics.

Unfortunately, as Confluent's current JSON Schema compatability checks in its Schema Registry, v7.3.1 at the time of writing, use these strict
rules, it makes it - in our honest opinion - unusable.

Primarily, its unusable as it only allows addition and removal of optional properties
through, verbose and restrictive, mapping of property name patterns to property type.

This is the key issue. Confluent's model requires the forward planning to add property mappings that map any name to a specific type.
This trick allows new properties to be added later without fear that they are clashing with existing data that uses the same property name, 
but with a different property type. 

In the [second part]({% post_url 2024-01-09-json-schema-evolution-part-2 %}) of this topic, we will look at how we can leverage
a mixed-mode approach to JSON Schema compatability checking that provides a much more user-friendly and clean solution.

[Yokata_article]:https://yokota.blog/2021/03/29/understanding-json-schema-compatibility/

