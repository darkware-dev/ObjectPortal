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
 * This is a very simple {@link PortalProvider} which uses a single, global context for all
 * requests. In this implementation, {@link PortalContextToken}s are essentially ignored, as they
 * will all resolve to the same {@link PortalContext}.
 *
 * @author jeff@darkware.org
 * @since 2016-06-12
 */
public class SingletonPortalProvider implements PortalProvider
{
    /** The active {@link PortalContext}. */
    private PortalContext context;
    private PortalContextToken defaultToken;

    /**
     * Creates a new provider with the given {@link PortalContext}. This context will be used
     * for all token requests.
     *
     * @param initialContext The initial context to use.
     * @throws IllegalArgumentException If the initial context is {@code null}.
     */
    public SingletonPortalProvider(final PortalContext initialContext)
    {
        super();

        this.defaultToken = new SimpleContextToken("singleton");

        if (initialContext == null) throw new IllegalArgumentException("Initial context cannot be null.");
        this.changeContext(initialContext);
    }

    /**
     * Creates a new provider with a default, empty {@link PortalContext}.
     */
    public SingletonPortalProvider()
    {
        this(new SimplePortalContext());
    }

    /**
     * Changes the current {@link PortalContext}. The effect is immediate, but will not block against threads that are
     * currently resolving dependencies against a different context.
     *
     * @param newContext The new {@link PortalContext} to use.
     */
    public void changeContext(final PortalContext newContext)
    {
        this.context = newContext;
    }


    @Override
    public PortalContext getPortalContext(final PortalContextToken token)
    {
        return this.getPortalContext();
    }

    @Override
    public PortalContext getPortalContext()
    {
        return this.context;
    }

    @Override
    public PortalContextToken getDefaultToken()
    {
        return this.defaultToken;
    }

    @Override
    public PortalContextToken requestNewContext()
    {
        return this.defaultToken;
    }

    @Override
    public void useDefaultToken(final PortalContextToken defaultToken)
    {
        // Do nothing.
    }
}
