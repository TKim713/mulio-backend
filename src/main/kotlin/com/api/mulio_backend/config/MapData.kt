package com.api.mulio_backend.config

import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MapData @Autowired constructor(private val modelMapper: ModelMapper) {

    // Uncomment the line below if needed
    // init { modelMapper.configuration.isAmbiguityIgnored = true }

    fun <T, S> mapOne(data: T, type: Class<S>): S {
        return modelMapper.map(data, type)
    }

    fun <D, T> mapList(typeList: List<T>, outClass: Class<D>): List<D> {
        return typeList.map { entity -> mapOne(entity, outClass) }
    }
}