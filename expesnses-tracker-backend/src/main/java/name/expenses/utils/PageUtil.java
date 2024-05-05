package name.expenses.utils;

import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

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

    public static SortDirection getSortDirection(String direction){
        SortDirection sortDir;
        if (direction == null || direction.isBlank() || direction.equalsIgnoreCase("ASC")) {
            sortDir = SortDirection.ASC;
        }else if (direction.equalsIgnoreCase("DESC")){
            sortDir = SortDirection.DESC;
        }else {
            sortDir = SortDirection.ASC;
        }
        return sortDir;
    }

}
