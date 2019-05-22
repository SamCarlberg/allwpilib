# wpilibSendable

This project contains schema files that define Sendable behavior.


## Schema

Schema files have the following structure:

```json
{
  "type": "<Sendable type>",
  "actuator": [true, false], // optional, defaults to false
  "hasSafeState": [true, false], // optional, defaults to false
  "hasUpdateTable": [true, false], // optional, defaults to false
  "properties": {
    "<Property name>": {
      "type": ["Number", "Text", "Boolean", "Number Array", "Text Array", "Boolean Array", "Raw", "Value"],
      "set": [true, false], // optional, defaults to true
      "metadata": [true, false] // optional, defaults to false
    }
  }
}
```

**Sendable `type`**

This is the string used to identify the sendable in a dashboard or over the network.

**Sendable `actuator`**

Flags the sendable as being an actuator. This is an optional property, and if omitted will default
to `false`.

**Sendable `hasSafeState`**

Flags the sendable as requiring a "safe state" function. Generated code will incorporate this.
This is an optional property, and if omitted will default to `false`.

**Sendable `hasUpdateTable`**

Flags the sendable as requiring a generate "update table" function. This is an optional property,
and if omitted will default to `false`.

**Property name**

Property names can only contain alphanumeric characters and spaces `/[a-zA-Z0-9 ]+/`

**Property `type`**

The type of data in the property.

**Property `set`**

Flags the property as having a "setter" function. This is an optional property, and if omitted will
default to `true`.

**Property `metadata`**

Flags the property as being metadata.
