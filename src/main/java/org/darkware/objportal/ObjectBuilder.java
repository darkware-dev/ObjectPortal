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

import org.darkware.objportal.error.ObjectCreationError;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class is a rather complex encapsulation of adaptive constructor code which exports the standardized
 * {@link Supplier} interface. The overall goal here is to turn the process of building a given class with the set of
 * available dependency objects into a proxy object which can be stored and used when needed.
 *
 * @author jeff@darkware.org
 * @since 2016-06-20
 */
public class ObjectBuilder<T> implements Supplier<T>
{
    private final Class<T> objectClass;
    private final PortalContext context;

    private Constructor<T> constructor;
    private final List<Object> param;

    private final ConstructorPrecedenceComparator conComparator = new ConstructorPrecedenceComparator();

    /**
     * Create a new builder for the given class using objects available from the supplied context.
     *
     * @param objectClass The class of object to build.
     * @param context The {@link PortalContext} to resolve dependencies against.
     */
    public ObjectBuilder(final Class<T> objectClass, final PortalContext context)
    {
        super();

        this.objectClass = objectClass;
        this.context = context;

        this.param = new ArrayList<>();
    }

    @Override
    public T get()
    {
        return this.build();
    }

    /**
     * Resets the builder, forgetting whatever it might have discovered on previous analysis passes. This method should
     * be called whenever changes in the attached {@link PortalContext} might affect the constructors available for use
     * (eg: the addition or removal of an available class type).
     */
    public void reset()
    {
        this.constructor = null;
        this.param.clear();
    }

    /**
     * Build an instance of the given object class. This will implicitly perform object analysis with reflection to
     * find a suitable way of building the class.
     *
     * @return An object of the associated type.
     */
    protected T build()
    {
        if (this.constructor == null) this.setupConstructor();
        if (this.constructor == null) throw new ObjectCreationError("No constructor available to create class: " + this.objectClass.getName());

        try
        {
            return this.constructor.newInstance(this.param.toArray());
        }
        catch (IllegalAccessException e)
        {
            throw new ObjectCreationError("Could not access the constructor for " + this.objectClass.getName() + ": " + this.constructor, e);
        }
        catch (InstantiationException e)
        {
            throw new ObjectCreationError("Unable to use constructor for " + this.objectClass.getName() + ": " + this.constructor, e);
        }
        catch (InvocationTargetException e)
        {
            throw new ObjectCreationError("Error while calling constructor for " + this.objectClass.getName() + ": " + this.constructor, e);
        }
    }

    /**
     * Attempts to discover a suitable constructor to use, then set up the list of parameters needed to call it. There
     * are numerous ways this can fail, including:
     * <ul>
     *     <li>No constructors found.</li>
     *     <li>No constructors without repeated object types.</li>
     *     <li>Insufficient objects in context for any of the constructors found.</li>
     * </ul>
     */
    protected void setupConstructor()
    {
        @SuppressWarnings("unchecked")
        Optional<Constructor<T>> result = Stream.of((Constructor<T>[])this.objectClass.getConstructors())
                                                .filter(this::canSupplyParameters)
                                                .sorted(this.conComparator)
                                                .findFirst();

        if (!result.isPresent())
        {
            throw new ObjectCreationError("Could not find a suitable constructor for: " + this.objectClass.getName());
        }

        this.constructor = result.get();
        System.out.println("Selected constructor: " + this.constructor);
        for (Class<?> paramClass : this.constructor.getParameterTypes())
        {
            this.param.add(this.context.take(paramClass));
        }
    }

    /**
     * Check to see if the currently attached {@link PortalContext} has a sufficient collection of available objects to
     * supply the parameters needed for the given {@link Constructor}.
     *
     * @param con The {@link Constructor} to analyze.
     * @return {@code true} if the context has enough objects to
     */
    protected boolean canSupplyParameters(final Constructor<T> con)
    {
        long paramCount = con.getParameterCount();

        Set<Class<?>> seen = new HashSet<>();
        Predicate<Class<?>> notYetSeen = paramClass ->
        {
            if (seen.contains(paramClass)) return false;
            else
            {
                seen.add(paramClass);
                return true;
            }
        };

        long availCount = Stream.of(con.getParameterTypes())
                                .filter(notYetSeen)
                                .filter(this.context::hasInstance)
                                .count();

        return (paramCount == availCount);
    }

    /**
     * This is a simple {@link Comparator} which does some simple trickery to provide a sort order that virtually
     * assures that constructors in a sorted collection or stream are ordered so that constructors annotated with the
     * {@link Inject} annotation come before constructors that don't have it, and constructors with fewer parameters
     * come before constructors with more parameters. The goal here is to prefer annotated constructors and constructors
     * with simpler parameter lists. When injecting dependencies, a minimal approach should be taken, favoring
     * post-construction injection over construction injection.
     */
    protected final class ConstructorPrecedenceComparator implements Comparator<Constructor<T>>
    {
        @Override
        public int compare(final Constructor<T> a, final Constructor<T> b)
        {
            int aScore = a.getParameterCount() - ((a.getAnnotation(Inject.class) != null) ? 1000 : 0);
            int bScore = b.getParameterCount() - ((b.getAnnotation(Inject.class) != null) ? 1000 : 0);
            return Integer.compare(aScore, bScore);
        }
    }
}
