package com.github.gomestkd.eventmanagement.mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.HashSet;
import java.util.Set;

public class ObjectMapper {
    private final static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    public static <D, O> D parseObject(O object, Class<D> destinationClass) {
        return mapper.map(object, destinationClass);
    }

    public static <D, O> Set<D> parseSetObject(Set<O> objects, Class<D> destinationClass) {
        Set<D> destinationObjects = new HashSet<>();

        for (Object o : objects) {
            destinationObjects.add(mapper.map(o, destinationClass));
        }

        return destinationObjects;
    }
}
