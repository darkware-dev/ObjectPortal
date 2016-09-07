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

import java.util.concurrent.atomic.AtomicReference;

/**
 * The {@code BasePortalProvider} provides some base default implementations of the {@link PortalProvider}
 * interface. It should be fairly safe for almost any {@code PortalProvider} object to extend this base class.
 *
 * @author jeff@darkware.org
 * @since 2016-09-07
 */
public abstract class BasePortalProvider implements PortalProvider
{
    protected AtomicReference<PortalContext> defaultDelegate;

    public BasePortalProvider()
    {
        super();
        this.defaultDelegate = new AtomicReference<>();
    }

    public void useDefaultDelegate(final PortalContext context)
    {
        this.defaultDelegate.set(context);
    }

    public PortalContext getDefaultDelegate()
    {
        return this.defaultDelegate.get();
    }

    /**
     * Create a new {@link PortalContext} suitable for the given token.
     *
     * @param token The {@link PortalContextToken} which will be associated with the new context.
     * @return A new {@link PortalContext} object.
     */
    protected PortalContext createContext(final PortalContextToken token)
    {
        PortalContext context = this.instantiateNewContext(token);

        context.useDelegate(this.getDefaultDelegate());

        return context;
    }

    /**
     * Instantiate a new {@link PortalContext} suitable for the given token. Subclasses are required to implement
     * this method. It's expected that a base class will handle higher level creation routines.
     *
     * @param token The {@link PortalContextToken} which will be associated with the new context.
     * @return A new {@link PortalContext} object.
     */
    protected abstract PortalContext instantiateNewContext(final PortalContextToken token);
}
