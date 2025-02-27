package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.entity.embeded.Language;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Realm {

    @Id
    private Long id;

    private Language name;

    private Long connectedRealmId;
    
    private Realm(Long id, Language name, Long connectedRealmId) {
        this.id = id;
        this.name = name;
        this.connectedRealmId = connectedRealmId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Language name;
        private Long connectedRealmId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(Language name) {
            this.name = name;
            return this;
        }

        public Builder connectedRealmId(Long connectedRealmId) {
            this.connectedRealmId = connectedRealmId;
            return this;
        }

        public Realm build() {
            return new Realm(id, name, connectedRealmId);
        }
    }
}
