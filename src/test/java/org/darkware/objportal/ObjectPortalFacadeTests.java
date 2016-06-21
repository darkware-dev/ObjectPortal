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

import org.darkware.objportal.examples.SimpleTestClass;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author jeff@darkware.org
 * @since 2016-06-16
 */
public class ObjectPortalFacadeTests
{
    protected PortalProvider provider;
    protected final Integer intObj = new Integer(42);
    protected final SimpleTestClass testObj = new SimpleTestClass();

    protected final PortalContextToken defToken = new SimpleContextToken("DEFAULT");
    protected final PortalContextToken newToken = new SimpleContextToken("NEW");

    protected PortalContext defContext;
    protected PortalContext newContext;

    @Before
    public void init()
    {
        this.provider = mock(PortalProvider.class);
        ObjectPortal.useProvider(this.provider);

        this.defContext = new SimplePortalContext();
        this.defContext.place(Integer.class, 42);

        this.newContext = new SimplePortalContext();
        this.newContext.place(Integer.class, 99);

        when(this.provider.getDefaultToken()).thenReturn(this.defToken);
        when(this.provider.requestNewContext()).thenReturn(this.newToken);
        when(this.provider.getPortalContext()).thenReturn(this.defContext);
        when(this.provider.getPortalContext(this.defToken)).thenReturn(this.defContext);
        when(this.provider.getPortalContext(this.newToken)).thenReturn(this.newContext);
    }

    @Test
    public void check_classLoadable()
    {
        assertThat(new ObjectPortal()).isNotNull();
    }

    @Test
    public void autoInject_default()
    {
        ObjectPortal.autoInject(this.testObj);

        verify(this.provider).getPortalContext();
        verifyNoMoreInteractions(this.provider);
    }

    @Test
    public void autoInject_other()
    {
        ObjectPortal.autoInject(this.newToken, this.testObj);

        verify(this.provider).getPortalContext(this.newToken);
        verifyNoMoreInteractions(this.provider);
    }

    @Test
    public void newInstance_default()
    {
        SimpleTestClass instance = ObjectPortal.newInstance(SimpleTestClass.class);

        verify(this.provider).getPortalContext();
        verifyNoMoreInteractions(this.provider);
    }

    @Test
    public void newInstance_other()
    {
        SimpleTestClass instance = ObjectPortal.newInstance(this.newToken, SimpleTestClass.class);

        verify(this.provider).getPortalContext(this.newToken);
        verifyNoMoreInteractions(this.provider);
    }

    @Test
    public void take_default()
    {
        Integer i = ObjectPortal.take(Integer.class);

        verify(this.provider).getPortalContext();
        verifyNoMoreInteractions(this.provider);
    }

    @Test
    public void take_other()
    {
        Integer i = ObjectPortal.take(this.newToken, Integer.class);

        verify(this.provider).getPortalContext(this.newToken);
        verifyNoMoreInteractions(this.provider);
    }

    @Test
    public void place_default()
    {
        ObjectPortal.place(Integer.class, 42);

        verify(this.provider).getPortalContext();
        verifyNoMoreInteractions(this.provider);
    }

    @Test
    public void place_other()
    {
        ObjectPortal.place(this.newToken, Integer.class, 42);

        verify(this.provider).getPortalContext(this.newToken);
        verifyNoMoreInteractions(this.provider);
    }

    @Test
    public void placeSupplier_default()
    {
        ObjectPortal.place(Integer.class, new Supplier<Integer>()
        {
            @Override
            public Integer get()
            {
                return 42;
            }
        });

        verify(this.provider).getPortalContext();
        verifyNoMoreInteractions(this.provider);
    }

    @Test
    public void placeSupplier_other()
    {
        ObjectPortal.place(this.newToken, Integer.class, new Supplier<Integer>()
        {
            @Override
            public Integer get()
            {
                return 99;
            }
        });

        verify(this.provider).getPortalContext(this.newToken);
        verifyNoMoreInteractions(this.provider);
    }



}
