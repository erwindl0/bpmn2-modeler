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

package org.eclipse.bpmn2.modeler.runtime.jboss.jbpm5.property;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextObjectEditor;
import org.eclipse.bpmn2.modeler.runtime.jboss.jbpm5.MetaDataTypeAdapter;
import org.eclipse.bpmn2.modeler.runtime.jboss.jbpm5.model.drools.DroolsFactory;
import org.eclipse.bpmn2.modeler.runtime.jboss.jbpm5.model.drools.DroolsPackage;
import org.eclipse.bpmn2.modeler.runtime.jboss.jbpm5.model.drools.ExternalProcess;
import org.eclipse.bpmn2.modeler.runtime.jboss.jbpm5.model.drools.MetaDataType;
import org.eclipse.bpmn2.modeler.runtime.jboss.jbpm5.model.drools.MetaValueType;
import org.eclipse.bpmn2.modeler.ui.property.DescriptionPropertySection;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * This is an empty tab section which simply exists to hide the "Basic" tab
 * defined the editor UI plugin.
 * 
 * @author Bob Brodt
 *
 */
public class JbpmDescriptionPropertySection extends DescriptionPropertySection {

	MetaDataType metaData = null;
	MetaValueType metaValue = null;

	@Override
	protected AbstractDetailComposite createSectionRoot() {
		return new JbpmDescriptionPropertyComposite(this);
	}

	@Override
	public AbstractDetailComposite createSectionRoot(Composite parent, int style) {
		return new JbpmDescriptionPropertyComposite(parent,style);
	}
	
	public JbpmDescriptionPropertySection() {
		super();
	}

	public class JbpmDescriptionPropertyComposite extends DescriptionDetailComposite {
		
		public JbpmDescriptionPropertyComposite(
				AbstractBpmn2PropertySection section) {
			super(section);
		}

		public JbpmDescriptionPropertyComposite(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		public void createBindings(final EObject be) {
			// for BPMNDiagram objects, pick out the Process and render the Process attributes
			Process process = null;
			if (be instanceof Participant) {
				process = ((Participant) be).getProcessRef();
			} else if (be instanceof BPMNDiagram) {
				BaseElement bpmnElement = ((BPMNDiagram)be).getPlane().getBpmnElement();
				if (bpmnElement instanceof Process)
					process = (Process)bpmnElement;
			}
			else if (be instanceof ExternalProcess) {
				// TODO: hook into jBPM guvnor instance to fetch additional info maybe?
				bindAttribute(be, "name"); //$NON-NLS-1$
				return;
			}
			
			if (process==null) {
				// display the default Description tab
				super.createBindings(be);
			}
			else {
				// create our own for Process
				bindDescription(be);
				
				bindAttribute(process, "name"); //$NON-NLS-1$
//				bindAttribute(process, "id");
//				bindAttribute(process, "anyAttribute");
//				bindAttribute(process, "isExecutable");
				bindList(process,"documentation"); //$NON-NLS-1$
//				bindAttribute(process, "adHoc");
//				bindList(process, "properties"); // this has moved to JbpmDataItemsDetailComposite
//				bindList(process, "laneSets"); // don't need this
			}
			if (be instanceof BaseElement) {
				bindMetaData((BaseElement) be);
			}
		}

		@Override
		protected void bindAppearance(EObject be) {
			// TODO: support the color/appearance extensions defined by jBPM Web Designer
		}

		protected void bindMetaData(final BaseElement be) {
			if (isModelObjectEnabled(DroolsPackage.eINSTANCE.getMetaDataType())) {
				Composite section = createSectionComposite(this, Messages.JbpmDescriptionPropertySection_MetaData_Section_Title);
				metaData = null;
				metaValue = null;
				for (ExtensionAttributeValue eav : be.getExtensionValues()) {
					for (Entry entry : eav.getValue()) {
						if (entry.getValue() instanceof MetaDataType) {
							metaData = (MetaDataType) entry.getValue();
							metaValue = metaData.getMetaValue();
						}
					}
				}
				if (metaData==null) {
					Button button = toolkit.createButton(section, Messages.JbpmDescriptionPropertySection_Add_MetaData_Button, SWT.PUSH);
					toolkit.createLabel(section, ""); //$NON-NLS-1$
					toolkit.createLabel(section, ""); //$NON-NLS-1$
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
							domain.getCommandStack().execute(new RecordingCommand(domain) {
								@Override
								protected void doExecute() {
									metaValue = DroolsFactory.eINSTANCE.createMetaValueType();
									metaValue.setValue(""); //$NON-NLS-1$
									metaData = DroolsFactory.eINSTANCE.createMetaDataType();
									MetaDataTypeAdapter.adapt(metaData);
									metaData.setMetaValue(metaValue);
									ExtensionAttributeValue eav = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
									eav.getValue().add(DroolsPackage.eINSTANCE.getDocumentRoot_MetaData(), metaData);
									be.getExtensionValues().add(eav);
									setBusinessObject(be);
								}
							});
						}
					});
				}
				else {
					TextObjectEditor nameEditor = new TextObjectEditor(this, metaData, DroolsPackage.eINSTANCE.getMetaDataType_Name());
					TextObjectEditor valueEditor = new TextObjectEditor(this, metaValue, DroolsPackage.eINSTANCE.getMetaValueType_Value());
					nameEditor.createControl(section, Messages.JbpmDescriptionPropertySection_MetaData_Name);
					valueEditor.createControl(section, Messages.JbpmDescriptionPropertySection_MetaData_Value);

					toolkit.createLabel(section, ""); //$NON-NLS-1$
					Button button = toolkit.createButton(section, Messages.JbpmDescriptionPropertySection_Remove_MetaData_Button, SWT.PUSH);
					toolkit.createLabel(section, ""); //$NON-NLS-1$
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
							domain.getCommandStack().execute(new RecordingCommand(domain) {
								@Override
								protected void doExecute() {
									for (ExtensionAttributeValue eav : be.getExtensionValues()) {
										if (eav.getValue().size()>0 && eav.getValue().getValue(0)==metaData) {
											be.getExtensionValues().remove(eav);
											break;
										}
									}
									metaValue = null;
									metaData = null;
									setBusinessObject(be);
								}
							});
						}
					});
				}
			}
		}
	}
}
