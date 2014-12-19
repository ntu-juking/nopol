package fr.inria.lille.repair.symbolic.spoon;

import gov.nasa.jpf.symbc.Debug;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

public class ConditionalAdder extends SymbolicProcessor {

	public ConditionalAdder(CtStatement target) {
		super(target);
		super.defaultValue = "true";
	}
	
	public void process(CtStatement element) {
		logger.debug("##### {} ##### Before:\n{}", element, element.getParent());
		CtElement parent = element.getParent();
		CtIf newIf = element.getFactory().Core().createIf();
		CtCodeSnippetExpression<Boolean> condition = null;
		if (getValue() != null) {
			if(getValue().equals("1")) {
				condition = element.getFactory().Code()
					.createCodeSnippetExpression("true");
			} else if(getValue().equals("0")) {
				condition = element.getFactory().Code()
						.createCodeSnippetExpression("false");
			} else {
				condition = element.getFactory().Code()
						.createCodeSnippetExpression(getValue());
			}
		} else {
			condition = element
					.getFactory()
					.Code()
					.createCodeSnippetExpression(
							Debug.class.getCanonicalName()
									+ ".makeSymbolicBoolean(\"guess_fix\")");
		}
		newIf.setCondition(condition);
		// Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2] != [CtElem3] )
		newIf.setParent(parent);
		element.replace(newIf);
		// this should be after the replace to avoid an StackOverflowException caused by the circular reference.
		// see SpoonStatementPredicate
		newIf.setThenStatement((CtStatement) element);
		// Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2] != [CtElem3] )
		newIf.getThenStatement().setParent(newIf);
		logger.debug("##### {} ##### After:\n{}", element, element.getParent().getParent());
	}
}