package com.samreact.skooLLy.common.util;

import com.samreact.skooLLy.common.response.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public final class PageUtil {

    private PageUtil() {}

    public static <T> PagedResponse<T> from(Page<?> page, List<T> content) {
        return PagedResponse.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public static <E, D> PagedResponse<D> from(Page<E> page, Function<E, D> mapper) {
        List<D> content = page.getContent().stream().map(mapper).toList();
        return from(page, content);
    }
}
