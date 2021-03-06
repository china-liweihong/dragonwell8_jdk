/*
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.jfr.consumer;

import java.util.List;

import jdk.jfr.ValueDescriptor;
import jdk.jfr.internal.Type;

/**
 * Abstract factory for creating specialized types
 */
abstract class ObjectFactory<T> {

    public static ObjectFactory<?> create(Type type, TimeConverter timeConverter) {
        switch (type.getName()) {
        case "java.lang.Thread":
            return RecordedThread.createFactory(type, timeConverter);
        case Type.ORACLE_TYPE_PREFIX + "StackFrame":
            return RecordedFrame.createFactory(type, timeConverter);
        case Type.ORACLE_TYPE_PREFIX + "Method":
            return RecordedMethod.createFactory(type, timeConverter);
        case Type.ORACLE_TYPE_PREFIX + "ThreadGroup":
            return RecordedThreadGroup.createFactory(type, timeConverter);
        case Type.ORACLE_TYPE_PREFIX + "StackTrace":
            return RecordedStackTrace.createFactory(type, timeConverter);
        case Type.ORACLE_TYPE_PREFIX + "ClassLoader":
            return RecordedClassLoader.createFactory(type, timeConverter);
        case "java.lang.Class":
            return RecordedClass.createFactory(type, timeConverter);
        }
        return null;
    }

    private final List<ValueDescriptor> valueDescriptors;

    ObjectFactory(Type type) {
        this.valueDescriptors = type.getFields();
    }

    T createObject(long id, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Object[]) {
            return createTyped(valueDescriptors, id, (Object[]) value);
        }
        throw new InternalError("Object factory must have struct type");
    }

    abstract T createTyped(List<ValueDescriptor> valueDescriptors, long id, Object[] values);
}
