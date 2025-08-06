package com.demo.autocareer.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

public interface EntityMapper<D1, E, D2> {

    D1 mapEntityToRequest(E entity);

    E mapRequestToEntity(D1 dto);

    D2 mapEntityToResponse(E entity);

    E mapResponseToEntity(D2 dto);

    //    List<E> mapDtosToEntities(List<D1> dtos);
    //
    //    List<D1> mapEntitiesToDtos(List<E> entities);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget E entity, D1 dto);
}
