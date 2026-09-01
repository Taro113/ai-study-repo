package com.gao.demo32.entity.po;

import com.gao.demo32.entity.bo.VectorMetaDataBO;
import com.gao.demo32.entity.bo.VectorStoreBO;
import com.gao.demo32.utils.JsonUtils;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Table(name = "vector_store")
public class VectorStorePO {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 分片后的数据内容
     */
    private String content;

    /**
     * 元数据信息，JSON 格式
     */
    private String metadata;

    /**
     * 数据向量
     */
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1024)                // 指定向量维度
    @Column(name = "embedding", columnDefinition = "vector(1024)")
    private float[] embedding;

    public VectorStoreBO toBO() {
        VectorStoreBO bo = new VectorStoreBO();
        bo.setId(this.id);
        bo.setContent(this.content);
        bo.setMetadata(JsonUtils.fromJson(this.metadata, VectorMetaDataBO.class));
        bo.setEmbedding(this.embedding);
        return bo;
    }
}
