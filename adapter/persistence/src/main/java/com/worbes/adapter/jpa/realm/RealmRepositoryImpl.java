package com.worbes.adapter.jpa.realm;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.CreateRealmRepository;
import com.worbes.application.realm.port.out.FindRealmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RealmRepositoryImpl implements FindRealmRepository, CreateRealmRepository {

    private final RealmJpaRepository jpaRepository;
    private final RealmEntityMapper mapper;

    @Override
    public List<Realm> saveAll(List<Realm> realms) {
        List<RealmEntity> entities = realms.stream()
                .map(mapper::toEntity)
                .toList();

        return jpaRepository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Set<String> findSlugByRegion(RegionType region) {
        return jpaRepository.findSlugByRegion(region);
    }

    @Override
    public List<Realm> findByRegion(RegionType region) {
        return jpaRepository.findByRegion(region).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Long> findDistinctConnectedRealmIdByRegion(RegionType region) {
        return jpaRepository.findDistinctConnectedRealmIdsByRegion(region);
    }
}
