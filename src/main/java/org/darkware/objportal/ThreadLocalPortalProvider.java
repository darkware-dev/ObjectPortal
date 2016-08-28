/*----------------------------------------------------------------------------------------------
 Copyright (c) 2016. darkware.org and contributors

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ---------------------------------------------------------------------------------------------*/

package org.darkware.objportal;

import org.darkware.objportal.error.DefaultTokenRefusedException;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@code ThreadLocalPortalProvider} is a {@link PortalProvider} which assigns a soft linkage between running
 * {@link Thread}s and {@link PortalContextToken}s. When retrieving the default {@link PortalContext}, the
 * {@code PortalProvider} will implicitly use a token linked to the currently running thread. The provider may
 * optionally enable a mode where threads inherit the token used by their parent threads. The resulting behavior is
 * the same, just with a wider scope to the contextual sharing.
 * <p>
 * The primary result of this behavior is that the provider will use a default context which is shared across all
 * objects created within a single thread or tree of threads. This can be particularly useful for multi-threaded
 * applications which wish to share state among threads or thread groups without resorting to complex storage classes
 * in thread-local storage.
 * <p>
 * <em>Note:</em> It's important to remember that the shared context exists strictly within the thread that
 * <em>performs object creation</em>, not actual execution. In order to fully
 *
 * @author jeff@darkware.org
 * @since 2016-06-12
 */
public class ThreadLocalPortalProvider extends TokenizedPortalProvider
{
    private boolean inheritContext;
    private ThreadLocalTokenStore tokenStore = new ThreadLocalTokenStore();

    /**
     * Create a new {@link PortalProvider} that automatically assigns and retrieves contexts to Threads.
     */
    public ThreadLocalPortalProvider()
    {
        super();

        this.inheritContext = false;
        this.enableAutoCreation(true);
    }

    /**
     * Declares whether child threads automatically inherit the {@link PortalContextToken} of their parent thread.
     *
     * @param value {@code true} if threads inherit their parents' tokens, {@code false} if new threads automatically
     * generate new tokens.
     */
    public void enableContextInheritance(final boolean value)
    {
        this.inheritContext = value;
    }

    /**
     * Fetch the {@link PortalContextToken} for the current thread. This is used as the default token for resolving the
     * local {@link PortalContext}.
     *
     * @return A {@link PortalContextToken}.
     */
    protected synchronized PortalContextToken getCurrentThreadToken()
    {
        ThreadLocalContextToken token = this.tokenStore.get();

        if (token == null)
        {
            token = (ThreadLocalContextToken)this.requestNewContext();
            this.tokenStore.set(token);
        }
        return token;
    }

    @Override
    public PortalContextToken getDefaultToken()
    {
        return this.getCurrentThreadToken();
    }

    @Override
    public PortalContextToken requestNewContext()
    {
        PortalContextToken token = new ThreadLocalContextToken();
        return token;
    }

    @Override
    public void useDefaultToken(final PortalContextToken defaultToken)
    {
        try
        {
            this.tokenStore.set((ThreadLocalContextToken)defaultToken);
        }
        catch (ClassCastException e)
        {
            throw new DefaultTokenRefusedException("The token was not a compatible type.", e);
        }
    }

    /**
     * This is a extension of the {@link InheritableThreadLocal} object with special behaviors set up to handle the
     * optional inheritance of the {@link PortalContextToken} for child threads.
     */
    public class ThreadLocalTokenStore extends InheritableThreadLocal<ThreadLocalContextToken>
    {
        @Override
        protected ThreadLocalContextToken childValue(final ThreadLocalContextToken threadLocalContextToken)
        {
            if (ThreadLocalPortalProvider.this.inheritContext)
            {
                return threadLocalContextToken;
            }
            else return null;
        }
    }

    /**
     * This is the implementation of {@link PortalContextToken} which is used for resolving {@link PortalContext}s
     * within a {@link ThreadLocalPortalProvider}.
     */
    public static class ThreadLocalContextToken extends DefaultContextToken
    {
        private static final AtomicLong nextId = new AtomicLong(1L);

        private final long contextThreadId;

        /**
         * Create a new token.
         */
        public ThreadLocalContextToken()
        {
            super();

            this.contextThreadId = ThreadLocalContextToken.nextId.getAndIncrement();
        }

        @Override
        protected String generateKey()
        {
            return "Thread-" + this.contextThreadId;
        }
    }
}
