package com.example.upload.domain.post.genFile.entity;

import com.example.upload.domain.base.genFile.genFile.entity.GenFile;
import com.example.upload.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostGenFile extends GenFile {

    public PostGenFile(Post post, TypeCode typeCode, int fileNo, String originalFileName, String metadataStr, String yyyyMmDd, String fileExtTypeCode, String fileExtType2Code, String fileExt, String fileName, long fileSize) {

        super(fileNo, originalFileName, metadataStr, yyyyMmDd, fileExtTypeCode, fileExtType2Code, fileExt, fileName, fileSize);
        this.post = post;
        this.typeCode = typeCode;
    }

    public enum TypeCode {
        attachment,
        thumbnail
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Enumerated(EnumType.STRING)
    private TypeCode typeCode;

    @Override
    protected long getOwnerModelId() {
        return post.getId();
    }

    @Override
    protected String getTypeCodeAsStr() {
        return typeCode.name();
    }
}
