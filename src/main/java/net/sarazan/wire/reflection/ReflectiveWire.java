package net.sarazan.wire.reflection;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Aaron Sarazan on 6/22/14
 * Copyright(c) 2014 Manotaur, LLC.
 */
public final class ReflectiveWire {

    private static final String TAG = "ReflectiveWire";

    private ReflectiveWire() {}

    /**
     * Attempts to intelligently merge fields of two Wire messages.
     * non-null fields will take priority over null ones.
     * TODO figure out a sane policy for lists
     * @param message the message containing updated fields
     * @param into the old message that will be updated
     * @param <M> Message subclass
     * @param <B> Builder class for {@link M}
     * @return merged Message builder, or cloned message if into is null
     */
    public static <M extends Message, B extends Message.Builder<M>>
    B mergeToBuilder(M message, M into) {
        if (into == null) return cloneToBuilder(message);
        B builder = cloneToBuilder(into);
        Class messageClass = message.getClass();
        for (Field f : messageClass.getDeclaredFields()) {
            ProtoField ann = f.getAnnotation(ProtoField.class);
            if (ann == null) continue;
            try {
                Object newValue = f.get(message);
                if (newValue == null) continue;
                if (newValue instanceof Message) {
                    newValue = merge((Message)newValue, (Message)f.get(into));
                }
                Method setter = builder.getClass().getDeclaredMethod(f.getName(), f.getType());
                setter.invoke(builder, newValue);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return builder;
    }

    /**
     * Attempts to intelligently merge fields of two Wire messages.
     * non-null fields will take priority over null ones.
     * TODO figure out a sane policy for lists
     * @param message the message containing updated fields
     * @param into the old message that will be updated
     * @param <M> Message subclass
     * @param <B> Builder class for {@link M}
     * @return merged Message, or message if into is null
     */
    public static <M extends Message, B extends Message.Builder<M>>
    M merge(M message, M into) {
        if (into == null) return message;
        return mergeToBuilder(message, into).build();
    }

    public static <M extends Message, B extends Message.Builder<M>>
    B cloneToBuilder(M message) {
        try {
            Class messageClass = message.getClass();
            String messageClassName = message.getClass().getName();
            String builderClassName = messageClassName + "$Builder";
            Class<B> builderClass = (Class<B>) Class.forName(builderClassName);
            Constructor<B> ctor = builderClass.getDeclaredConstructor(messageClass);
            return ctor.newInstance(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <R extends Message> Field getFieldByTag(R message, int tag) {
        if (message == null) return null;
        try {
            Class messageClass = message.getClass();
            for (Field f : messageClass.getDeclaredFields()) {
                if (f.isAnnotationPresent(ProtoField.class)) {
                    ProtoField pf = f.getAnnotation(ProtoField.class);
                    if (pf.tag() == tag) {
                        return f;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static <T, R extends Message> T getByTag(R message, int tag) {
        Field f = getFieldByTag(message, tag);
        try {
            return (T) f.get(message);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <R extends Message, M extends Message> List<M> getList(R message, String name) {
        if (message == null) return null;
        try {
            Class messageClass = message.getClass();
            Method method = messageClass.getDeclaredMethod(name);
            return (List<M>) method.invoke(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
