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

package org.eclipse.bpmn2.modeler.runtime.jboss.jbpm5;

import org.eclipse.bpmn2.modeler.core.adapters.IExtensionValueAdapter;
import org.eclipse.bpmn2.modeler.runtime.jboss.jbpm5.model.drools.MetaDataType;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class MetaDataTypeAdapter extends AdapterImpl implements IExtensionValueAdapter {
	
	public static MetaDataTypeAdapter adapt(EObject object) {
		if (appliesTo(object)) {
			for (Adapter a : ((EObject)object).eAdapters()) {
				if (a instanceof MetaDataTypeAdapter) {
					return (MetaDataTypeAdapter)a;
				}
			}
			MetaDataTypeAdapter a = new MetaDataTypeAdapter();
			object.eAdapters().add(a);
			return a;
		}
		return null;
	}
	
	public static boolean appliesTo(EObject object) {
		return object instanceof MetaDataType;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.IExtensionValueAdapter#shouldSaveElement(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean shouldSaveElement(EObject o) {
		if (((MetaDataType)o).getName()==null || ((MetaDataType)o).getName().isEmpty())
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.IExtensionValueAdapter#shouldSaveFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public boolean shouldSaveFeature(EObject o, EStructuralFeature f) {
		return true;
	}
}