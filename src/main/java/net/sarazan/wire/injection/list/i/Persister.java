package net.sarazan.wire.injection.list.i;

import com.squareup.wire.Message;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Aaron Sarazan on 8/29/14
 * Copyright(c) 2014 Level, Inc.
 */

/**
 * This object is responsible for storing and retrieving responses from disk
 * @param <R> Response type (e.g. PhoneBookResponse)
 */
public interface Persister<R extends Message> {

    /**
     * Get the latest version of response from disk.
     * @return response
     */
    @Nullable
    R get();

    /**
     * Replace existing response with modified data.
     * @param data updated response object.
     */
    void insert(R data);

}
