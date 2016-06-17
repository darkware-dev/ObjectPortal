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

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jeff@darkware.org
 * @since 2016-06-09
 */
public final class ObjectPortal
{
    private static PortalProvider portalProvider;

    /**
     * Set the {@link PortalProvider} to use when resolving {@link PortalContext}s for {@link Inject}ing dependencies.
     *
     * @param provider The {@link PortalProvider} to use.
     */
    public static void useProvider(final PortalProvider provider)
    {
        ObjectPortal.portalProvider = provider;
    }

    /**
     * Creates a new concrete instance of the given class, resolving injection requests against the . This requires that a suitable default (parameterless)
     * constructor exists for the class.
     *
     * @param instanceClass The class of object to createContext.
     * @param <T> The parameterized type of the class.
     * @return A new concrete instance of the given class.
     */
    public static <T> T newInstance(final Class<T> instanceClass)
    {
        return ObjectPortal.portalProvider.getPortalContext().newInstance(instanceClass);
    }

    /**
     * Creates a new concrete instance of the given class. This requires that a suitable default (parameterless)
     * constructor exists for the class.
     *
     * @param token The token identifying the context to resolve the object against.
     * @param instanceClass The class of object to createContext.
     * @param <T> The parameterized type of the class.
     * @return A new concrete instance of the given class.
     */
    public static <T> T newInstance(final PortalContextToken token, final Class<T> instanceClass)
    {
        return ObjectPortal.portalProvider.getPortalContext(token).newInstance(instanceClass);
    }

    /**
     * Automatically injects dependencies from the default {@link PortalContext}.
     * <p>
     * Injection behavior is delegated to and dictated by the resulting {@link PortalContext}.
     *
     * @param object The object to inject dependencies into.
     * @see PortalContext#autoInject(Object)
     */
    public static void autoInject(final Object object)
    {
        ObjectPortal.portalProvider.getPortalContext().autoInject(object);
    }

    /**
     * Automatically injects dependencies from the {@link PortalContext} associated with the given token.
     * <p>
     * Injection behavior is delegated to and dictated by the resulting {@link PortalContext}.
     *
     * @param object The object to inject dependencies into.
     * @see PortalContext#autoInject(Object)
     */
    public static void autoInject(final PortalContextToken token, final Object object)
    {
        ObjectPortal.portalProvider.getPortalContext(token).autoInject(object);
    }

    /**
     * Retrieve an instance of the given class from the default {@link PortalContext}.
     *
     * @param targetClass The type of object to retrieve.
     * @param <T> The parameterized class.
     * @return An instance of the given class.
     * @throws NoRegisteredInstanceError If there was no instance of the class available in the default context.
     */
    public static <T> T take(final Class<T> targetClass)
    {
        return ObjectPortal.portalProvider.getPortalContext().take(targetClass);
    }

    /**
     * Retrieve an instance of the given class from the {@link PortalContext} identified by the given token.
     *
     * @param token The token identifying the context to pull the value from.
     * @param targetClass The type of object to retrieve.
     * @param <T> The parameterized class.
     * @return An instance of the given class.
     * @throws NoRegisteredInstanceError If there was no instance of the class available in the default context.
     */
    public static <T> T take(final PortalContextToken token, final Class<T> targetClass)
    {
        return ObjectPortal.portalProvider.getPortalContext(token).take(targetClass);
    }

    /**
     * Place the given object in the default {@link PortalContext}. This will allow the object to be used for
     * retrieval and dependency injection via the supplied class.
     *
     * @param targetClass The class to register the object under.
     * @param value The object value to store. This must be a concrete instance of the registered class or a subclass
     * of that class.
     * @param <T> The parameterized object type.
     */
    public static <T> void place(final Class<T> targetClass, T value)
    {
        ObjectPortal.portalProvider.getPortalContext().place(targetClass, value);
    }

    /**
     * Place the given object a given {@link PortalContext} matching the token. This will allow the object to be used
     * for retrieval and dependency injection via the supplied class.
     *
     * @param token The token identifying the {@link PortalContext} to place the value into.
     * @param targetClass The class to register the object under.
     * @param value The object value to store. This must be a concrete instance of the registered class or a subclass
     * of that class.
     * @param <T> The parameterized object type.
     */
    public static <T> void place(final PortalContextToken token, final Class<T> targetClass, T value)
    {
        ObjectPortal.portalProvider.getPortalContext(token).place(targetClass, value);
    }

    /**
     * Request that the current {@link PortalProvider} generate a new local {@link PortalContext} with an associated
     * {@link PortalContextToken} for addressing it. The {@code PortalProvider} is allowed to ignore this request, or
     * to generate a token which points to a pre-existing context.
     *
     * @return A {@link PortalContextToken} addressing an active {@link PortalContext}. There is no guarantee that
     * either the token or the context are new or different than the currently active context.
     */
    public static PortalContextToken requestNewContext()
    {
        return ObjectPortal.portalProvider.requestNewContext();
    }

    /**
     * Fetch a {@link PortalContextToken} for the current default {@link PortalContext}.
     * <p>
     * This token is valid only for the current {@link PortalProvider}.
     *
     * @return
     */
    public static PortalContextToken getDefaultContextToken()
    {
        return ObjectPortal.portalProvider.getDefaultToken();
    }

    /**
     * Request that the provider adopt a new default {@link PortalContextToken}. This may implicitly create a new
     * {@link PortalContext} if an associated context does not already exist.
     *
     * @throws DefaultTokenRefusedException If the provider chooses to refuse the request for any reason.
     * @see PortalProvider#useDefaultToken(PortalContextToken)
     */
    public static void useDefaultToken(final PortalContextToken defaultToken)
    {
        ObjectPortal.portalProvider.useDefaultToken(defaultToken);
    }

    /**
     * Fetch the fields which have registered for dependency injection on the given class. This searches the class and
     * all ancestor classes.
     *
     * @param objectClass The class to search
     * @return A {@link Set} of {@link Field}s which are marked for injection.
     */
    protected static Set<Field> getInjectableFields(final Class<?> objectClass)
    {
        //TODO: Cache this?
        Set<Field> fields = new HashSet<>();
        if (objectClass.equals(Object.class)) return fields;

        // Check all the fields in this class
        for (final Field field : objectClass.getDeclaredFields())
        {
            if (field.getDeclaredAnnotation(Inject.class) != null) fields.add(field);
        }
        fields.addAll(ObjectPortal.getInjectableFields(objectClass.getSuperclass()));

        return fields;
    }
}
