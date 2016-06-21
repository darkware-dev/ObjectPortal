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

import org.darkware.objportal.error.ObjectCreationError;
import org.darkware.objportal.examples.SimpleTestClass;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Tests for the {@link SimplePortalContext} class
 *
 * @author jeff@darkware.org
 * @since 2016-06-09
 */
public class SimplePortalContextTests
{
    protected SimplePortalContext context;

    @Before
    public void setup()
    {
        this.context = new SimplePortalContext();
    }

    @Test
    public void constructor_simple()
    {
        assertNotNull(this.context);
    }

    @Test
    public void storeRetrieve_object()
    {
        Integer iVal = 42;

        this.context.place(Integer.class, iVal);

        assertSame(iVal, this.context.take(Integer.class));
    }

    @Test
    public void storeRetrieve_supplier()
    {
        this.context.place(Integer.class, new Supplier<Integer>()
        {
            @Override
            public Integer get()
            {
                return 42;
            }
        });

        assertTrue(this.context.hasInstance(Integer.class));
        assertEquals(new Integer(42), this.context.take(Integer.class));
    }

    @Test
    public void newInstance_normal()
    {
        this.context.place(Integer.class, 42);
        SimpleTestClass instance = this.context.newInstance(SimpleTestClass.class);

        assertThat(instance).isNotNull();
    }

    @Test
    public void newInstance_noConstructorFound()
    {
        assertThatExceptionOfType(ObjectCreationError.class)
                .isThrownBy(() ->  {this.context.newInstance(NoAvailableConstructorClass.class);})
                .withCauseInstanceOf(InstantiationException.class);
    }

    @Test
    public void newInstance_privateConstructor()
    {
        assertThatExceptionOfType(ObjectCreationError.class)
                .isThrownBy(() ->  {this.context.newInstance(NoPublicConstructorClass.class);})
                .withCauseInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void newInstance_exceptionInConstructor()
    {
        assertThatExceptionOfType(ObjectCreationError.class)
                .isThrownBy(() ->  {this.context.newInstance(ExceptionConstructorClass.class);})
                .withCauseInstanceOf(Exception.class);
    }

    @Test
    public void newInstance_runtimeExceptionInConstructor()
    {
        assertThatExceptionOfType(ObjectCreationError.class)
                .isThrownBy(() ->  {this.context.newInstance(RuntimeExceptionConstructorClass.class);})
                .withCauseInstanceOf(RuntimeException.class);
    }

    /** A class with no suitable constructor for injection */
    public static class NoAvailableConstructorClass
    {
        /** A constructor that can never be called correctly. */
        public NoAvailableConstructorClass(final NoAvailableConstructorClass otherInstance)
        {
            super();
        }
    }

    /** A class with no suitable constructor for injection */
    public static class NoPublicConstructorClass
    {
        /** A constructor that can never be called correctly. */
        private NoPublicConstructorClass()
        {
            super();
        }
    }

    /** A class that throws a runtime exception when created */
    public static class RuntimeExceptionConstructorClass
    {
        /** A constructor that can never be called correctly. */
        public RuntimeExceptionConstructorClass()
        {
            super();
            throw new RuntimeException("Cannot be instantiated.");
        }
    }

    /** A class that throws an exception when created */
    public static class ExceptionConstructorClass
    {
        /** A constructor that can never be called correctly. */
        public ExceptionConstructorClass() throws Exception
        {
            super();
            throw new Exception("Cannot be instantiated.");
        }
    }


}
