/*
 * Copyright (c) 2016. darkware.org and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.darkware.objportal;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the {@link SingletonPortalProvider} class.
 *
 * @author jeff@darkware.org
 * @since 2016-06-15
 */
public class SingletonProviderTests
{
    @Test
    public void simple_happyPath()
    {
        PortalProvider provider = new SingletonPortalProvider();
        ObjectPortal.useProvider(provider);

        provider.getPortalContext().place(Integer.class, 42);

        SimpleTestClass check = ObjectPortal.newInstance(SimpleTestClass.class);

        assertEquals(42, check.getAnswer().intValue());
    }

    @Test
    public void endToEnd_confirmSharedContext()
    {
        PortalProvider provider = new SingletonPortalProvider();
        ObjectPortal.useProvider(provider);

        provider.getPortalContext().place(Integer.class, 42);

        SimpleTestClass check1 = ObjectPortal.newInstance(SimpleTestClass.class);
        PortalContext contextBefore = provider.getPortalContext();

        // Request a new context.
        // ... Ideally, this should have no effect.
        PortalContextToken newToken = provider.requestNewContext();
        provider.useDefaultToken(newToken);

        SimpleTestClass check2 = ObjectPortal.newInstance(SimpleTestClass.class);
        PortalContext contextAfter = provider.getPortalContext();

        assertEquals(42, check1.getAnswer().intValue());
        assertEquals(42, check2.getAnswer().intValue());
        assertSame(contextBefore, contextAfter);
    }

    @Test
    public void internal_confirmIgnoredContextRequest()
    {
        PortalProvider provider = new SingletonPortalProvider();

        PortalContextToken initialToken = provider.getDefaultToken();
        PortalContextToken newToken = provider.requestNewContext();

        assertSame(initialToken, newToken);
    }

    @Test
    public void internal_confirmContextAlwaysTheSame()
    {
        PortalProvider provider = new SingletonPortalProvider();

        PortalContext context = provider.getPortalContext();

        PortalContextToken token = new SimpleContextToken("newToken");

        assertSame(context, provider.getPortalContext(token));
    }

    @Test
    public void internal_confirmContextResistsChange()
    {
        PortalProvider provider = new SingletonPortalProvider();

        PortalContext context = provider.getPortalContext();

        PortalContextToken token = new SimpleContextToken("newToken");
        provider.useDefaultToken(token);

        assertSame(context, provider.getPortalContext());
    }

}
