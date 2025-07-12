package com.worbes.adapter.jpa.item;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.ItemCommandRepository;
import com.worbes.application.item.port.out.ItemQueryRepository;
import com.worbes.application.item.port.out.ItemSearchCondition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemCommandRepository, ItemQueryRepository {

    private final ItemJpaRepository jpaRepository;
    private final EntityManager entityManager;
    private final ItemEntityMapper mapper;

    @Override
    public List<Item> saveAll(List<Item> items) {
        List<ItemEntity> entities = items.stream()
                .map(mapper::toEntity)
                .toList();

        return jpaRepository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    //TODO:  name 컬럼에 GIN/GIN+trgm 인덱스 등 추가 고려 필요
    @SuppressWarnings("unchecked")
    @Override
    public List<Item> findByCondition(ItemSearchCondition condition) {
        StringBuilder sql = new StringBuilder("SELECT * FROM item WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        if (condition.classId() != null) {
            sql.append(" AND class_id = :classId");
            parameters.put("classId", condition.classId());
        }

        if (condition.subclassId() != null) {
            sql.append(" AND subclass_id = :subclassId");
            parameters.put("subclassId", condition.subclassId());
        }

        if (condition.name() != null && !condition.name().isBlank()) {
            sql.append(" AND EXISTS (SELECT 1 FROM jsonb_each_text(name) WHERE value ILIKE :searchName)");
            parameters.put("searchName", "%" + condition.name() + "%");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), ItemEntity.class);
        parameters.forEach(query::setParameter);
        List<ItemEntity> result = query.getResultList();

        return result.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return jpaRepository.findById(itemId)
                .map(mapper::toDomain);
    }
}
