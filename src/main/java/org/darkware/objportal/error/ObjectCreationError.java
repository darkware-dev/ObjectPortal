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

import org.darkware.objportal.ObjectPortal;

import java.util.function.Supplier;

/**
 * A {@code ObjectCreationError} is thrown whenever various errors prevent an {@link ObjectPortal} or associated
 * class from creating a new concrete object instance. This can be due to various exceptions while calling a
 * class's default constructor, errors while using a provided {@link Supplier}, or internal problems with resolving
 * or handling references to the supplied class. In all cases, the originating exception will be attached to the
 * exception instance.
 *
 * @author jeff@darkware.org
 * @since 2016-06-12
 */
public class ObjectCreationError extends RuntimeException
{
    /**
     * Creates a new {@code ObjectCreationError} reporting a problem caused by another {@link Throwable}.
     *
     * @param explanation An explanation of the error or what action was happening when the error was encountered.
     * @param cause The {@link Throwable} that caused this error.
     */
    public ObjectCreationError(final String explanation, final Throwable cause)
    {
        super(explanation, cause);
    }

    /**
     * Creates a new {@code ObjectCreationError} reporting a problem that was caused and detected internally.
     *
     * @param explanation An explanation of the error or what action was happening when the error was encountered.
     */
    public ObjectCreationError(final String explanation)
    {
        super(explanation);
    }
}
