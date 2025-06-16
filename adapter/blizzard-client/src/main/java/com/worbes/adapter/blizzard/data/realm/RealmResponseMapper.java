package com.worbes.adapter.blizzard.data.realm;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.RealmFetchResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface RealmResponseMapper {

    @Mapping(target = "id", source = "response.id")
    @Mapping(target = "name", source = "response.name")
    @Mapping(target = "connectedRealmHref", source = "response.connectedRealmHref")
    @Mapping(target = "slug", source = "response.slug")
    @Mapping(target = "region", source = "region")
    RealmFetchResult toDto(RealmResponse response, RegionType region);
}
