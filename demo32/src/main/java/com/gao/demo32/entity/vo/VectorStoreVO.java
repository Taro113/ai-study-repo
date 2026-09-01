package com.gao.demo32.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VectorStoreVO {

    /**
     * id
     */
    private String id;

    /**
     * 分片后的数据内容
     */
    private String content;

    /**
     * 元数据信息
     */
    private VectorMetaDataVO metadata;

    /**
     * 数据向量
     */
    private float[] embedding;
}

