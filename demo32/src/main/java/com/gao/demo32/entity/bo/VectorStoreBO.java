package com.gao.demo32.entity.bo;

import com.gao.demo32.entity.vo.VectorMetaDataVO;
import com.gao.demo32.entity.vo.VectorStoreVO;
import lombok.Data;

@Data
public class VectorStoreBO {

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
    private VectorMetaDataBO metadata;

    /**
     * 数据向量
     */
    private float[] embedding;

    public VectorStoreVO toVO() {
        VectorMetaDataVO vectorMetaDataVO = this.metadata.toVO();
        return new VectorStoreVO(this.id, this.content, vectorMetaDataVO, this.embedding);
    }
}
