package ma.mundiapolis.banking.dtos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.print.DocFlavor;

@Data @NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String id;
    private String name;
    private double price;
    private CategoryDTO categoryDTO;
}
