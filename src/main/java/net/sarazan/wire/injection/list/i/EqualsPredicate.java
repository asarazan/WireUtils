package net.sarazan.wire.injection.list.i;

import com.squareup.wire.Message;

/**
 * Created by Aaron Sarazan on 8/29/14
 * Copyright(c) 2014 Level, Inc.
 */

/**
 * Didn't want to pull in google libs for a predicate, and we don't care about sort order so here you go.
 * @param <I> List Item
 */
public interface EqualsPredicate<I extends Message> {

    /**
     * Check two items for equality
     * @param i1 first item
     * @param i2 second item
     * @return true if these should be considered the same item.
     */
    boolean isEqual(I i1, I i2);

}
