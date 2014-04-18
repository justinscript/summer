/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Iterator;

/**
 * This interfaces contains all commonly used Yaml functions. {@link Yaml} contains all of these methods as static
 * methods for convient access while {@link YamlConfig} implements this interface.
 * 
 * @author zxc Apr 14, 2013 12:34:02 AM
 */
@SuppressWarnings("rawtypes")
public interface YamlOperations {

    /**
     * Loads one object from an input stream
     * 
     * @param in the stream to read from
     * @return the first object on the stream in Yaml format
     */
    public Object load(InputStream in);

    /**
     * Loads one object from a Reader
     * 
     * @param in the reader to read from
     * @return the first object on the stream in Yaml format
     */
    public Object load(Reader in);

    /**
     * Loads one object from a file in Yaml format
     * 
     * @param file the file to read from
     * @return the first object in the file in Yaml format
     * @throws FileNotFoundException
     */
    public Object load(File file) throws FileNotFoundException;

    /**
     * Loads one object from a string of Yaml text
     * 
     * @param yamlText the text to read from
     * @return the first object in the Yaml text
     */
    public Object load(String yamlText);

    /**
     * Loads one object from an input stream of the specified type
     * 
     * @param <T> the specified type
     * @param in the stream to read from
     * @param clazz the class of the specified type
     * @return the first object in the stream in Yaml format
     */
    public <T> T loadType(InputStream in, Class<T> clazz);

    /**
     * Loads one object from a a reader of the specified type
     * 
     * @param <T> the specified type
     * @param in the reader to read from
     * @param clazz the class of the specified type
     * @return the first object in the stream in Yaml format
     */
    public <T> T loadType(Reader in, Class<T> clazz);

    /**
     * Loads one object from a file in Yaml format
     * 
     * @param <T> the specified type
     * @param file the file to read from
     * @param clazz the class of the specified type
     * @return the first object in the file in Yaml format
     * @throws FileNotFoundException
     */
    public <T> T loadType(File file, Class<T> clazz) throws FileNotFoundException;

    /**
     * Loads one object from a file in Yaml format
     * 
     * @param <T> the specified type
     * @param yamlText the Yaml text
     * @param clazz the class of the specified type
     * @return the first object in the Yaml text
     */
    public <T> T loadType(String yamlText, Class<T> clazz);

    /**
     * Loads the objects in input stream in Yaml format into a YamlStream, which is used to iterate the objects in the
     * input stream
     * 
     * @param in the stream to read from
     * @return a YamlStream for iterating the objects
     */
    public YamlStream loadStream(InputStream in);

    /**
     * Loads the objects in reader in Yaml format into a YamlStream, which is used to iterate the objects in the input
     * stream
     * 
     * @param in the reader to read from
     * @return a YamlStream for iterating the objects
     */
    public YamlStream loadStream(Reader in);

    /**
     * Loads the objects in a file in Yaml format into a YamlStream, which is used to iterate the objects in the file
     * 
     * @param file the file to read from
     * @return a YamlStream for iterating the objects
     * @throws FileNotFoundException
     */
    public YamlStream loadStream(File file) throws FileNotFoundException;

    /**
     * Loads the objects in a Yaml text into a YamlStream, which is used to iterate the objects in the Yaml text
     * 
     * @param yamlText
     * @return a YamlStream for iterating the objects
     */
    public YamlStream loadStream(String yamlText);

    /**
     * Loads the objects of a specified type in an input stream in Yaml format into a YamlStream, which is used to
     * iterate the objects in the input stream
     * 
     * @param <T> the specified type
     * @param in the stream to read from
     * @param clazz the class of the specified type
     * @return a YamlStream for iterating the objects
     */
    public <T> YamlStream<T> loadStreamOfType(InputStream in, Class<T> clazz);

    /**
     * Loads the objects of a specified type in a reader in Yaml format into a YamlStream, which is used to iterate the
     * objects in the input stream
     * 
     * @param <T> the specified type
     * @param in the reader to read from
     * @param clazz the class of the specified type
     * @return a YamlStream for iterating the objects
     */
    public <T> YamlStream<T> loadStreamOfType(Reader in, Class<T> clazz);

    /**
     * Loads the objects of a specified type in a file in Yaml format into a YamlStream, which is used to iterate the
     * objects in the file
     * 
     * @param <T> the specified type
     * @param file the file to read from
     * @param clazz the class of the specified type
     * @return a YamlStream for iterating the objects
     * @throws FileNotFoundException
     */
    public <T> YamlStream<T> loadStreamOfType(File file, Class<T> clazz) throws FileNotFoundException;

    /**
     * Loads the objects of a specified type in a in Yaml format into a YamlStream, which is used to iterate the objects
     * in the file
     * 
     * @param <T> the specified type
     * @param yamlText the text to read from
     * @param clazz the class of the specified type
     * @return a YamlStream for iterating the objects
     * @throws FileNotFoundException
     */
    public <T> YamlStream<T> loadStreamOfType(String yamlText, Class<T> clazz);

    /**
     * Dumps an object to a file in Yaml format
     * 
     * @param obj the object to dump
     * @param file the file to dump to
     * @throws FileNotFoundException
     */
    public void dump(Object obj, File file) throws FileNotFoundException;

    /**
     * Dumps an object to a file in Yaml format
     * 
     * @param obj the object to dump
     * @param file the file to dump to
     * @param minimalOutput whether minimal output is on
     * @throws FileNotFoundException
     */
    public void dump(Object obj, File file, boolean minimalOutput) throws FileNotFoundException;

    /**
     * Dumps an object into Yaml format
     * 
     * @param obj the object to dump
     * @return a String in Yaml format representing the object
     */
    public String dump(Object obj);

    /**
     * Dumps an object into Yaml format
     * 
     * @param obj the object to dump
     * @param minimalOutput whether minimal output is on
     * @return a String in Yaml format representing the object
     */
    public String dump(Object obj, boolean minimalOutput);

    /**
     * Dumps a stream of objects specified with an iterator to a file in Yaml format, one document per object
     * 
     * @param iterator the iterator to read objects from
     * @param file the file to write to
     * @throws FileNotFoundException
     */
    public void dumpStream(Iterator iterator, File file) throws FileNotFoundException;

    /**
     * Dumps a stream of objects specified with an iterator to a file in Yaml format, one document per object
     * 
     * @param iterator the iterator to read objects from
     * @param file the file to write to
     * @param minimalOutput whether minimal output is on
     * @throws FileNotFoundException
     */
    public void dumpStream(Iterator iterator, File file, boolean minimalOutput) throws FileNotFoundException;

    /**
     * Dumps a stream of objects specified with an iterator to a String in Yaml format, one document per object
     * 
     * @param iterator the iterator to read objects from
     * @return a String in Yaml format representing the object
     */
    public String dumpStream(Iterator iterator);

    /**
     * Dumps a stream of objects specified with an iterator to a String in Yaml format, one document per object
     * 
     * @param iterator the iterator to read objects from
     * @param minimalOutput whether minimal output is on
     * @return a String in Yaml format representing the object
     */
    public String dumpStream(Iterator iterator, boolean minimalOutput);

    /**
     * Dumps an object to an OutputStream
     * 
     * @param obj the object to dump
     * @param out the stream to write to
     */
    public void dump(Object obj, OutputStream out);

    /**
     * Dumps an object to an OutputStream
     * 
     * @param obj the object to dump
     * @param out the stream to write to
     * @param minimalOutput whether to minimal output is on
     */
    public void dump(Object obj, OutputStream out, boolean minimalOutput);
}
