package com.soldesk6F.ondal.useract.complain.repository;

import static org.springframework.util.StringUtils.hasText;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.soldesk6F.ondal.useract.complain.dto.ComplainDto;
import com.soldesk6F.ondal.useract.complain.dto.ComplainSearchCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.soldesk6F.ondal.useract.complain.entity.QComplain;      // ★ 패키지 맞춤
import com.soldesk6F.ondal.useract.complain.entity.QComplainImg;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ComplainRepositoryImpl implements ComplainRepositoryCustom {

    private final JPAQueryFactory qf;

    @Override
    public Page<ComplainDto> search(ComplainSearchCond cond, Pageable pageable) {

        QComplain c = QComplain.complain;
        QComplainImg  i = new QComplainImg("img");        // ★ static 대신 new

        BooleanBuilder where = new BooleanBuilder();
        if (hasText(cond.role())) {
            where.and(c.role.stringValue().eq(cond.role()));
        }
        if (hasText(cond.keyword())) {
            String kw = "%" + cond.keyword().toLowerCase() + "%";
            where.and(
                c.complainTitle.lower().like(kw)               // VARCHAR 칼럼
                  .or(c.complainContent.like("%" + cond.keyword() + "%"))   // CLOB: lower() 빼기
            );
        }

        List<ComplainDto> rows = qf.select(
                Projections.constructor(ComplainDto.class,
                    c.complainId,
                    c.complainTitle,
                    c.complainContent,
                    c.user.userId.coalesce(c.guestId),
                    c.role.stringValue(),
                    c.createdDate,
                    c.complainStatus.stringValue(),
                    ExpressionUtils.as(
                        JPAExpressions.select(i.complainImg)
                                      .from(i)
                                      .where(i.complain.eq(c))
                                      .limit(1),           // 첫 장만
                        "firstImage")))
            .from(c)
            .where(where)
            .orderBy(c.complainId.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = qf.select(c.count()).from(c).where(where).fetchOne();
        return new PageImpl<>(rows, pageable, total);
    }
}