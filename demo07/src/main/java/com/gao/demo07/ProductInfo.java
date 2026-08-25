package com.gao.demo07;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {
    /**
     * ID
     */
    private Long id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品类别
     */
    private Integer category;

    /**
     * 产品价格
     */
    private Double price;

    /**
     * 综合评分（1-10）
     */
    private Integer rating;

}
