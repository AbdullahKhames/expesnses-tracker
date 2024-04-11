package name.expenses.utils;

import name.expenses.globals.Page;

import java.util.List;

public class PageUtil {
    public static <T> Page<T> createPage(Long pageNumber, Long pageSize, List<T> entities, long totalElements) {
        Page<T> page = new Page<>();
        page.setContent(entities);
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);
        page.setTotalElements(totalElements);

        long totalPages = 1;
        if (pageSize != null && pageSize != 0){
            totalPages = Math.ceilDiv(totalElements, pageSize);
        }
        page.setTotalPages(totalPages);
        page.setHasNext((pageNumber * pageSize) < totalElements);
        page.setHasPrevious(pageNumber > 1);
        return page;
    }

}
