/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.view.facelets.tag.composite;

import java.io.IOException;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.view.facelets.Facelet;
import org.apache.myfaces.view.facelets.AbstractFacelet;
import org.apache.myfaces.view.facelets.FaceletFactory;
import org.apache.myfaces.view.facelets.FaceletViewDeclarationLanguage;
import org.apache.myfaces.view.facelets.tag.jsf.ComponentSupport;

/**
 * This listener must be attached to PostAddToViewEvent, so when composite component is
 * added to the view, the algorithm that create the composite component content is executed.
 *
 * @author lu4242
 */
public class CreateDynamicCompositeComponentListener 
    implements ComponentSystemEventListener, StateHolder
{
    private String taglibURI;
    private String tagName;
    private Map<String,Object> attributes;
    private String baseKey;

    public CreateDynamicCompositeComponentListener(String taglibURI, String tagName, 
        Map<String, Object> attributes, String baseKey)
    {
        this.taglibURI = taglibURI;
        this.tagName = tagName;
        this.attributes = attributes;
        this.baseKey = baseKey;
    }

    public CreateDynamicCompositeComponentListener()
    {
    }
    
    public void processEvent(ComponentSystemEvent event)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        FaceletViewDeclarationLanguage vdl = (FaceletViewDeclarationLanguage) 
            facesContext.getApplication().getViewHandler().getViewDeclarationLanguage(
                facesContext, facesContext.getViewRoot().getViewId());
        
        Facelet componentFacelet;
        FaceletFactory faceletFactory = vdl.getFaceletFactory();
            
        FaceletFactory.setInstance(faceletFactory);
        try
        {
            componentFacelet
                    = faceletFactory.compileComponentFacelet(taglibURI, tagName, attributes);
        }
        finally
        {
            FaceletFactory.setInstance(null);
        }
        try
        {
            UIComponent component = event.getComponent(); 
            
            Integer step = (Integer) component.getAttributes().get(
                CompositeComponentResourceTagHandler.CREATE_CC_ON_POST_ADD_TO_VIEW); 
            if (step != null && step.intValue() == 0)
            {
                component.getAttributes().put(CompositeComponentResourceTagHandler.CREATE_CC_ON_POST_ADD_TO_VIEW, 1);
            }
            else
            {
                return;
            }
            
            facesContext.getAttributes().put(FaceletViewDeclarationLanguage.REFRESHING_TRANSIENT_BUILD,
                Boolean.TRUE);
            
            // The trick here is restore MARK_CREATED, just to allow ComponentTagHandlerDelegate to
            // find the component. Then we reset it to exclude it from facelets refresh algorithm.
            String markId = (String) component.getAttributes().get("oam.vf.GEN_MARK_ID");
            if (markId == null)
            {
                ((AbstractFacelet)componentFacelet).applyDynamicComponentHandler(
                    facesContext, component, baseKey);
            }
            else
            {
                try
                {
                    component.getAttributes().put(ComponentSupport.MARK_CREATED, markId);
                    ((AbstractFacelet)componentFacelet).applyDynamicComponentHandler(
                        facesContext, component.getParent(), baseKey);
                }
                finally
                {
                    component.getAttributes().put(ComponentSupport.MARK_CREATED, null);
                }
            }
        }
        catch (IOException e)
        {
            throw new FacesException(e);
        }
        finally
        {
            facesContext.getAttributes().remove(
                FaceletViewDeclarationLanguage.REFRESHING_TRANSIENT_BUILD);
        }
    }

    public Object saveState(FacesContext context)
    {
        Object[] values = new Object[4];
        values[0] = taglibURI;
        values[1] = tagName;
        values[2] = attributes;
        values[3] = baseKey;
        return values;
    }

    public void restoreState(FacesContext context, Object state)
    {
        Object[] values = (Object[]) state;
        taglibURI = (String) values[0];
        tagName = (String)values[1];
        attributes = (Map<String,Object>) values[2];
        baseKey = (String)values[3];
    }

    public boolean isTransient()
    {
        return false;
    }

    public void setTransient(boolean newTransientValue)
    {
    }

}
