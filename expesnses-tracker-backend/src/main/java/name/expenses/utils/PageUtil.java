package name.expenses.utils;

import name.expenses.globals.Page;

import java.util.List;

public class PageUtil {
    public static <T> Page<T> createPage(long pageNumber, long pageSize, List<T> entities, long totalElements) {
        Page<T> page = new Page<>();
        page.setContent(entities);
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);
        page.setTotalElements(totalElements);
        page.setHasNext((pageNumber * pageSize) < totalElements);
        page.setHasPrevious(pageNumber > 1);
        return page;
    }

}
