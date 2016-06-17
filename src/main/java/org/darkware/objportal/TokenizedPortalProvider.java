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

import org.darkware.objportal.error.UnrecognizedTokenException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code TokenizedPortalProvider} class provides a base implementation of a {@link PortalProvider} which uses
 * {@link PortalContextToken}s to select {@link PortalContext}s.
 *
 * @author jeff@darkware.org
 * @since 2016-06-12
 */
public abstract class TokenizedPortalProvider implements PortalProvider
{
    private final Map<PortalContextToken, PortalContext> contexts;
    private boolean autoCreate;

    /**
     * Creates a new {@code TokenizedPortalProvider} with an empty set of providers.
     */
    public TokenizedPortalProvider()
    {
        super();

        this.contexts = new ConcurrentHashMap<>();
    }

    /**
     * Declare whether this provider should automatically createContext new {@link PortalContext}s whenever an attempt is
     * made to fetch an unrecognized context.
     *
     * @param value {@code true} if contexts should be created for unrecognized {@link PortalContextToken}s,
     * {@code false} if exceptions should be thrown when unrecognized {@code PortalContextToken}s are encountered.
     */
    protected void enableAutoCreation(final boolean value)
    {
        synchronized (this.contexts)
        {
            this.autoCreate = value;
        }
    }

    @Override
    public PortalContext getPortalContext(final PortalContextToken token)
    {
        synchronized (this.contexts)
        {
            PortalContext context = this.contexts.get(token);

            if (context == null)
            {
                if (this.autoCreate)
                {
                    context = this.createContext(token);
                    this.contexts.put(token, context);
                }
                else
                {
                    throw new UnrecognizedTokenException(token);
                }
            }

            return context;
        }
    }

    /**
     * Create a new {@link PortalContext} suitable for the given token.
     *
     * @param token The {@link PortalContextToken} which will be associated with the new context.
     * @return A new {@link PortalContext} object.
     */
    @SuppressWarnings("unused")
    protected PortalContext createContext(final PortalContextToken token)
    {
        return new SimplePortalContext();
    }

    /**
     * Registers a {@link PortalContext} with the given {@link PortalContextToken}. This will replace any existing
     * context under the same token.
     *
     * @param token The token to register.
     * @param context The context to register.
     */
    protected void registerContext(final PortalContextToken token, final PortalContext context)
    {
        this.contexts.put(token, context);
    }
}
