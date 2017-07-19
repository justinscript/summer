/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.CollectionWrapper;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.MapWrapper;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.ObjectWrapper;
import com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper.SimpleObjectWrapper;

/**
 * YamlEncoder - The usage of YamlEncoder mirrors that of java.beans.XMLEncoder. You create an encoder, make some calls
 * to writeObject, and then close the encoder. In most cases you may find it is not necessary to access this class
 * directly as {@link Yaml} contain methods for the most common usages. The utility functions that were previously in
 * this class are now in {@link Yaml}.
 * 
 * @author zxc Apr 14, 2013 12:34:23 AM
 */
@SuppressWarnings("rawtypes")
public class YamlEncoder {

    PrintWriter              out;
    Map<Object, ObjectEntry> referenceMap = new IdentityHashMap<Object, ObjectEntry>();
    YamlConfig               config       = YamlConfig.getDefaultConfig();
    int                      nextRefName  = 0;

    /**
     * Creates a YamlEncoder writing to specifed stream.
     * 
     * @param out the output stream to write to.
     */
    public YamlEncoder(OutputStream out) {
        try {
            this.out = new PrintWriter(new OutputStreamWriter(out, config.encoding));
        } catch (UnsupportedEncodingException e) {
            throw new YamlException("Unsupported encoding " + config.encoding);
        }
    }

    /**
     * Creates a YamlEncoder writing to specifed stream.
     * 
     * @param out the output stream to write to.
     * @param config the YAML config
     */
    public YamlEncoder(OutputStream out, YamlConfig config) {
        try {
            this.config = config;
            this.out = new PrintWriter(new OutputStreamWriter(out, config.encoding));
        } catch (UnsupportedEncodingException e) {
            throw new YamlException("Unsupported encoding " + config.encoding);
        }
    }

    /**
     * Returns the indentation amount used for one indentation level.
     * 
     * @return the amount of indentation used for one indentation level.
     */
    public String getIndentAmount() {
        return config.getIndentAmount();
    }

    /**
     * Sets indentation amount.
     * 
     * @param indentAmount must be a string consisting only of spaces.
     */
    public void setIndentAmount(String indentAmount) {
        config.setIndentAmount(indentAmount);
    }

    /**
     * Returns whether the minimal output option is set.
     * 
     * @return whether the minimal output option is set.
     */
    public boolean isMinimalOutput() {
        return config.isMinimalOutput();
    }

    /**
     * Sets the minimal output option.
     * 
     * @param minimalOutput true for on; false for off.
     */
    public void setMinimalOutput(boolean minimalOutput) {
        config.setMinimalOutput(minimalOutput);
    }

    class ObjectEntry {

        Object  target;
        int     ref;
        int     refs           = 0;
        boolean anchorDeclared = false;

        ObjectEntry(Object t) {
            target = t;
        }

        public String toString() {
            return "{target: " + target + ", refname: " + ref + ", refs: " + refs + "}";
        }
    }

    void traverseAndCount(Object obj) {
        if (obj == null) return;
        if (obj instanceof String) return;
        mark(obj);
        if (refCount(obj) > 1) return;
        else if (ReflectionUtil.isSimpleType(obj.getClass())) return;
        else {
            ObjectWrapper wrapper = getConfig().getWrapper(obj);
            if (wrapper instanceof CollectionWrapper) traverseAndCountCollection((CollectionWrapper) wrapper);
            else if (wrapper instanceof MapWrapper) traverseAndCountMap((MapWrapper) wrapper);
        }
    }

    void traverseAndCountCollection(CollectionWrapper c) {
        for (Object obj : c) {
            traverseAndCount(obj);
        }
    }

    void traverseAndCountMap(MapWrapper map) {
        for (Object key : map.keys()) {
            Object value = map.get(key);
            traverseAndCount(key);
            traverseAndCount(value);
        }
    }

    int refCount(Object obj) {
        ObjectEntry ent = referenceMap.get(obj);
        return ent != null ? ent.refs : 0;
    }

    boolean toBeAnchored(Object obj) {
        ObjectEntry ent = referenceMap.get(obj);
        return (ent != null && ent.refs > 1 && !ent.anchorDeclared);
    }

    boolean toBeAliased(Object obj) {
        ObjectEntry ent = referenceMap.get(obj);
        return (ent != null && ent.refs > 1 && ent.anchorDeclared);
    }

