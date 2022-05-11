package ru.storozh.common.network

data class Consider(val variant: ConsiderVariants, val httpCode: Int, val msg: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Consider

        if (httpCode != other.httpCode) return false

        return true
    }

    override fun hashCode(): Int {
        return httpCode
    }
}

enum class ConsiderVariants {
    Y, // SUCCESS
    N, // EMPTY ANSHWER OR NOT MODIFIED DATA
    E  // ERROR
}