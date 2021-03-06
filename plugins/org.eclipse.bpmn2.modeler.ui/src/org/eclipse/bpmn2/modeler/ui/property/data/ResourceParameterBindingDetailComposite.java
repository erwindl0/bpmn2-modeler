/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.ui.property.data;

import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ResourceParameterBinding;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.PropertiesCompositeFactory;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Bob Brodt
 *
 */
public class ResourceParameterBindingDetailComposite extends DefaultDetailComposite {

	private AbstractDetailComposite exprDetails;

	/**
	 * @param parent
	 * @param style
	 */
	public ResourceParameterBindingDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param section
	 */
	public ResourceParameterBindingDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}

	@Override
	protected void cleanBindings() {
		super.cleanBindings();
		exprDetails = null;
	}

	@Override
	public void createBindings(final EObject be) {
		
		if (be instanceof ResourceParameterBinding) {
			
			final ResourceParameterBinding rpb = (ResourceParameterBinding) be;
			TargetRuntime rt = TargetRuntime.getRuntime(be);

			bindReference(this, be, PACKAGE.getResourceParameterBinding_ParameterRef());
			
			// an MultipleAssignments is not really valid without both a From and To
			Expression expr = rpb.getExpression();
			if (expr==null) {
				expr = createModelObject(FormalExpression.class);
				InsertionAdapter.add(rpb, PACKAGE.getResourceParameterBinding_Expression(), expr);
			}
			
			if (exprDetails==null) {
				exprDetails = PropertiesCompositeFactory.INSTANCE.createDetailComposite(
						Expression.class, this, rt, SWT.NONE);
			}
			exprDetails.setBusinessObject(expr);
			exprDetails.setTitle(Messages.ResourceParameterBindingDetailComposite_Expression_Label);
		}
	}

}
