package one.dfy.bily.api.space.model.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.space.dto.NonUserSpaceInfo;
import one.dfy.bily.api.space.model.QSpace;
import one.dfy.bily.api.space.model.QSpaceFileInfo;
import one.dfy.bily.api.space.model.repository.SpaceCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SpaceCustomRepositoryImpl implements SpaceCustomRepository {

    private final JPAQueryFactory queryFactory;


}
