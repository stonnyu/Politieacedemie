package tech.tucano.pmp_politie.UI.Helpers

fun stringGenerator(length: Int) : String {
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length).map { charset.random() }.joinToString("")
}