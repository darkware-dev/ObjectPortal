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

import org.darkware.objportal.examples.DependentConstructorClass;
import org.darkware.objportal.examples.MixedDefaultConstructorClass;
import org.darkware.objportal.examples.MultiConstructorClass;
import org.darkware.objportal.examples.RepeatedParamConstructorClass;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests for the {@link ObjectBuilder} class.
 *
 * @author jeff@darkware.org
 * @since 2016-06-21
 */
public class ObjectBuilderTests
{
    private PortalContext context;

    @Before
    public void init()
    {
        this.context = new SimplePortalContext();

        this.context.place(Integer.class, 42);
        this.context.placeSource(Path.class, () -> Paths.get("/etc/passwd"));
        this.context.place(PrintStream.class, System.out);
    }

    @Test
    public void simple_noArg()
    {
        ObjectBuilder<String> stringBuilder = new ObjectBuilder<>(String.class, this.context);

        assertThat(stringBuilder.get()).isEqualTo("");
    }

    @Test
    public void simple_simpleArg()
    {
        ObjectBuilder<DependentConstructorClass> builder = new ObjectBuilder<>(DependentConstructorClass.class, this.context);

        assertThat(builder.get().file).isNotNull();
        assertThat(builder.get().count).isNull();
    }

    @Test
    public void multiConstructor_favorFewerParams()
    {
        ObjectBuilder<MultiConstructorClass> builder = new ObjectBuilder<>(MultiConstructorClass.class, this.context);

        MultiConstructorClass obj = builder.get();

        assertThat(obj.file).isNotNull();
        assertThat(obj.count).isNull();
    }

    @Test
    public void multiConstructor_favorDefault()
    {
        ObjectBuilder<MixedDefaultConstructorClass> builder = new ObjectBuilder<>(MixedDefaultConstructorClass.class, this.context);

        MixedDefaultConstructorClass obj = builder.get();

        assertThat(obj.file).isNull();
        assertThat(obj.count).isNotNull().isEqualTo(new Integer(42));
    }

    @Test
    public void multiConstructor_avoidRepeatedParams()
    {
        ObjectBuilder<RepeatedParamConstructorClass> builder = new ObjectBuilder<>(RepeatedParamConstructorClass.class, this.context);

        RepeatedParamConstructorClass obj = builder.get();

        assertThat(obj.file).isNotNull();
        assertThat(obj.stream).isNotNull();
        assertThat(obj.count).isNotNull().isEqualTo(new Integer(42));
    }

}
