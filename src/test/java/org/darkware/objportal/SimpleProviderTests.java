/*----------------------------------------------------------------------------------------------
 Copyright (c) 2016. darkware.org and contributors

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ---------------------------------------------------------------------------------------------*/

package org.darkware.objportal;

import org.darkware.objportal.examples.SimpleTestClass;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * These are tests for the {@link SimplePortalProvider} class.
 *
 * @author jeff@darkware.org
 * @since 2016-06-16
 */
public class SimpleProviderTests
{
    protected SimplePortalProvider provider;

    @Before
    public void init()
    {
        this.provider = new SimplePortalProvider();
        this.provider.getPortalContext().place(Integer.class, 42);
    }

    @Test
    public void inject_defaultContext()
    {
        SimpleTestClass testObj = this.provider.getPortalContext().newInstance(SimpleTestClass.class);

        assertThat(testObj.getAnswer()).isEqualTo(42);
    }

    @Test
    public void inject_newContext()
    {
        PortalContextToken token = this.provider.requestNewContext();
        this.provider.getPortalContext(token).place(Integer.class, 99);

        SimpleTestClass testObj = this.provider.getPortalContext(token).newInstance(SimpleTestClass.class);

        assertThat(testObj.getAnswer()).isEqualTo(99);
    }

    @Test
    public void context_requestNew()
    {
        PortalContextToken defToken = this.provider.getDefaultToken();

        PortalContextToken token = this.provider.requestNewContext();
        this.provider.getPortalContext(token).place(Integer.class, 99);

        assertThat(defToken).isNotEqualByComparingTo(token);
        assertThat(this.provider.getPortalContext(token)).isNotSameAs(this.provider.getPortalContext(defToken));

    }

    @Test
    public void context_changeDefault()
    {
        PortalContextToken token = this.provider.requestNewContext();

        this.provider.getPortalContext(token).place(Integer.class, 99);

        assertThat(this.provider.getPortalContext().take(Integer.class)).isEqualTo(42);

        this.provider.useDefaultToken(token);

        assertThat(this.provider.getPortalContext().take(Integer.class)).isEqualTo(99);
    }

}
