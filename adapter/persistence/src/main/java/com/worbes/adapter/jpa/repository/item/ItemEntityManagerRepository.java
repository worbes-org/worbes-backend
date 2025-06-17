package com.worbes.adapter.jpa.repository.item;

import com.worbes.adapter.jpa.entity.ItemEntity;
import com.worbes.application.item.port.in.SearchItemCommand;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemEntityManagerRepository {

    private final EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<ItemEntity> searchAll(SearchItemCommand command) {
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

        return query.getResultList();
    }
}
