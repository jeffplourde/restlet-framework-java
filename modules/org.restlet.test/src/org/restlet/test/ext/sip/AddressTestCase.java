/**
 * Copyright 2005-2011 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the
 * "Licenses"). You can select the license that you prefer but you may not use
 * this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1.php
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1.php
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package org.restlet.test.ext.sip;

import junit.framework.TestCase;

import org.junit.Test;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.ext.sip.Address;
import org.restlet.ext.sip.internal.AddressReader;
import org.restlet.ext.sip.internal.AddressWriter;

/**
 * Test case for the Address reader and writer.
 * 
 * @author Thierry Boileau
 * 
 */
public class AddressTestCase extends TestCase {

    @Test
    public void testParsing() throws Exception {
        String str = "Anonymous <sip:c8oqz84zk7z@privacy.org>;tag=hyh8";
        AddressReader r = new AddressReader(str);
        Address a = r.readValue();

        assertEquals("Anonymous", a.getDisplayName());
        assertEquals("sip:c8oqz84zk7z@privacy.org", a.getReference().toString());
        assertEquals(1, a.getParameters().size());
        Parameter parameter = a.getParameters().get(0);
        assertEquals("tag", parameter.getName());
        assertEquals("hyh8", parameter.getValue());

        str = "sip:+12125551212@server.phone2net.com;tag=887s";
        r = new AddressReader(str);
        a = r.readValue();
        assertNull(a.getDisplayName());
        assertEquals("sip:+12125551212@server.phone2net.com", a.getReference()
                .toString());
        assertEquals(1, a.getParameters().size());
        parameter = a.getParameters().get(0);
        assertEquals("tag", parameter.getName());
        assertEquals("887s", parameter.getValue());

        str = "\"A. G. Bell\" <sip:agb@bell-telephone.com> ;tag=a48s";
        r = new AddressReader(str);
        a = r.readValue();
        assertEquals("A. G. Bell", a.getDisplayName());
        assertEquals("sip:agb@bell-telephone.com", a.getReference().toString());
        assertEquals(1, a.getParameters().size());
        parameter = a.getParameters().get(0);
        assertEquals("tag", parameter.getName());
        assertEquals("a48s", parameter.getValue());

        str = "A. G. Bell <sip:agb@bell-telephone.com> ;tag=a48s";
        r = new AddressReader(str);
        a = r.readValue();
        r = new AddressReader(str);
        a = r.readValue();
        assertEquals("A. G. Bell", a.getDisplayName());
        assertEquals("sip:agb@bell-telephone.com", a.getReference().toString());
        assertEquals(1, a.getParameters().size());
        parameter = a.getParameters().get(0);
        assertEquals("tag", parameter.getName());
        assertEquals("a48s", parameter.getValue());
    }

    @Test
    public void testWriting() {
        Address a = new Address();
        a.setDisplayName("A. G. Bell");
        a.setReference(new Reference("sip:agb@bell-telephone.com"));
        a.getParameters().add("tag", "a48s");

        AddressWriter w = new AddressWriter();
        assertEquals("\"A. G. Bell\" <sip:agb@bell-telephone.com> ;tag=a48s", w
                .append(a).toString());

        w = new AddressWriter();
        a = new Address();
        a.setDisplayName(null);
        a.setReference(new Reference("sip:agb@bell-telephone.com"));
        a.getParameters().add("tag", "a48s");
        assertEquals("<sip:agb@bell-telephone.com> ;tag=a48s", w.append(a)
                .toString());
    }

}