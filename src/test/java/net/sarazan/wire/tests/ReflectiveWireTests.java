package net.sarazan.wire.tests;

import junit.framework.TestCase;

import net.sarazan.wire.ReflectiveWire;
import net.sarazan.wire.tests.proto.TestObject;
import net.sarazan.wire.tests.proto.TestObject.Builder;
import net.sarazan.wire.tests.proto.TestSubObject;

/**
 * Created by Aaron Sarazan on 8/20/14
 * Copyright(c) 2014 Level, Inc.
 */
public class ReflectiveWireTests extends TestCase {

    public static final String TAG = "ReflectiveWireTests";

    public void testMerge() {

        final Long age = 54L;
        final Long ageNew = 55L;
        final String name = "Bob";
        final String first = "first";
        final String second = "second";
        final String secondNew = "secondNew";

        TestObject obj1 = new Builder()
                .age(age)
                .name(name)
                .sub(new TestSubObject.Builder()
                        .first(first)
                        .second(second)
                        .third(null)
                        .build())
                .build();

        TestObject update = new Builder()
                .age(ageNew)
                .sub(new TestSubObject.Builder()
                        .second(secondNew)
                        .build())
                .build();

        TestObject merged = ReflectiveWire.merge(update, obj1);

        assertEquals(ageNew, merged.age);
        assertEquals(name, merged.name);
        assertEquals(first, merged.sub.first);
        assertEquals(secondNew, merged.sub.second);
        assertNull(merged.sub.third);
    }
}
