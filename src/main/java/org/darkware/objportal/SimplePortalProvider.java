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

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.util.Base64;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This is a simple {@link PortalProvider} which supports multiple {@link PortalContext}s selectable by configurable
 * tokens, along with a single shared default context.
 * <p>
 * Effectively, this is the "default" implementation of a {@link PortalProvider}, supporting all of the core features
 * of the library in ways that are meant to feel natural.
 *
 * @author jeff@darkware.org
 * @since 2016-06-15
 */
public class SimplePortalProvider extends TokenizedPortalProvider
{
    private static final AtomicLong nextId = new AtomicLong(System.currentTimeMillis());

    private PortalContextToken defaultToken;

    /**
     * Create a new {@link PortalProvider} with a default, empty {@link PortalContext}.
     */
    public SimplePortalProvider()
    {
        super();

        this.defaultToken = this.requestNewContext();
        PortalContext context = this.createContext(this.defaultToken);

        this.enableAutoCreation(true);
        this.registerContext(this.defaultToken, context);
        this.useDefaultToken(this.defaultToken);
    }

    @Override
    public final PortalContextToken getDefaultToken()
    {
        return this.defaultToken;
    }

    @Override
    public PortalContextToken requestNewContext()
    {
        return new SimpleContextToken(Base64.getEncoder().encodeToString(Longs.toByteArray(SimplePortalProvider.nextId.getAndIncrement())));
    }

    @Override
    public void useDefaultToken(final PortalContextToken defaultToken)
    {
        this.defaultToken = defaultToken;
    }
}
