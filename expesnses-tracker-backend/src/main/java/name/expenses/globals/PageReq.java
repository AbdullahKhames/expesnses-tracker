package name.expenses.globals;

//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class PageReq {
//    Long pageNumber;
//    Long pageSize;
//}

public record PageReq(Long pageNumber, Long pageSize) {}
