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
import org.darkware.objportal.error.NoRegisteredInstanceError;
import org.darkware.objportal.error.ObjectCreationError;
import org.darkware.objportal.examples.SimpleTestClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for teh {@link ThreadLocalPortalProvider}.
 *
 * @author jeff@darkware.org
 * @since 2016-06-13
 */
public class ThreadLocalProviderTests
{
    @Test
    public void happyPath_inject() throws InterruptedException
    {
        ThreadLocalPortalProvider provider = new ThreadLocalPortalProvider();
        provider.enableContextInheritance(true);

        ObjectPortal.useProvider(provider);

        // Place an instance in the current thread context
        ObjectPortal.place(Integer.class, 42);

        // Create a new thread which expects the same value
        SimpleTestThread check = new SimpleTestThread();

        check.run();
        check.join();

        assertEquals(new Integer(42), check.answer);
    }

    @Test
    public void inject_inheritance_simple() throws InterruptedException
    {
        ThreadLocalPortalProvider provider = new ThreadLocalPortalProvider();
        provider.enableContextInheritance(true);

        ObjectPortal.useProvider(provider);

        // Place an instance in the current thread context
        ObjectPortal.place(Integer.class, 42);

        // Create a new thread which expects the same value
        SimpleTestThread check = new SimpleTestThread();
        SimpleTestThread checkChild = new SimpleTestThread();

        check.thenRun(checkChild);

        check.run();
        check.join();

        checkChild.join();

        assertEquals(new Integer(42), check.answer);
        assertEquals(new Integer(42), checkChild.answer);
    }

    @Test
    public void inject_inheritance_modifyInParent() throws InterruptedException
    {
        ThreadLocalPortalProvider provider = new ThreadLocalPortalProvider();
        provider.enableContextInheritance(true);

        ObjectPortal.useProvider(provider);

        // Place an instance in the current thread context
        ObjectPortal.place(Integer.class, 42);

        // Create a new thread which expects the same value
        SimpleTestThread check = new SimpleTestThread();
        SimpleTestThread checkChild = new SimpleTestThread();

        check.setHook("beforeFetch", () -> ObjectPortal.place(Integer.class, 99));

        check.thenRun(checkChild);

        // Ensure the ObjectPortal (still the current thread) contains the updated value
        assertEquals(42, ObjectPortal.take(Integer.class).intValue());

        check.run();
        check.join();

        checkChild.join();

        assertEquals(new Integer(99), check.answer);
        assertEquals(new Integer(99), checkChild.answer);

        // Ensure the ObjectPortal (still the current thread) contains the updated value
        assertEquals(99, ObjectPortal.take(Integer.class).intValue());
    }

    @Test
    public void inject_inheritance_modifyInChild() throws InterruptedException
    {
        ThreadLocalPortalProvider provider = new ThreadLocalPortalProvider();
        provider.enableContextInheritance(true);

        ObjectPortal.useProvider(provider);

        // Place an instance in the current thread context
        ObjectPortal.place(Integer.class, 42);

        // Create a new thread which expects the same value
        SimpleTestThread check = new SimpleTestThread();
        SimpleTestThread checkChild = new SimpleTestThread();

        checkChild.setHook("beforeFetch", () -> ObjectPortal.place(Integer.class, 99));

        check.thenRun(checkChild);

        // Ensure the ObjectPortal (still the current thread) contains the updated value
        assertEquals(42, ObjectPortal.take(Integer.class).intValue());

        check.run();
        check.join();

        checkChild.join();

        assertEquals(new Integer(42), check.answer);
        assertEquals(new Integer(99), checkChild.answer);

        // Ensure the ObjectPortal (still the current thread) contains the updated value
        assertEquals(99, ObjectPortal.take(Integer.class).intValue());
    }

    @Test
    public void inject_inheritance_disownedParent() throws InterruptedException
    {
        ThreadLocalPortalProvider provider = new ThreadLocalPortalProvider();
        provider.enableContextInheritance(true);

        ObjectPortal.useProvider(provider);

        // Place an instance in the current thread context
        ObjectPortal.place(Integer.class, 42);

        // Create a new thread which expects the same value
        SimpleTestThread check = new SimpleTestThread();
        SimpleTestThread checkChild = new SimpleTestThread();

        check.thenRun(checkChild);
        checkChild.requestNewContext = true;
        checkChild.updateValue = 99;

        PortalContextToken token_before = ObjectPortal.getDefaultContextToken();
        PortalContext context_before = provider.getPortalContext();

        // Ensure the ObjectPortal (still the current thread) contains the updated value
        assertEquals(42, ObjectPortal.take(Integer.class).intValue());

        check.start();
        check.join();

        assertEquals(new Integer(42), check.answer);
        assertEquals(new Integer(99), checkChild.answer);

        assertEquals(token_before, ObjectPortal.getDefaultContextToken());
        assertSame(context_before, provider.getPortalContext());

        // Ensure the ObjectPortal (still the current thread) contains the original value
        assertEquals(42, ObjectPortal.take(Integer.class).intValue());
    }

