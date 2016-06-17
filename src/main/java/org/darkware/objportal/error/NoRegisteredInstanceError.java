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

package org.darkware.objportal.error;

import org.darkware.objportal.PortalContext;

/**
 * This exception is thrown whenever an {@link PortalContext} fails to resolve a requested
 * instance because no instance was registered under the requested {@link Class} or a
 * suitable alias.
 *
 * @author jeff@darkware.org
 * @since 2016-06-09
 */
public class NoRegisteredInstanceError extends RuntimeException
{
    /**
     * Creates a new error reporting a failure to resolve a class instance.
     *
     * @param queryClass The class which was queried.
     */
    public NoRegisteredInstanceError(final Class<?> queryClass)
    {
        super("No registered instance found for class: " + queryClass.getName());
    }
}
