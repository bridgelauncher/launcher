package com.tored.bridgelauncher.utils

class URLWithQueryBuilder(val baseURL: String)
{
    val params = mutableListOf<Pair<String, Any?>>();

    fun addParam(name: String, value: Any?, addEvenIfNull: Boolean = false): URLWithQueryBuilder
    {
        if (addEvenIfNull || value != null)
            params.add(name to value)
        return this
    }

    fun addParams(params: Iterable<Pair<String, Any?>>): URLWithQueryBuilder
    {
        this.params.addAll(params)
        return this
    }

    fun addParams(params: Array<Pair<String, Any?>>): URLWithQueryBuilder
    {
        this.params.addAll(params)
        return this
    }

    fun build(): String
    {
        return if (params.any())
            "$baseURL?${params.joinToString("&") { "${it.first}=${transformParam(it.second)}" }}"
        else
            baseURL
    }

    private fun transformParam(value: Any?): Any?
    {
        return if (value is RawRepresentable<*>)
            value.rawValue
        else
            value
    }
}

fun buildURL(path: String, transform: (URLWithQueryBuilder.() -> Unit)): String
{
    return URLWithQueryBuilder(path).apply(transform).build()
}
