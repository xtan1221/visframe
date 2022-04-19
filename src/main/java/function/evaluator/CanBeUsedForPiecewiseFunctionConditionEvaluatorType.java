package function.evaluator;

import function.component.PiecewiseFunction;
import function.variable.output.OutputVariable;
import function.variable.output.type.PFConditionEvaluatorBooleanOutputVariable;
import function.variable.output.type.ValueTableColumnOutputVariable;

/**
 * marker interface for {@link Evaluator} types with possibly single output variable of boolean type thus can be used as conditional evaluator of {@link PiecewiseFunction}
 * 
 * as a result, the output variable of such Evaluators must be of type {@link OutputVariable} rather than {@link ValueTableColumnOutputVariable}
 * to include {@link PFConditionEvaluatorBooleanOutputVariable} type,
 * while for those not marked by this interface, their output variables should be of {@link ValueTableColumnOutputVariable} type;
 * 
 * 
 * @author tanxu
 *
 */
public interface CanBeUsedForPiecewiseFunctionConditionEvaluatorType {

}
