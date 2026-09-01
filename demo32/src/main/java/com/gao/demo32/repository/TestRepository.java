package com.gao.demo32.repository;

import com.gao.demo32.entity.po.VectorStorePO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import jakarta.annotation.Resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TestRepository {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<VectorStorePO> select() {
        String sql = "select id, content, metadata, embedding from vector_store";
        return jdbcTemplate.query(sql, new RowMapper<>() {
            @Override
            public VectorStorePO mapRow(ResultSet rs, int rowNum) throws SQLException {
                VectorStorePO po = new VectorStorePO();
                po.setId(rs.getString("id"));
                po.setContent(rs.getString("content"));
                po.setMetadata(rs.getString("metadata"));

                // 直接获取 embedding 的字符串形式，格式如 "[0.1,0.2,0.3]"
                String embeddingStr = rs.getString("embedding");
                float[] embedding = parseVector(embeddingStr);
                po.setEmbedding(embedding);

                return po;
            }

            private float[] parseVector(String vectorStr) {
                if (vectorStr == null || vectorStr.isEmpty()) {
                    return null; // 或返回空数组
                }
                // 去除首尾方括号并按逗号分割
                String[] parts = vectorStr.substring(1, vectorStr.length() - 1).split(",");
                float[] result = new float[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    result[i] = Float.parseFloat(parts[i].trim());
                }
                return result;
            }
        });
    }
}
