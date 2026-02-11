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
