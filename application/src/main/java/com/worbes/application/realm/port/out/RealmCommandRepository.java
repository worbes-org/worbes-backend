package com.worbes.application.realm.port.out;

import com.worbes.application.realm.model.Realm;

import java.util.List;
import java.util.Set;

public interface RealmCommandRepository {
    List<Realm> saveAll(Set<Realm> realms);
}
