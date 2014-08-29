
package net.sarazan.wire.injection.list;

import com.squareup.wire.Message;

import net.sarazan.wire.injection.list.i.EqualsPredicate;
import net.sarazan.wire.injection.list.i.ListExtractor;
import net.sarazan.wire.injection.list.i.Persister;
import net.sarazan.wire.injection.list.i.WireInjectListener;
import net.sarazan.wire.reflection.ReflectiveWire;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron Sarazan on 8/8/14
 * Copyright(c) 2014 Level, Inc.
 */

/**
 * Lots of Wire response times are primarily vehicles for a list of objects.
 *
 * Often when performing CRUD operations on these objects, you will want to optimistically insert, remove, or update
 * that object on disk without triggering a network fetch.
 *
 * @param <R> The container response type. For example: PhoneBookResponse
 * @param <I> The list item type. If R is PhoneBookResponse, then this would be Person
 */
public abstract class WireListInjector<R extends Message, I extends Message> {

    public static final String TAG = "ProtoInject";

    @NotNull
    private final Persister<R> mPersister;

    @NotNull
    private final ListExtractor<R, I> mExtractor;

    @NotNull
    private final EqualsPredicate<I> mEquals;

    @Nullable
    private final WireInjectListener<I> mListener;

    protected WireListInjector(@NotNull Persister<R> persister,
                               @NotNull ListExtractor<R, I> extractor,
                               @NotNull EqualsPredicate<I> equals,
                               @Nullable WireInjectListener<I> listener) {
        mPersister = persister;
        mExtractor = extractor;
        mEquals = equals;
        mListener = listener;
    }

    protected WireListInjector(@NotNull Persister<R> persister,
                               @NotNull ListExtractor<R, I> extractor,
                               @NotNull EqualsPredicate<I> equals) {
        this(persister, extractor, equals, null);
    }

    /**
     * Replaces in list, or adds if not already existent
     * @param items a list of items to insert or update
     * @param mergeFields On a replace operation, do you want to bring over the old object's fields in case of null values on the new object?
     *
     * See {@link ReflectiveWire#merge} for discussion of merge policy
     */
    public void insert(List<I> items, boolean mergeFields) {
        R response = mPersister.get();
        if (response == null) return;
        List<I> copy = new ArrayList<I>(mExtractor.getItems(response));
        Message.Builder<R> builder = ReflectiveWire.cloneToBuilder(response);
        for (final I item : items) {
            int index = -1;
            for (int i = 0; i < copy.size(); ++i) {
                if (mEquals.isEqual(copy.get(i), item)) {
                    index = i;
                }
            }
            I add = item;
            if (index < 0) {
                copy.add(add);
            } else {
                I old = copy.get(index);
                if (mergeFields) {
                    add = ReflectiveWire.merge(item, old);
                }
                if (mListener != null) add = mListener.onReplace(old, add);
                copy.set(index, add);
            }
        }
        mExtractor.setItems(builder, copy);
        mPersister.insert(builder.build());
    }

    /**
     * Remove items if they exist
     * @param items list of items to remove
     */
    public void remove(List<I> items) {
        R response = mPersister.get();
        if (response == null) return;
        List<I> copy = new ArrayList<I>(mExtractor.getItems(response));
        Message.Builder<R> builder = ReflectiveWire.cloneToBuilder(response);
        for (final I item : items) {
            int index = -1;
            for (int i = 0; i < copy.size(); ++i) {
                if (mEquals.isEqual(copy.get(i), item)) {
                    index = i;
                }
            }
            if (index >= 0) {
                copy.remove(item);
            }
        }
        mExtractor.setItems(builder, copy);
        mPersister.insert(builder.build());
    }
}
