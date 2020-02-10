/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus.java;

import org.codehaus.gmavenplus.groovy.GObject;


public class JClass {
    private GObject gObject = new GObject();
    private JObject jObject = new JObject();

    public GObject getgObject() {
        return gObject;
    }

    public void setgObject(GObject gObject) {
        this.gObject = gObject;
    }

    public JObject getjObject() {
        return jObject;
    }

    public void setjObject(JObject jObject) {
        this.jObject = jObject;
    }
}
