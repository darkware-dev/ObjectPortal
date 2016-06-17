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

import org.darkware.objportal.PortalContextToken;
import org.darkware.objportal.PortalProvider;

/**
 * An {@code UnrecognizedTokenException} is thrown when a {@link PortalProvider} encounters a token that it doesn't
 * recognize and it lacks the ability or configuration to automatically create a new context.
 *
 * @author jeff@darkware.org
 * @since 2016-06-12
 */
public class UnrecognizedTokenException extends RuntimeException
{
    /**
     * Create a new {@code UnrecognizedTokenException} reporting the token that wasn't recognized.
     *
     * @param token The unrecognized token.
     */
    public UnrecognizedTokenException(final PortalContextToken token)
    {
        super("Unrecognized token: " + token.getClass().getName() + "::" + token.getTokenKey());
    }
}
