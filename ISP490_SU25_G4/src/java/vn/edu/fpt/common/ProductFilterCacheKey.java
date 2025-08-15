package vn.edu.fpt.common;

import java.io.Serializable;
import java.util.Objects;

public class ProductFilterCacheKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String keyword;
    private final Double minPrice;
    private final Double maxPrice;
    private final String origin;
    private final Integer categoryId;
    private final int page;
    private final int pageSize;

    public ProductFilterCacheKey(String keyword, Double minPrice, Double maxPrice, String origin, Integer categoryId, int page, int pageSize) {
        this.keyword = (keyword != null && !keyword.isEmpty()) ? keyword.trim() : null;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.origin = (origin != null && !origin.isEmpty()) ? origin.trim() : null;
        this.categoryId = categoryId;
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductFilterCacheKey that = (ProductFilterCacheKey) o;
        return page == that.page &&
               pageSize == that.pageSize &&
               Objects.equals(keyword, that.keyword) &&
               Objects.equals(minPrice, that.minPrice) &&
               Objects.equals(maxPrice, that.maxPrice) &&
               Objects.equals(origin, that.origin) &&
               Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, minPrice, maxPrice, origin, categoryId, page, pageSize);
    }
}
