/*
 * Copyright (c) 2007, 2008, 2009, 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. March 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.json;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxReader;
import com.thoughtworks.xstream.io.xml.StaxWriter;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;

import javax.xml.stream.XMLStreamException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;


/**
 * Simple XStream driver wrapping Jettison's Mapped reader and writer. Serializes object from
 * and to JSON.
 * 
 * @author Dejan Bosanac
 */
public class JettisonMappedXmlDriver extends AbstractDriver {

    private final MappedXMLOutputFactory mof;
    private final MappedXMLInputFactory mif;
    private final MappedNamespaceConvention convention;
    private boolean useSerializeAsArray = true;

    /**
     * Construct a JettisonMappedXmlDriver.
     */
    public JettisonMappedXmlDriver() {
        this(new Configuration());
    }

    /**
     * Construct a JettisonMappedXmlDriver with configuration.
     * @param config the Jettison configuration
     */
    public JettisonMappedXmlDriver(final Configuration config) {
        this(config, true);
    }

    /**
     * Construct a JettisonMappedXmlDriver with configuration. This constructor has been added
     * by special request of Jettison users to support JSON generated by older Jettison
     * versions. if the driver is setup to ignore the XStream hints for JSON arrays, there is
     * neither support from XStream's side nor are there any tests to ensure this mode.
     * 
     * @param config the Jettison configuration
     * @param useSerializeAsArray flag to use XStream's hints for collections and arrays
     * @since upcoming
     */
    public JettisonMappedXmlDriver(final Configuration config, final boolean useSerializeAsArray) {
        mof = new MappedXMLOutputFactory(config);
        mif = new MappedXMLInputFactory(config);
        convention = new MappedNamespaceConvention(config);
        this.useSerializeAsArray = useSerializeAsArray;
    }
    
    public HierarchicalStreamReader createReader(final Reader reader) {
        try {
            return new StaxReader(new QNameMap(), mif.createXMLStreamReader(reader), getNameCoder());
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(final InputStream input) {
        try {
            return new StaxReader(new QNameMap(), mif.createXMLStreamReader(input), getNameCoder());
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(final Writer writer) {
        try {
            if (useSerializeAsArray) {
                return new JettisonStaxWriter(new QNameMap(), mof.createXMLStreamWriter(writer), getNameCoder(), convention);
            } else {
                return new StaxWriter(new QNameMap(), mof.createXMLStreamWriter(writer), getNameCoder());
            }
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(final OutputStream output) {
        try {
            if (useSerializeAsArray) {
                return new JettisonStaxWriter(new QNameMap(), mof.createXMLStreamWriter(output), getNameCoder(), convention);
            } else {
                return new StaxWriter(new QNameMap(), mof.createXMLStreamWriter(output), getNameCoder());
            }
        } catch (final XMLStreamException e) {
            throw new StreamException(e);
        }
    }

}
