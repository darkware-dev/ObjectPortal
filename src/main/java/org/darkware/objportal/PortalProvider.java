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

import org.darkware.objportal.error.DefaultTokenRefusedException;
import org.darkware.objportal.error.UnrecognizedTokenException;

/**
 * A {@code PortalProvider} is a provider object that functions like something of a combination
 * of a Factory and a Lookup Facade. It is responsible for providing the current active
 * {@link PortalContext} for use to the global {@link ObjectPortal}, and is thus capable of
 * modifying the behavior of the {@code ObjectPortal} based on source, context or configuration.
 *
 * @author jeff@darkware.org
 * @since 2016-06-12
 */
public interface PortalProvider
{
    /**
     * Fetches the {@link PortalContext} for the given token.
     *
     * @param token The token used to select the active {@code PortalContext}
     * @return A {@link PortalContext}.
     * @throws UnrecognizedTokenException If the token wasn't recognized and the provider was unable or unwilling to
     * create a suitable context.
     */
    PortalContext getPortalContext(final PortalContextToken token);

    /**
     * Fetches the default {@link PortalContext}.
     * <p>
     * This should be equivalent to calling {@link #getPortalContext(PortalContextToken)} with the token returned by
     * {@link #getDefaultToken()};
     *
     * @return A {@link PortalContext}.
     */
    default PortalContext getPortalContext()
    {
        return this.getPortalContext(this.getDefaultToken());
    }

    /**
     * Fetches a token which is associated with the current default context.
     *
     * @return A {@link PortalContextToken}.
     */
    PortalContextToken getDefaultToken();

    /**
     * Requests the creation of a new {@link PortalContext} and implicitly a new {@link PortalContextToken}. This can
     * be used to request the creation of new {@code PortalContexts} for different subsystems, or possibly to request
     * creating entirely clean contexts.
     * <p>
     * It's very important to note that this issues a request without a guarantee of that request being fulfilled. Some
     * {@link PortalProvider}s may opt to refuse to build new contexts, or may base their contexts on external factors
     * which may not have changed for the calling code.
     *
     * @return A {@link PortalContextToken}, which may or may not be the same as the current token.
     */
    PortalContextToken requestNewContext();

    /**
     * Request that the provider adopt a new default {@link PortalContextToken}. This may implicitly create a new
     * {@link PortalContext} if an associated context does not already exist.
     * <p>
     * A {@code PortalProvider} is allowed refuse to honor this request entirely, or may choose to refuse the request if
     * various requirements are not met. For example, a provider may require that the token already exist, or that the
     * token be of a certain type. If the provider opts to refuse the request, it is required to throw a
     * {@link DefaultTokenRefusedException} with an explanation for why it was refused.
     *
     * @throws DefaultTokenRefusedException If the provider chooses to refuse the request for any reason.
     */
    void useDefaultToken(final PortalContextToken defaultToken);
}
