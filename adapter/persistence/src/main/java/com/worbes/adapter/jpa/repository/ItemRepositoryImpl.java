package com.worbes.adapter.jpa.repository;

import com.worbes.adapter.jpa.entity.ItemEntity;
import com.worbes.adapter.jpa.mapper.ItemEntityMapper;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemCommand;
import com.worbes.application.item.port.out.CreateItemRepository;
import com.worbes.application.item.port.out.SearchItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements CreateItemRepository, SearchItemRepository {

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

    @SuppressWarnings("unchecked")
    @Override
    public List<Item> searchAll(SearchItemCommand command) {
        StringBuilder sql = new StringBuilder("SELECT * FROM item WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        if (command.itemClassId() != null) {
            sql.append(" AND item_class_id = :itemClassId");
            parameters.put("itemClassId", command.itemClassId());
        }

        if (command.itemSubclassId() != null) {
            sql.append(" AND item_subclass_id = :itemSubclassId");
            parameters.put("itemSubclassId", command.itemSubclassId());
        }

        if (command.name() != null && !command.name().isBlank()) {
            sql.append(" AND EXISTS (SELECT 1 FROM jsonb_each_text(name) WHERE value ILIKE :searchName)");
            parameters.put("searchName", "%" + command.name() + "%");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), ItemEntity.class);
        parameters.forEach(query::setParameter);
        List<ItemEntity> result = query.getResultList();

        return result.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
