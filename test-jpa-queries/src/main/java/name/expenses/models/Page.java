
package name.expenses.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class  Page<T> {

  private List<T> content;
  private long pageNumber;
  private long pageSize;
  private long totalElements;
  private long totalPages;
  private boolean hasNext;
  private boolean hasPrevious;

}