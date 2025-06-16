package com.worbes.application.item.model;

import com.worbes.application.common.model.LocalizedName;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
public class ItemSubclass {

    private final Long itemClassId;
    private final Long subclassId;
    private final LocalizedName displayName;
    private final LocalizedName verboseName;

    private ItemSubclass(Builder builder) {
        this.itemClassId = builder.itemClassId;
        this.subclassId = builder.subclassId;
        this.displayName = builder.displayName;
        this.verboseName = builder.verboseName;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemSubclass that = (ItemSubclass) o;
        return Objects.equals(itemClassId, that.itemClassId) && Objects.equals(subclassId, that.subclassId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemClassId, subclassId);
    }

    public static class Builder {
        private Long itemClassId;
        private Long subclassId;
        private LocalizedName displayName;
        private LocalizedName verboseName;

        public Builder itemClassId(Long itemClassId) {
            this.itemClassId = itemClassId;
            return this;
        }

        public Builder subclassId(Long subclassId) {
            this.subclassId = subclassId;
            return this;
        }

        public Builder displayName(LocalizedName displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder displayName(Map<String, String> displayName) {
            this.displayName = LocalizedName.fromRaw(displayName);
            return this;
        }

        public Builder verboseName(LocalizedName verboseName) {
            this.verboseName = verboseName;
            return this;
        }

        public Builder verboseName(Map<String, String> verboseName) {
            this.verboseName = LocalizedName.fromRaw(verboseName);
            return this;
        }

        public ItemSubclass build() {
            validateRequiredFields();
            return new ItemSubclass(this);
        }

        private void validateRequiredFields() {
            if (itemClassId == null) {
                throw new IllegalStateException("itemClass is required");
            }
            if (subclassId == null) {
                throw new IllegalStateException("subclassId is required");
            }
            if (displayName == null || displayName.isEmpty()) {
                throw new IllegalStateException("displayName is required");
            }
        }
    }
}
