package one.dfy.bily.api.common.mapper;

import one.dfy.bily.api.common.dto.Pagination;
import org.springframework.data.domain.Pageable;

public class PaginationMapper {

    public static Pagination toPagination(Pageable pageable, Long total, int totalPage) {
        return new Pagination(pageable.getPageNumber(), pageable.getPageSize(), total, totalPage);
    }
}
