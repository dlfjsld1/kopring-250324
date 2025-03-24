package com.example.upload.domain.post.genFile.entity;

import com.example.upload.domain.base.genFile.genFile.entity.GenFile;
import com.example.upload.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PostGenFile extends GenFile {

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
