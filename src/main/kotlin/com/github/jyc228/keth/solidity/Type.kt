package com.github.jyc228.keth.solidity

sealed interface Type {
    val name: String
    val size: Int?
    val dynamic: Boolean

    companion object {
        fun of(typeName: String): Type = when {
            typeName.endsWith(']') -> arrayType(typeName)
            typeName.startsWith('(') || typeName.startsWith("tuple(") -> tupleType(typeName)
            else -> primitiveType(typeName)
        }

        private fun arrayType(typeName: String): ArrayType {
            val start = typeName.lastIndexOf('[')
            val end = typeName.lastIndexOf(']')
            val elementName = typeName.substring(0, start)
            return ArrayType(
                name = elementName,
                size = when (start + 1 == end) {
                    true -> null
                    false -> typeName.substring(start + 1, end).toInt()
                },
                elementType = of(elementName)
            )
        }

        private fun tupleType(typeName: String): TupleType {
            val range = (typeName.indexOf('(') + 1)..<typeName.lastIndexOf(')')
            val components = mutableListOf<Type>()
            var cursor = range.first
            while (cursor <= range.last) {
                if (typeName[cursor] == '(' || typeName.startsWith("tuple(", cursor)) {
                    val tupleEnd = typeName.indexOf(')', cursor)
                    components += tupleType(typeName.substring(cursor, tupleEnd + 1))
                    cursor = tupleEnd + 2
                } else {
                    val index = typeName.indexOf(',', cursor).takeIf { it != -1 } ?: (range.last + 1)
                    components += of(typeName.substring(cursor, index))
                    cursor = index + 1
                }
            }
            return TupleType(components)
        }

        private fun primitiveType(typeName: String): PrimitiveType {
            return when (val sizeIndex = typeName.indexOfFirst { it.isDigit() }) {
                -1 -> PrimitiveType(typeName, null)
                else -> PrimitiveType(typeName.take(sizeIndex), typeName.drop(sizeIndex).toInt())
            }
        }
    }
}

data class PrimitiveType(override val name: String, override val size: Int?) : Type {
    override val dynamic get() = (name == "string" || name == "bytes") && size == null
    override fun toString(): String = "$name${size ?: ""}"
}

data class ArrayType(override val name: String, override val size: Int?, val elementType: Type) : Type {
    constructor(name: String, size: Int? = null, typeName: String) : this(name, size, Type.of(typeName))

    override val dynamic: Boolean get() = size == null
    override fun toString(): String = "$name[${size ?: ""}]"
}

data class TupleType(val components: List<Type>) : Type {
    constructor(vararg components: Type) : this(components.toList())
    constructor(vararg typeName: String) : this(typeName.map(Type::of))

    override val name: String = "tuple"
    override val size: Int get() = components.size
    override val dynamic: Boolean = components.any { it.dynamic }
    override fun toString(): String = "$name(${components.joinToString(",")})"
}
