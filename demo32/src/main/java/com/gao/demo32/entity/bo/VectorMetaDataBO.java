package com.gao.demo32.entity.bo;

import com.gao.demo32.entity.vo.VectorMetaDataVO;
import lombok.Data;

@Data
public class VectorMetaDataBO {
    private String source;
    private String title;
    private String url;

    public VectorMetaDataVO toVO() {
        return new VectorMetaDataVO(this.source, this.title, this.url);
    }
}
