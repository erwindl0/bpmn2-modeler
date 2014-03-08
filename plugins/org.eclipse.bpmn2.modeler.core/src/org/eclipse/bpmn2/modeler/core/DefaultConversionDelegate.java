/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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

package org.eclipse.bpmn2.modeler.core;

import org.eclipse.bpmn2.modeler.core.EditControlProvider.EditControl;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 *
 */
public class DefaultConversionDelegate implements ConversionDelegate, EditControlProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.EditControlProvider#createControl(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public EditControl createControl(Composite parent, int style) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate#convertToString(java.lang.Object)
	 */
	@Override
	public String convertToString(Object value) {
		if (value==null)
			return "";
		return value.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate#createFromString(java.lang.String)
	 */
	@Override
	public Object createFromString(String literal) {
		return null;
	}

	public static class TextControl extends EditControl {

		Text text;
		
		/**
		 * @param parent
		 * @param style
		 */
		public TextControl(Composite parent, int style) {
			super(parent, style);
	    	this.setLayout(new RowLayout());
			text = new Text(parent,style);
			text.addSelectionListener(this);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.EditControlProvider.EditControl#getValue()
		 */
		@Override
		public Object getValue() {
			return text.getText();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.EditControlProvider.EditControl#setValue(java.lang.Object)
		 */
		@Override
		public boolean setValue(Object value) {
			String s = value==null ? "" : value.toString();
			text.setText(s);
			return true;
		}
		
	}

}