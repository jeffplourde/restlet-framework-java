/**
 * Copyright 2005-2012 Restlet S.A.S.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet S.A.S.
 */

package org.restlet.ext.jackson;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

/**
 * Representation based on the Jackson library. It can serialize and deserialize
 * automatically in Jackson smile format.
 * 
 * @see <a href="http://jackson.codehaus.org/">Jackson project</a>
 * @author Jerome Louvel
 * @param <T>
 *            The type to wrap.
 */
public class JacksonSmileRepresentation<T> extends OutputRepresentation {

    /** The (parsed) object to format. */
    private T object;

    /** The object class to instantiate. */
    private Class<T> objectClass;

    /** The JSON representation to parse. */
    private Representation jsonRepresentation;

    /** The modifiable Jackson object mapper. */
    private ObjectMapper objectMapper;

    /**
     * Constructor.
     * 
     * @param mediaType
     *            The target media type.
     * @param object
     *            The object to format.
     */
    @SuppressWarnings("unchecked")
    public JacksonSmileRepresentation(MediaType mediaType, T object) {
        super(mediaType);
        this.object = object;
        this.objectClass = (Class<T>) ((object == null) ? null : object
                .getClass());
        this.jsonRepresentation = null;
        this.objectMapper = null;
    }

    /**
     * Constructor.
     * 
     * @param representation
     *            The representation to parse.
     */
    public JacksonSmileRepresentation(Representation representation,
            Class<T> objectClass) {
        super(representation.getMediaType());
        this.object = null;
        this.objectClass = objectClass;
        this.jsonRepresentation = representation;
        this.objectMapper = null;
    }

    /**
     * Constructor.
     * 
     * @param object
     *            The object to format.
     */
    public JacksonSmileRepresentation(T object) {
        this(MediaType.APPLICATION_JSON_SMILE, object);
    }

    /**
     * Creates a Jackson object mapper based on a media type. By default, it
     * calls {@link ObjectMapper#ObjectMapper(JsonFactory)} with a {@link SmileFactory}.
     * 
     * @return The Jackson object mapper.
     */
    protected ObjectMapper createObjectMapper() {
        JsonFactory jsonFactory = new SmileFactory();
        jsonFactory.configure(Feature.AUTO_CLOSE_TARGET, false);
        return new ObjectMapper(jsonFactory);
    }

    /**
     * Returns the wrapped object, deserializing the representation with Jackson
     * if necessary.
     * 
     * @return The wrapped object.
     * @throws IOException
     */
    public T getObject() throws IOException {
        T result = null;

        if (this.object != null) {
            result = this.object;
        } else if (this.jsonRepresentation != null) {
            result = getObjectMapper().readValue(
                    this.jsonRepresentation.getStream(), this.objectClass);
        }

        return result;
    }

    /**
     * Returns the object class to instantiate.
     * 
     * @return The object class to instantiate.
     */
    public Class<T> getObjectClass() {
        return objectClass;
    }

    /**
     * Returns the modifiable Jackson object mapper. Useful to customize
     * mappings.
     * 
     * @return The modifiable Jackson object mapper.
     */
    public ObjectMapper getObjectMapper() {
        if (this.objectMapper == null) {
            this.objectMapper = createObjectMapper();
        }

        return this.objectMapper;
    }

    /**
     * Sets the object to format.
     * 
     * @param object
     *            The object to format.
     */
    public void setObject(T object) {
        this.object = object;
    }

    /**
     * Sets the object class to instantiate.
     * 
     * @param objectClass
     *            The object class to instantiate.
     */
    public void setObjectClass(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    /**
     * Sets the Jackson object mapper.
     * 
     * @param objectMapper
     *            The Jackson object mapper.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        if (jsonRepresentation != null) {
            jsonRepresentation.write(outputStream);
        } else if (object != null) {
            getObjectMapper().writeValue(outputStream, object);
        }

    }
}
