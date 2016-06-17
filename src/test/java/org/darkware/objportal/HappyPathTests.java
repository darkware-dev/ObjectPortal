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

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * @author jeff@darkware.org
 * @since 2016-06-09
 */
public class HappyPathTests
{
    @Test
    public void injectParentField()
    {
        ObjectPortal.useProvider(new SingletonPortalProvider(new SimplePortalContext()));
        ObjectPortal.place(Integer.class, 42);

        ExampleClass ex = ObjectPortal.newInstance(ExampleChildClass.class);

        assertEquals(new Integer(42), ex.answer);
    }

    @Test
    public void injectParentField_threadLocal()
    {
        ObjectPortal.useProvider(new ThreadLocalPortalProvider());
        ObjectPortal.place(Integer.class, 42);

        ExampleClass ex = ObjectPortal.newInstance(ExampleChildClass.class);

        assertEquals(new Integer(42), ex.answer);
    }

    @SuppressWarnings("all")
    private static class ExampleClass
    {
        @Inject
        public Integer answer;
        private Integer foo;

        /** Default constructor */
        public ExampleClass()
        {
            super();

            this.foo = 99;
        }
    }

    private static class ExampleChildClass extends ExampleClass
    {
        public ExampleChildClass()
        {
            super();
        }
    }
}
