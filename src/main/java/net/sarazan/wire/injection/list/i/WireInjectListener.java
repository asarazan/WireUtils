package net.sarazan.wire.injection.list.i;

import com.squareup.wire.Message;

/**
 * Created by Aaron Sarazan on 8/29/14
 * Copyright(c) 2014 Level, Inc.
 */

/**
 * Respond to successful operations on a Wire list response
 * @param <I> list item type
 */
public interface WireInjectListener<I extends Message> {

    /**
     * If the injector successfully updates an item, it will call this method for additional logic.
     * @param oldItem item to be replaced
     * @param newItem item to be inserted
     * @return newItem or processed item to be inserted.
    */
    I onReplace(I oldItem, I newItem);

}
