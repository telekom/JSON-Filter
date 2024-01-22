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

    public static EvaluationResult empty() {
        return new EvaluationResult(true, "noop", null, null, null);
    }

    public static EvaluationResult valid(Operator op) {
        return new EvaluationResult(true, op.getOperator().getValue(), null, "", Collections.emptyList());
    }

    public static EvaluationResult valid(Operator op, List<EvaluationResult> evaluationResults) {
        return new EvaluationResult(true, op.getOperator().getValue(), null, "", evaluationResults);
    }

    public static EvaluationResult withError(Operator rootCause, String causeDescription) {
        return new EvaluationResult(false, rootCause.getOperator().getValue(), rootCause, causeDescription, new ArrayList<>());
    }

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
