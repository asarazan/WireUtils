package net.sarazan.wire.injection.list.i;

import com.squareup.wire.Message;
import com.squareup.wire.Message.Builder;

import java.util.List;

/**
 * Created by Aaron Sarazan on 8/29/14
 * Copyright(c) 2014 Level, Inc.
 */

/**
 * This object is responsible for getting and setting a list in a response object
 * @param <R> PhoneBookResponse
 * @param <I> Person
 */
public interface ListExtractor<R extends Message, I extends Message> {

    /**
     * Extract item list from a response.
     * @param response response object.
     * @return list of objects stored in response.
     */
    List<I> getItems(R response);

    /**
     * Update item list in a response.
     * @param builder mutable response builder.
     * @param items list of items to insert.
     */
    void setItems(Builder<R> builder, List<I> items);
}
