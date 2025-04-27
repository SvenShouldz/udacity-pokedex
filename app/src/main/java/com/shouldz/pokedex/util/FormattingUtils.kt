package com.shouldz.pokedex.util // Adjust package if needed

import com.shouldz.pokedex.data.model.StatSlot
import com.shouldz.pokedex.data.model.TypeSlot
import java.util.Locale

// Formats raw stat name ("special-attack") to ("Special Attack")
fun formatStatName(rawStatName: String): String {
    return rawStatName.split("-").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}

// Capitalize first letter
fun capitalizeFirstLetter(name: String): String {
    return name.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

// Formats List of Type into single String
fun formatTypeList(types: List<TypeSlot>): String {
    return types.joinToString(" / ") { typeSlot ->
        capitalizeFirstLetter(typeSlot.type.name)
    }
}

// Formats List of Stats into single String by count and offset
fun formatStatsList(stats: List<StatSlot>, offset: Int, count: Int): String {
    if (offset < 0 || offset >= stats.size) {
        return "" // Return empty string if offset is out of bounds
    }
    val subList = stats.drop(offset).take(count)

    // Format the sublist
    return subList.joinToString("\n") { statSlot ->
        val name = formatStatName(statSlot.stat.name)
        val value = statSlot.baseStat
        "$name: $value"
    }
}


