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
package org.apache.myfaces.view.facelets.tag;

import java.util.Arrays;
import javax.faces.view.facelets.TagAttribute;
import org.junit.Assert;
import org.junit.Test;

public class TagAttributesImplTest
{
    @Test
    public void testNamespaceMapping()
    {
        TagAttributeImpl test1 = new TagAttributeImpl(null, "", "test1", null, "");
        TagAttributeImpl test2 = new TagAttributeImpl(null, "", "test2", null, "");
        TagAttributeImpl testTest1 = new TagAttributeImpl(null, "test", "test1", null, "");
        
        TagAttributesImpl impl = new TagAttributesImpl(new TagAttribute[] { test1, test2, testTest1 });
        Assert.assertEquals(test1, impl.get("test1"));
        Assert.assertEquals(test2, impl.get("test2"));
        
        Assert.assertEquals(testTest1, impl.get("test", "test1"));

        
        Assert.assertEquals(3, Arrays.asList(impl.getAll()).size());
        Assert.assertTrue(Arrays.asList(impl.getAll()).contains(test1));
        Assert.assertTrue(Arrays.asList(impl.getAll()).contains(test2));
        Assert.assertTrue(Arrays.asList(impl.getAll()).contains(testTest1));
        
        Assert.assertEquals(2, Arrays.asList(impl.getAll("")).size());
        Assert.assertTrue(Arrays.asList(impl.getAll()).contains(test1));
        Assert.assertTrue(Arrays.asList(impl.getAll()).contains(test2));
    }
    
    @Test
    public void testNotAvailable()
    {
        TagAttributesImpl impl = new TagAttributesImpl(new TagAttribute[] { });
        Assert.assertEquals(null, impl.get("test1"));
        Assert.assertEquals(null, impl.get("test", "test2"));
    }
}