    @Test
    public void inject_noInheritance_fail() throws InterruptedException
    {
        ThreadLocalPortalProvider provider = new ThreadLocalPortalProvider();
        provider.enableContextInheritance(false);

        ObjectPortal.useProvider(provider);

        // Place an instance in the current thread context
        ObjectPortal.place(Integer.class, 42);

        // Create a new thread which expects the same value
        SimpleTestThread check = new SimpleTestThread();

        check.start();
        check.join();

        assertNotNull(check.fetchError);
        assertEquals(ObjectCreationError.class, check.fetchError.getClass());
        assertEquals(NoRegisteredInstanceError.class, check.fetchError.getCause().getClass());

        // Ensure the ObjectPortal (still the current thread) contains the original value
        assertEquals(42, ObjectPortal.take(Integer.class).intValue());
    }

    @Test
    public void inject_noInheritance_proveDiff() throws InterruptedException
    {
        ThreadLocalPortalProvider provider = new ThreadLocalPortalProvider();
        provider.enableContextInheritance(false);

        ObjectPortal.useProvider(provider);

        // Place an instance in the current thread context
        ObjectPortal.place(Integer.class, 42);

        // Create a new thread which expects the same value
        SimpleTestThread check = new SimpleTestThread();
        SimpleTestThread checkChild = new SimpleTestThread();

        check.thenRun(checkChild);
        checkChild.updateValue = 99;

        check.start();
        check.join();

        assertNotNull(check.fetchError);
        assertEquals(ObjectCreationError.class, check.fetchError.getClass());
        assertEquals(NoRegisteredInstanceError.class, check.fetchError.getCause().getClass());

        assertEquals(new Integer(99), checkChild.answer);

        // Ensure the ObjectPortal (still the current thread) contains the original value
        assertEquals(42, ObjectPortal.take(Integer.class).intValue());
    }

    @Test(expected = DefaultTokenRefusedException.class)
    public void inject_incompatibleToken()
    {
        ThreadLocalPortalProvider provider = new ThreadLocalPortalProvider();
        provider.enableContextInheritance(false);

        provider.useDefaultToken(new SimpleContextToken("FOO"));
    }

    public static class SimpleTestThread extends Thread
    {
        // The resulting answer of the injected object
        public Integer answer;
        private Thread postRun;
        public boolean requestNewContext = false;
        public Integer updateValue = null;
        public PortalContextToken token;
        public Throwable fetchError = null;

        private Map<String, Runnable> hooks = new HashMap<>();

        @Override
        public void run()
        {
            this.runHook("beforeContextInit");
            if (this.requestNewContext)
            {
                this.token = ObjectPortal.requestNewContext();
                ObjectPortal.useDefaultToken(this.token);
            }
            this.token = ObjectPortal.getDefaultContextToken();
            if (this.updateValue != null)
            {
                ObjectPortal.place(Integer.class, this.updateValue);
            }
            this.runHook("afterContextInit");

            this.runHook("beforeFetch");
            this.fetchAnswer();
            this.runHook("afterFetch");

            if (this.postRun != null)
            {
                try
                {
                    this.postRun.start();
                    this.postRun.join();
                }
                catch (InterruptedException e)
                {
                    // Do nothing
                }
            }
            this.runHook("childDone");
            this.runHook("end");
        }

        /**
         * Set an action to execute for a specific event in the thread lifecycle.
         *
         * @param name The name of the hook
         * @param action The action to execute.
         */
        public void setHook(final String name, final Runnable action)
        {
            this.hooks.put(name, action);
        }

        /**
         * Executes the action associated with the given hook name.
         *
         * @param hook The name of the hook.
         */
        private void runHook(final String hook)
        {
            if (this.hooks.containsKey(hook)) this.hooks.get(hook).run();
        }

        /** Fetch the answer value. */
        protected void fetchAnswer()
        {
            try
            {
                SimpleTestClass test1 = ObjectPortal.newInstance(SimpleTestClass.class);
                this.answer = test1.getAnswer();
            }
            catch (Throwable t)
            {
                this.fetchError = t;
            }
        }

        /**
         * Register a thread to run at the end of the normal run of this thread.
         *
         * @param thread The thread to run.
         */
        public void thenRun(final Thread thread)
        {
            this.postRun = thread;
        }
    }
}
