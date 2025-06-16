package com.worbes.adapter.jpa.mapper;

import com.worbes.adapter.jpa.entity.RealmEntity;
import com.worbes.application.realm.model.Realm;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface RealmEntityMapper {

    Realm toDomain(RealmEntity entity);

    RealmEntity toEntity(Realm domain);
}
