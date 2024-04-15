package name.expenses.features.association.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssociationGenericReqDto <T>{
    private Set<T> associationReqDtos;

}
