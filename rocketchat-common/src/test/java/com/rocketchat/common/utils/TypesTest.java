/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rocketchat.common.utils;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class TypesTest {

    @Test
    public void newParameterizedType() throws Exception {
        // List<A>. List is a top-level class.
        Type type = Types.newParameterizedType(List.class, A.class);
        assertThat(getFirstTypeArgument(type), is(Matchers.<Type>equalTo(A.class)));

        // A<B>. A is a static inner class.
        type = Types.newParameterizedTypeWithOwner(TypesTest.class, A.class, B.class);
        assertThat(getFirstTypeArgument(type), is(Matchers.<Type>equalTo(B.class)));
    }

    @Test
    public void parameterizedTypeWithRequiredOwnerMissing() throws Exception {
        try {
            Types.newParameterizedType(A.class, B.class);
            fail();
        } catch (IllegalArgumentException expected) {
            String message = "unexpected owner type for " + A.class + ": null";
            assertThat(expected.getMessage(), is(equalTo(message)));
        }
    }

    @Test
    public void parameterizedTypeWithUnnecessaryOwnerProvided() throws Exception {
        try {
            Types.newParameterizedTypeWithOwner(A.class, List.class, B.class);
            fail();
        } catch (IllegalArgumentException expected) {
            String message = "unexpected owner type for " + List.class + ": " + A.class;
            assertThat(expected.getMessage(), is(equalTo(message)));
        }
    }

    @Test
    public void arrayOf() {
        assertTrue(Types.getRawType(Types.arrayOf(int.class)) == int[].class);
        assertTrue(Types.getRawType(Types.arrayOf(List.class)) == List[].class);
        assertTrue(Types.getRawType(Types.arrayOf(String[].class)) == String[][].class);
    }

    List<? extends CharSequence> listSubtype;
    List<? super String> listSupertype;

    @Test
    public void subtypeOf() throws Exception {
        Type listOfWildcardType = TypesTest.class.getDeclaredField("listSubtype").getGenericType();
        Type expected = Types.collectionElementType(listOfWildcardType, List.class);
        assertThat(Types.subtypeOf(CharSequence.class), is(equalTo(expected)));
    }

    @Test
    public void supertypeOf() throws Exception {
        Type listOfWildcardType = TypesTest.class.getDeclaredField("listSupertype").getGenericType();
        Type expected = Types.collectionElementType(listOfWildcardType, List.class);
        assertThat(Types.supertypeOf(String.class), is(equalTo(expected)));
    }

    @Test
    public void getFirstTypeArgument() throws Exception {
        assertThat(getFirstTypeArgument(A.class), is(nullValue()));

        Type type = Types.newParameterizedTypeWithOwner(TypesTest.class, A.class, B.class, C.class);
        assertThat(getFirstTypeArgument(type), is(Matchers.<Type>equalTo(B.class)));
    }

    @Test
    public void newParameterizedTypeObjectMethods() throws Exception {
        Type mapOfStringIntegerType = TypesTest.class.getDeclaredField(
                "mapOfStringInteger").getGenericType();
        ParameterizedType newMapType = Types.newParameterizedType(Map.class, String.class, Integer.class);
        assertThat(newMapType, is(equalTo(mapOfStringIntegerType)));
        assertThat(newMapType.hashCode(), is(equalTo(mapOfStringIntegerType.hashCode())));
        assertThat(newMapType.toString(), is(equalTo(mapOfStringIntegerType.toString())));

        Type arrayListOfMapOfStringIntegerType = TypesTest.class.getDeclaredField(
                "arrayListOfMapOfStringInteger").getGenericType();
        ParameterizedType newListType = Types.newParameterizedType(ArrayList.class, newMapType);
        assertThat(newListType, is(equalTo(arrayListOfMapOfStringIntegerType)));
        assertThat(newListType.hashCode(), is(equalTo(arrayListOfMapOfStringIntegerType.hashCode())));
        assertThat(newListType.toString(), is(equalTo(arrayListOfMapOfStringIntegerType.toString())));
    }

    private static final class A {
    }

    private static final class B {
    }

    private static final class C {
    }

    /**
     * Given a parameterized type {@code A<B, C>}, returns B. If the specified type is not a generic
     * type, returns null.
     */
    public static Type getFirstTypeArgument(Type type) throws Exception {
        if (!(type instanceof ParameterizedType)) return null;
        ParameterizedType ptype = (ParameterizedType) type;
        Type[] actualTypeArguments = ptype.getActualTypeArguments();
        if (actualTypeArguments.length == 0) return null;
        return Types.canonicalize(actualTypeArguments[0]);
    }

    Map<String, Integer> mapOfStringInteger;
    Map<String, Integer>[] arrayOfMapOfStringInteger;
    ArrayList<Map<String, Integer>> arrayListOfMapOfStringInteger;

    interface StringIntegerMap extends Map<String, Integer> {
    }

    @Test
    public void arrayComponentType() throws Exception {
        assertThat(Types.arrayComponentType(String[][].class), is(Matchers.<Type>equalTo(String[].class)));
        assertThat(Types.arrayComponentType(String[].class), is(Matchers.<Type>equalTo(String.class)));

        Type arrayOfMapOfStringIntegerType = TypesTest.class.getDeclaredField(
                "arrayOfMapOfStringInteger").getGenericType();
        Type mapOfStringIntegerType = TypesTest.class.getDeclaredField(
                "mapOfStringInteger").getGenericType();
        assertThat(Types.arrayComponentType(arrayOfMapOfStringIntegerType),
                is(equalTo(mapOfStringIntegerType)));
    }

    @Test
    public void collectionElementType() throws Exception {
        Type arrayListOfMapOfStringIntegerType = TypesTest.class.getDeclaredField(
                "arrayListOfMapOfStringInteger").getGenericType();
        Type mapOfStringIntegerType = TypesTest.class.getDeclaredField(
                "mapOfStringInteger").getGenericType();
        assertThat(Types.collectionElementType(arrayListOfMapOfStringIntegerType, List.class),
                is(equalTo(mapOfStringIntegerType)));
    }

    @Test
    public void mapKeyAndValueTypes() throws Exception {
        Type mapOfStringIntegerType = TypesTest.class.getDeclaredField(
                "mapOfStringInteger").getGenericType();
        assertThat(Types.mapKeyAndValueTypes(mapOfStringIntegerType, Map.class),
                is(Matchers.<Type>arrayContaining(String.class, Integer.class)));
    }

    @Test
    public void propertiesTypes() throws Exception {
        assertThat(Types.mapKeyAndValueTypes(Properties.class, Properties.class),
                is(Matchers.<Type>arrayContaining(String.class, String.class)));
    }

    @Test
    public void fixedVariablesTypes() throws Exception {
        assertThat(Types.mapKeyAndValueTypes(StringIntegerMap.class, StringIntegerMap.class),
                is(Matchers.<Type>arrayContaining(String.class, Integer.class)));
    }

    @Test
    public void arrayEqualsGenericTypeArray() {
        assertThat(Types.equals(int[].class, Types.arrayOf(int.class)), is(true));
        assertThat(Types.equals(Types.arrayOf(int.class), int[].class), is(true));
        assertThat(Types.equals(String[].class, Types.arrayOf(String.class)), is(true));
        assertThat(Types.equals(Types.arrayOf(String.class), String[].class), is(true));
    }
}
