// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.telekom.jsonfilter.operator.logic.LogicOperator;
import de.telekom.jsonfilter.serde.OperatorDeserializer;
import de.telekom.jsonfilter.serde.OperatorSerializer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@JsonIgnoreProperties(value = {"om"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EvaluationResult {

    String operatorName;
    boolean match;
    String causeDescription;
    Operator operator;
    List<EvaluationResult> childOperators;
    static ObjectMapper om = initOm();

    private static ObjectMapper initOm() {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        SimpleModule m = new SimpleModule();
        m.addDeserializer(Operator.class, new OperatorDeserializer());
        m.addSerializer(Operator.class, new OperatorSerializer());
        om.registerModule(m);

        return om;
    }

    private EvaluationResult(boolean match, String operatorName, Operator operator, String causeDescription, List<EvaluationResult> childOperators) {
        this.match = match;
        this.operatorName = operatorName;
        this.operator = operator;
        this.causeDescription = causeDescription;
        this.childOperators = childOperators;
    }

    /**
     * Creates an EvaluationResult instance indicating no operation.
     *
     * @return EvaluationResult the created evaluation result, with default values and "noop" operator name.
     */
    public static EvaluationResult empty() {
        return new EvaluationResult(true, "noop", null, null, null);
    }

    /**
     * Creates a valid EvaluationResult instance based on the given Operator.
     *
     * @param op the operator for this evaluation.
     * @return EvaluationResult the created evaluation result, which is valid by default.
     */
    public static EvaluationResult valid(Operator op) {
        return new EvaluationResult(true, op.getOperator().getValue(), null, "", Collections.emptyList());
    }

    /**
     * Creates a valid EvaluationResult instance based on the given Operator and other evaluation results.
     *
     * @param op the operator for this evaluation.
     * @param evaluationResults the list of evaluation results for child operators.
     *
     * @return EvaluationResult the created evaluation result, which is valid by default.
     */
    public static EvaluationResult valid(Operator op, List<EvaluationResult> evaluationResults) {
        return new EvaluationResult(true, op.getOperator().getValue(), null, "", evaluationResults);
    }

    /**
     * Creates an EvaluationResult instance indicating an error in the evaluation process.
     *
     * @param rootCause the operator that caused the error.
     * @param causeDescription the description of the error.
     *
     * @return EvaluationResult the created evaluation result, which is not valid by default.
     */
    public static EvaluationResult withError(Operator rootCause, String causeDescription) {
        return new EvaluationResult(false, rootCause.getOperator().getValue(), rootCause, causeDescription, new ArrayList<>());
    }


    /**
     * Creates an EvaluationResult instance based on the results of an evaluation process.
     *
     * @param logicOperator the operator for this evaluation.
     * @param evaluationResults the list of evaluation results for child operators.
     *
     * @return EvaluationResult the created evaluation result, which is valid if all/any child evaluations are valid.
     */
    public static EvaluationResult fromResultList(LogicOperator logicOperator, List<EvaluationResult> evaluationResults) {
        switch (logicOperator.getOperator()) {
            case AND:
                if (evaluationResults.stream().allMatch(EvaluationResult::isMatch)) {
                    return EvaluationResult.valid(logicOperator, Collections.emptyList());
                } else {
                    return new EvaluationResult(false, logicOperator.getOperator().getValue(), null, "Not all child-operators matched.", evaluationResults);
                }
            case OR:
                if (evaluationResults.stream().anyMatch(EvaluationResult::isMatch)) {
                    return EvaluationResult.valid(logicOperator, Collections.emptyList());
                } else {
                    return new EvaluationResult(false, logicOperator.getOperator().getValue(), null, "No child-operator matched.", evaluationResults);
                }
        }
        return null;
    }

    @Override
    public String toString() {
        try {
            return om.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "invalid EvaluationResult";
        }
    }
}
