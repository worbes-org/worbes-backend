package com.worbes.adapter.jpa.realm;

import com.worbes.application.realm.model.Realm;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface RealmMapper {
    Realm toDomain(RealmEntity entity);

    RealmEntity toEntity(Realm domain);
}
