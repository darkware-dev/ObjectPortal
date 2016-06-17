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
 * This {@link Exception} is required to be thrown if a {@link PortalProvider} chooses to refuse a request to change
 * its default {@link PortalContextToken}.
 *
 * @author jeff@darkware.org
 * @since 2016-06-14
 */
public class DefaultTokenRefusedException extends RuntimeException
{
    /**
     * Create a new exception reporting a {@link PortalProvider}'s refusal to accept a
     * {@link PortalProvider#useDefaultToken(PortalContextToken)} request.
     *
     * @param reason The reason the request was refused.
     */
    public DefaultTokenRefusedException(final String reason)
    {
        super(reason);
    }

    /**
     * Create a new exception reporting a {@link PortalProvider}'s inability or refusal to accept a
     * {@link PortalProvider#useDefaultToken(PortalContextToken)} request.
     *
     * @param reason The reason the request was refused.
     * @param cause The {@link Throwable} error associated which caused the refusal.
     */
    public DefaultTokenRefusedException(final String reason, final Throwable cause)
    {
        super(reason, cause);
    }
}
