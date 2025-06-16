package com.worbes.application.realm.port.out;

import com.worbes.application.realm.model.Realm;

import java.util.List;

public interface CreateRealmRepository {
    List<Realm> saveAll(List<Realm> realms);
}
