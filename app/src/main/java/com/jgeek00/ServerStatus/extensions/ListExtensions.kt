package com.jgeek00.ServerStatus.extensions

fun <T> List<T>.padEnd(size: Int, element: T): List<T> {
    return this + List(maxOf(0, size - this.size)) { element }
}