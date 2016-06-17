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

import com.google.common.base.Objects;

/**
 * The {@code DefaultContextToken} class provides base implementations for the {@link PortalContextToken}
 * interface, including some behaviors that should simplify implementations.
 *
 * @author jeff@darkware.org
 * @since 2016-06-12
 */
public abstract class DefaultContextToken implements PortalContextToken
{
    private String key;

    @Override
    public String getTokenKey()
    {
        if (this.key == null) this.key = this.generateKey();

        return this.key;
    }

    /**
     * Generate the token key. This must be identical for all tokens that intend to use the same {@link PortalContext},
     * and unique across all disparate contexts.
     *
     * @return The token key as a {@code String}.
     */
    protected abstract String generateKey();

    @Override
    public int compareTo(final PortalContextToken portalContextToken)
    {
        return PortalContextToken.super.compareTo(portalContextToken);
    }

    @Override
    public String toString()
    {
        return this.getTokenKey();
    }

    @Override
    public final boolean equals(final Object that)
    {
        if (that == null) return false;
        if (that instanceof PortalContextToken)
        {
            PortalContextToken thatToken = (PortalContextToken)that;
            return (this.getTokenKey().equals(thatToken.getTokenKey()));
        }

        return false;
    }

    @Override
    public final int hashCode()
    {
        return Objects.hashCode(this.getTokenKey());
    }
}
