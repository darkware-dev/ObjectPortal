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

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for the {@link PortalContextToken} interface and the base implementations.
 *
 * @author jeff@darkware.org
 * @since 2016-06-16
 */
public class PortalContextTokenTests
{
    @Test
    public void compare_same()
    {
        SimpleContextToken a = new SimpleContextToken("A");

        assertThat(a).isEqualTo(a);
        assertThat(a).isEqualByComparingTo(a);
    }

    @Test
    public void compare_similarAndEqual()
    {
        SimpleContextToken a = new SimpleContextToken("A");
        SimpleContextToken b = new SimpleContextToken("A");

        assertThat(a).isEqualTo(b);
        assertThat(a).isEqualByComparingTo(b);
    }

    @Test
    public void compare_notEqual()
    {
        SimpleContextToken a = new SimpleContextToken("A");
        SimpleContextToken b = new SimpleContextToken("B");

        assertThat(a).isNotEqualTo(b);
        assertThat(a).isNotEqualByComparingTo(b);
    }

    @Test
    public void compare_differentType()
    {
        SimpleContextToken a = new SimpleContextToken("A");
        String b = a.getTokenKey();

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    public void compare_compareNull()
    {
        SimpleContextToken a = new SimpleContextToken("A");

        assertThat(a).isNotEqualTo(null);
    }

    @Test
    public void key_usedByToString()
    {
        SimpleContextToken a = new SimpleContextToken("key");

        assertThat(a.getTokenKey()).isEqualTo(a.toString());
    }
}
