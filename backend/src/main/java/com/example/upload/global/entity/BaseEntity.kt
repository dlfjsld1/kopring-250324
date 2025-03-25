package com.example.upload.global.entity;

import com.example.upload.domain.base.genFile.genFile.entity.GenFile
import com.example.upload.standard.util.Ut
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity {

    @Id // PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    var id: Long? = null

    val modelName: String
        get() = Ut.str.lcfirst(this::class.simpleName)

    override fun equals(other: Any?): Boolean {
        if (other == null || javaClass != other.javaClass) return false

        return id == (other as GenFile).id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: System.identityHashCode(this)
    }
}
