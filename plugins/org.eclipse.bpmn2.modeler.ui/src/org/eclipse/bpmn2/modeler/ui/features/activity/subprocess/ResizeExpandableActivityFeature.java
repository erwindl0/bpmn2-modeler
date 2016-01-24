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
package org.eclipse.bpmn2.modeler.ui.features.activity.subprocess;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.DefaultResizeBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class ResizeExpandableActivityFeature extends DefaultResizeBPMNShapeFeature {
	
	public ResizeExpandableActivityFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public void resizeShape(IResizeShapeContext context) {

		ResizeShapeContext resizeShapeContext = (ResizeShapeContext)context;

		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Activity activity = BusinessObjectUtil.getFirstElementOfType(containerShape, Activity.class);
		
		List<AnchorContainer> movedChildren = new ArrayList<AnchorContainer>();
		List<PictogramElement> containerChildren = FeatureSupport.getContainerChildren(containerShape);
		try {
			BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram(containerShape);
			BPMNShape bpmnShape = DIUtils.findBPMNShape(bpmnDiagram, activity);
			if (bpmnShape.isIsExpanded()) {

				// Activity is expanded
				FeatureSupport.ExpandableActivitySizeCalculator sizeCalc = new FeatureSupport.ExpandableActivitySizeCalculator(resizeShapeContext);
				int deltaX = sizeCalc.deltaX;
				int deltaY = sizeCalc.deltaY;
				
				if (deltaX != 0) {
					for (PictogramElement pe : containerChildren) {
						GraphicsAlgorithm childGa = pe.getGraphicsAlgorithm();
						if (childGa!=null) {
							int x = childGa.getX() - deltaX;
							childGa.setX(x);
							if (pe instanceof ContainerShape)
								movedChildren.add((ContainerShape)pe);
						}
					}
				}
				
				if (deltaY != 0) {
					for (PictogramElement pe : containerChildren) {
						GraphicsAlgorithm childGa = pe.getGraphicsAlgorithm();
						if (childGa!=null) {
							int y = childGa.getY() - deltaY;
							childGa.setY(y);
							if (!movedChildren.contains(pe) && pe instanceof ContainerShape)
								movedChildren.add((ContainerShape)pe);
						}
					}
				}
				
				super.resizeShape(context);
				FeatureSupport.updateExpandedSize(containerShape);
			}
			else {
				
				// Activity is collapsed
				
				for (PictogramElement pe : FeatureSupport.getContainerDecorators(containerShape)) {
					GraphicsAlgorithm childGa = pe.getGraphicsAlgorithm();
					if (childGa!=null) {
						childGa.setWidth(context.getWidth());
						childGa.setHeight(context.getHeight());
					}
				}
				
				super.resizeShape(context);
				FeatureSupport.updateCollapsedSize(containerShape);
			}
			
		} catch (Exception e) {
			Activator.logError(e);
		}
		
		FeatureSupport.updateConnections(getFeatureProvider(), movedChildren);
	}
}
