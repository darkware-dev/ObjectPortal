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

/**
 * A {@code PortalContextToken} is a (potentially) simple token which can be used to uniquely
 * identify a
 *
 * @author jeff@darkware.org
 * @since 2016-06-12
 */
public interface PortalContextToken extends Comparable<PortalContextToken>
{
    /**
     * Fetch the identification key for this token. This should uniquely identify the token.
     *
     * @return The key as a {@code String}.
     */
    String getTokenKey();

    @Override
    default int compareTo(PortalContextToken portalContextToken)
    {
        return this.getTokenKey().compareTo(portalContextToken.getTokenKey());
    }
}
