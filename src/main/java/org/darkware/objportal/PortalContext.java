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

import org.darkware.objportal.error.NoRegisteredInstanceError;

import java.util.function.Supplier;

/**
 * The {@code InstancePortal} interface declares the set of methods used for retrieving the concrete
 * instances of objects stored inside an {@link ObjectPortal}.
 *
 * @author jeff@darkware.org
 * @since 2016-06-09
 */
public interface PortalContext
{
    /**
     * Creates a new concrete instance of the given class. This requires that a suitable default (parameterless)
     * constructor exists for the class.
     *
     * @param instanceClass The class of object to createContext.
     * @param <T> The parameterized type of the class.
     * @return A new concrete instance of the given class.
     */
    <T> T newInstance(Class<T> instanceClass);

    /**
     * Supplies values for fields declared as injection targets. The objects available for injection are limited to
     * just this context.
     *
     * @param object The object to inject values into.
     */
    void autoInject(Object object);

    /**
     * Checks to see if the given class has a suitable instance registered.
     *
     * @param queryClass The class to search for an instance of.
     * @return {@code true} if a suitably-matching class instance is available
     */
    boolean hasInstance(Class<?> queryClass);

    /**
     * Fetch a constructed instance of the given class. The actual object returned may or may not be of the
     * exact type specified.
     *
     * @param queryClass The class to fetch.
     * @param <T> The declared type of the object to return.
     * @return An object of the queried class.
     * @throws NoRegisteredInstanceError If no objects of the given class were available.
     */
    @SuppressWarnings("unchecked")
    <T> T take(Class<T> queryClass);

    /**
     * Register an object in the portal. This will declare the object to be returned when the supplied
     * class is queried.
     *
     * @param instanceClass The {@link Class} to place the instance for.
     * @param object The instance object to place.
     * @param <T> The type of the object being registered.
     */
    <T> void place(Class<T> instanceClass, T object);

    /**
     * Register an object in the portal. This will declare a {@link Supplier} method which can createContext an
     * instance of the given class when an object is needed. The {@code Supplier} will only be called
     * the first time an instance is requested. Thus, the cost of creating an object can be delayed or
     * avoided altogether if an instance is not needed immediately.
     *
     * @param instanceClass The {@link Class} to place the instance for.
     * @param supplier A {@link Supplier} which can createContext objects of the given class.
     * @param <T> The type of the object being registered.
     */
    <T> void place(Class<T> instanceClass, Supplier<? extends T> supplier);
}