    void mark(Object obj) {
        ObjectEntry ent = referenceMap.get(obj);
        if (ent == null) {
            ent = new ObjectEntry(obj);
            referenceMap.put(obj, ent);
            ent.ref = nextRefName++;
        }
        ent.refs++;
    }

    /**
     * Write an object to the stream.
     * 
     * @param obj object to write.
     */
    public void writeObject(Object obj) {
        traverseAndCount(obj);
        out.print("--- ");
        writeObject(obj, "", obj.getClass());
        reset();
    }

    void reset() {
        referenceMap.clear();
    }

    String indent(String s) {
        return getIndentAmount() + s;
    }

    void writeObject(Object value, String indent, Class expectedType) {
        if (value == null) out.println("~");
        else if (toBeAliased(value)) writeReference(value);
        else {
            if (toBeAnchored(value)) writeAlias(value);
            ObjectWrapper wrapper = getConfig().getWrapper(value);
            if (wrapper instanceof SimpleObjectWrapper) {
                writeSimpleValue((SimpleObjectWrapper) wrapper, expectedType, indent);
            } else if (wrapper instanceof CollectionWrapper) {
                writeCollection((CollectionWrapper) wrapper, expectedType, indent);
            } else if (wrapper instanceof MapWrapper) {
                writeMap((MapWrapper) wrapper, indent, expectedType);
            }
        }
    }

    void writeReference(Object value) {
        ObjectEntry ent = referenceMap.get(value);
        out.println("*" + ent.ref);
    }

    void writeAlias(Object value) {
        ObjectEntry ent = referenceMap.get(value);
        out.print("&" + ent.ref + " ");
        ent.anchorDeclared = true;

    }

    void writeSimpleValue(SimpleObjectWrapper value, Class expectedType, String indent) {
        if ((!Utilities.classEquals(expectedType, value.getType()) || !isMinimalOutput())
            && !(value.getType() == Integer.class || value.getType() == Boolean.class || value.getType() == String.class)) {
            out.print("!" + getTransferName(value.getType()) + " ");
        }
        Object outputValue = value.getOutputValue();
        if (outputValue == null) out.println("~");
        else if (outputValue instanceof String || outputValue instanceof Character) out.println(Utilities.stringify(outputValue,
                                                                                                                    indent));
        else out.println(outputValue);

    }

    /**
     * assumes map is not null
     * 
     * @param map
     * @param indent
     */
    void writeMap(MapWrapper map, String indent, Class expectedType) {
        if ((isMinimalOutput() && expectedType == map.getType()) || expectedType == HashMap.class
            || expectedType == Map.class) out.print("");
        else out.print("!" + getTransferName(map.getType()));
        if (map.keys().size() == 0) out.println(" {}");
        else {
            out.println("");
            for (Object key : map.keys()) {
                Object value = map.get(key);
                out.print(indent + Utilities.stringify(key) + ": ");
                writeObject(value, indent(indent), map.getExpectedType(key));
            }
        }
    }

    /**
     * assumes col is not null
     * 
     * @param col
     * @param indent
     */
    void writeCollection(CollectionWrapper col, Class expectedType, String indent) {
        if (col.size() > 0) {
            if ((isMinimalOutput() && expectedType == col.getType()) || col.getType() == ArrayList.class
                || expectedType == List.class) out.println();
            else out.println("!" + getTransferName(col.getType()));
            for (Object o : col) {
                out.print(indent + "- ");
                writeObject(o, indent(indent), col.isTyped() ? col.componentType() : null);
            }
        } else {
            if ((isMinimalOutput() && expectedType == col.getType()) || col.getType() == ArrayList.class
                || expectedType == List.class) out.print("");
            else out.print("!" + getTransferName(col.getType()) + " ");
            if (col.size() == 0) out.println("[]");
        }
    }

    String getTransferName(Class clazz) {
        return /* "java/object:" + */ReflectionUtil.className(clazz, config);
    }

    /**
     * Closes this YamlEncoder instance. This must be done after a write sequence for the write to be effective.
     */
    public void close() {
        out.close();
    }

    /**
     * Flushes the outputStream that this YamlEncoder points to.
     */
    public void flush() {
        out.flush();
    }

    /**
     * @return the config object for this encoder.
     */
    public YamlConfig getConfig() {
        return config;
    }

    /**
     * @param config the new config object for this encoder.
     */
    public void setConfig(YamlConfig config) {
        this.config = config;
    }

}
