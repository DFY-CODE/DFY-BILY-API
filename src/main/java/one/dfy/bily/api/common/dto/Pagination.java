package one.dfy.bily.api.common.dto;

public record Pagination(
        int page,
        int pageSize,
        Long totalItems,
        int totalPages
) {
}
