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

import org.darkware.objportal.error.InjectionError;
import org.darkware.objportal.error.NoRegisteredInstanceError;
import org.darkware.objportal.error.ObjectCreationError;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * This is a {@link PortalContext} which uses very simple local storage of instances.
 *
 * @author jeff@darkware.org
 * @since 2016-06-09
 */
public class SimplePortalContext implements PortalContext
{
    private final Map<Class, Object> objectStore;
    private final Map<Class, Supplier> supplierStore;

    /**
     * Creates a new implementation of {@link PortalContext} that uses simple object storage which
     * allows a given object to place for multiple types.
     */
    public SimplePortalContext()
    {
        super();

        this.objectStore = new ConcurrentHashMap<>();
        this.supplierStore = new ConcurrentHashMap<>();
    }

    @Override
    public <T> T newInstance(final Class<T> instanceClass)
    {
        try
        {
            final T instance = instanceClass.newInstance();
            this.autoInject(instance);

            return instance;
        }
        catch (InstantiationException e)
        {
            throw new ObjectCreationError("Error while creating a new " + instanceClass.getName(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new ObjectCreationError("Cannot access the default constructor for " + instanceClass.getName(), e);
        }
        catch (Exception e)
        {
            throw new ObjectCreationError("Exception while creating a new " + instanceClass.getName(), e);
        }
        catch (Throwable t)
        {
            throw new ObjectCreationError("Critical runtime error while creating a new " + instanceClass.getName(), t);
        }
    }

    @Override
    public void autoInject(final Object object)
    {
        try
        {
            Class<?> objectClass = object.getClass();

            for (final Field field : ObjectPortal.getInjectableFields(objectClass))
            {
                Class<?> fieldClass = field.getType();
                field.setAccessible(true);
                field.set(object, this.take(fieldClass));
            }
        }
        catch (IllegalAccessException e)
        {
            throw new InjectionError("Error while trying to inject dependencies.", e);
        }
    }

    @Override
    public boolean hasInstance(Class<?> queryClass)
    {
        synchronized (this.objectStore)
        {
            return this.objectStore.containsKey(queryClass) || this.supplierStore.containsKey(queryClass);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T take(Class<T> queryClass)
    {
        synchronized (this.objectStore)
        {
            // Try to fetch a previously created object
            if (this.objectStore.containsKey(queryClass)) return (T) this.objectStore.get(queryClass);

            // Try to fetch a new object based on a stored Supplier
            if (this.supplierStore.containsKey(queryClass))
            {
                T instanceObject = (T)this.supplierStore.get(queryClass).get();
                this.place(queryClass, instanceObject);

                return instanceObject;
            }
        }

        throw new NoRegisteredInstanceError(queryClass);
    }

    @Override
    public <T> void place(final Class<T> instanceClass, T object)
    {
        this.objectStore.put(instanceClass, object);
    }

    @Override
    public <T> void place(final Class<T> instanceClass, Supplier<? extends T> supplier)
    {
        this.supplierStore.put(instanceClass, supplier);
    }
}
