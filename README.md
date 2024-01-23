<!--
SPDX-FileCopyrightText: 2023 Deutsche Telekom AG

SPDX-License-Identifier: CC0-1.0    
-->

# JSON Filter <!-- omit in toc -->

JSON filter is a small, lightweight filter-library that allows to evaluate JSON payload against a filter consisting of
operators.
Operators can be defined programmatically or in JSON- or YAML-files.
The evaluation of JSON payloads against a filter results in an EvaluationResult, which indicates why a filter failed.

## Usage

There are two types of operators: comparison- and logical-operators.

### Comparison operators

Comparison-operators are used to compare a JSON path against an expected value.
Comparison-operators consist of three fields, `operator`, `field` and `value`.

`operator` is the operator to be used for the comparison ([available operators](#available-operators)).  
`field` is the JSON-path to be evaluated.  
`value` is the expected value.  
An example can be found below:

```json
{
  "<comparison operator>": {
    "field": "<JSON-path>",
    "value": "<expected value>"
  }
}
```

### Logic operators

Logical operators are used to combine multiple operators.
This can be used to create complex filter scenarios.
Logical operators also consist of the `operator`-field and a list of
operators ([available operators](#available-operators)).  
An example can be found below:

```json
{
  "<logic operator>": [
    {
      "<comparison operator>": {
        "field": "<JSON-path>",
        "value": "<expected value>"
      }
    },
    {
      "<comparison operator>": {
        "field": "<JSON-path>",
        "value": "<expected value>"
      }
    }
  ]
}
```

### Instantiate filters

A JSON filter always has a root-operator from which the evaluation is started.
A root-operator can be instantiated from a JSON- or YAML-file or manually.

#### Instantiating a root-operator from a JSON- or YAML-file

To convert fields from JSON- or YAML-files to operators one needs to add the `OperatorDeserializer` to the Jackson
`ObjectMapper` as follows:

```java
ObjectMapper om = new ObjectMapper(new YAMLFactory());

SimpleModule m = new SimpleModule();
m.

addDeserializer(Operator .class, new OperatorDeserializer());
        m.

addSerializer(Operator .class, new OperatorSerializer());

        om.

registerModule(m);
```

Once the `OperatorDeserializer` and the module is registered in the `ObjectMapper` one can use the `Operator` in any
POJO like so:

```java
package foo.bar;

import ...

public class DummyClass {
    @JsonProperty
    private Operator jsonFilter;
}
```

In the example above `DummyClass` can then be instantiated by calling `ObjectMapper.read(..., DummyClass.class)`.

During the `ObjectMapper.read()` the operator-chain is also validated.
If the operator-chain specified in the JSON- or YAML-file is not valid an `OperatorParsingException` is thrown.

An example of an operator-chain in YAML-format can be found below:

```json
{
  "jsonFilter": {
    "and": [
      {
        "eq": {
          "field": "$.processing.state",
          "value": "IN_PROGRESS"
        }
      },
      {
        "or": [
          {
            "ge": {
              "field": "$.total.amount",
              "value": 100000
            }
          },
          {
            "eq": {
              "field": "$.notify.policy",
              "value": "ALWAYS"
            }
          }
        ]
      }
    ]
  }
}
```

#### Instantiating a root-operator manually

To instantiate an operator-chain manually, one has to differentiate between logical- and comparison-operators.
Comparison operators that take a JSON path and an expected value can be instantiated by
calling `ComparisonOperator.instantiate(ComparisonOperatorEnum operator, String jsonPath, T expectedValue)`.  
Logical operators that take a list of operators can be instantiated by
calling `LogicOperator.instantiate(LogicOperatorEnum operator, List<Operator> operatorList)`.

### Evaluate JSON payload against a filter

To evaluate a JSON payload with a certain `Operator` one only needs to call `Operator.evaluate(String json)`
and pass the JSON as a string.

The result of the evaluation is an `EvaluationResult` that indicates if and why the evaluation was successful or not.

## Available operators

| Name             | Operator | Type       | Description                                               |
|------------------|----------|------------|-----------------------------------------------------------|
| or               | `or`     | logic      | Valid if **at least one** of the child-operators is valid |
| and              | `and`    | logic      | Valid if **all** of the child-operators are valid         |
|                  |          |            |                                                           |
| equal            | `eq`     | comparison | Valid if `field` == `value`.                              |
| not equal        | `ne`     | comparison | Valid if `field` != `value`.                              |
| regex            | `rx`     | comparison | Valid if `field` matches regex defined in `value`.        |
| less than        | `lt`     | comparison | Valid if `field` < `value`.                               |
| less or equal    | `le`     | comparison | Valid if `field` <= `value`.                              |
| greater than     | `gt`     | comparison | Valid if `field` >  `value`.                              |
| greater or equal | `ge`     | comparison | Valid if `field` >= `value`.                              |
| in               | `in`     | comparison | Valid if `field` is in `value`.                           |
| contains         | `ct`     | comparison | Valid if `field` contains `value`.                        |
| nct              | `nct`    | comparison | Valid if `field` does not contain `value`.                |