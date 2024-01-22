<!--
SPDX-FileCopyrightText: 2023 Deutsche Telekom AG

SPDX-License-Identifier: CC0-1.0    
-->

# JSON filter <!-- omit in toc -->

- [Available operators](#available-operators)
- [Usage](#usage)
  - [Instantiating a operator-chain from a JSON- or YAML-file](#instantiating-a-operator-chain-from-a-json--or-yaml-file)
  - [Instantiating a operator-chain manually](#instantiating-a-operator-chain-manually)
- [Evaluating JSON-string with operator-chains](#evaluating-json-string-with-operator-chains)
- [Features](#features)
  - [Complexity-limiting](#complexity-limiting)
- [Creating new operators](#creating-new-operators)

## Available operators

| Name             | Operator | Type       | Description                                                        | Implemented |
|------------------|----------|------------|--------------------------------------------------------------------|-------------|
| equal            | `eq`     | comparison | True if `field` == `value`.                                        | ✅           |
| not equal        | `ne`     | comparison | True if `field` != `value`.                                        | ✅           |
| regex            | `rx`     | comparison | True if `field` matches regex defined in `value`.                  | ✅           |
| less than        | `lt`     | comparison | True if `field` < `value`.                                         | ✅           |
| less or equal    | `le`     | comparison | True if `field` <= `value`.                                        | ✅           |
| greater than     | `gt`     | comparison | True if `field` >  `value`.                                        | ✅           |
| greater or equal | `ge`     | comparison | True if `field` >= `value`.                                        | ✅           |
| in               | `in`     | comparison | True if `field` is in `value`.                                     | ✅           |
| contains         | `ct`     | comparison | True if `field` contains `value`.                                  | ✅           |
| nct              | `nct`    | comparison | True if `field` does not contain `value` (i.e., blacklist filter). | ✅           |
| or               | `or`     | logic      | True if **at least one** of the child-operators returns true       | ✅           |
| and              | `and`    | logic      | True if **all** of the child-operators return true                 | ✅           |

## Usage

JSON-filters consist of operators that are chained together to be used in advanced filter-scenarios.
A operator-chain always has a root-operator from which the evaluation is started.
A root-operator can be instantiated from a JSON- or YAML-file or manually.

### Instantiating a operator-chain from a JSON- or YAML-file

In order to convert JSON- or YAML-files to operator-chains one needs o add the `OperatorDeserializer` to the Jackson
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
  private Operator advancedSelectionFilter;
}
```

In the example above `DummyClass` can then be instantiated by calling `ObjectMapper.read(..., DummyClass.class)`.

During the `ObjectMapper.read()` the operator-chain is also validated.
If the operator-chain specified in the JSON- or YAML-file is not valid an `OperatorParsingException` is thrown.

An example of an operator-chain in YAML-format can be found below:

```yaml
advancedSelectionFilter:
  and:
  - eq:
      field: $.processing.state
      value: IN_PROGRESS
  - or:
    - ge:
        field: $.total.amount
        value: 100000
    - eq:
        field: $.notify.policy
        value: ALWAYS
```

### Instantiating a operator-chain manually

In order to instantiate a operator-chain manually one has to differentiate between logical- and comparison-operators.
Comparison operators that take a JSON-path and an expected value can be instantiated by
calling `ComparisonOperator.instantiate(ComparisonOperatorEnum operator, String jsonPath, T expectedValue)`.
Logical operators that take a list of operators can be instantiated by
calling `LogicOperator.instantiate(LogicOperatorEnum operator, List<Operator> operatorList)`.

## Evaluating JSON-string with operator-chains

In order to evaluate a JSON-string with a certrain `Operator` one only needs to call `Operator.evaluate(String json)`
and pass the JSON as an string.
This returns `true` when the JSON is valid and `false` when the JSON is not.

## Features

### Complexity-limiting

In order to to minimize the attack-vector on any system evaluating or validating an operator-chain the complexity of any
operator-chain is limited per default to 42 operators.
The default complexity can be overwritten in the deserializer-constructor.

In any case when the operator-chain is too complex, an `OperatorParsingException` will be thrown.

## Creating new operators

1) In order to create a new operator just create a Class in `de.telekom.eni.jfilter.operator.comparison`
   or `de.telekom.eni.jfilter.operator.logic`
   that extends `ComparisonOperator<T>` (where `T` is the type of the expected value) or `LogicOperator`.
2) Implement `validate()` and `evaluate(String json)` specific to the operator.
3) Add the operator-abbreviation in `ComparisonOperatorEnum` or `LogicOperatorEnum`.
4) Adapt the `instantiate()`-function in `ComparisonOperator` or `LogicOperator` to also instantiate your new operator.
