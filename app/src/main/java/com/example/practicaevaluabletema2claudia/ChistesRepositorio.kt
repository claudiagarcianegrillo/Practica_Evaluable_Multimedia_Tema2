package com.example.practicaevaluabletema2claudia

// Objeto con la lista de los 10 chistes
// Al ser un object no hace falta crear ninguna instancia y se usa directamente con ChistesRepositorio.aleatorio()
object ChistesRepositorio {

    //Creamos la lista de los 10 chistes
    private val chistes = listOf(
        "¿Qué hace una abeja en el gimnasio? ¡Zumba!",
        "— Papá, papá, ¿qué se siente tener un hijo tan guapo? — No lo sé, hijo, pregúntale a tu abuelo.",
        "¿Qué le dice un gusanito a otro? Voy a dar una vuelta a la manzana.",
        "¿Por qué las focas del circo miran siempre hacia arriba? Porque ahí están los focos.",
        "¿Qué le dice una iguana a otra? ¡Iguana que tú!",
        "¿Qué hace un pez en el agua? ¡Nada!",
        "¿Por qué los pájaros vuelan hacia el sur en invierno? Porque caminando tardan demasiado.",
        "— Camarero, hay una mosca en mi sopa. — No se preocupe, el precio no incluye la carne.",
        "¿Qué le dice una pared a otra? Nos vemos en la esquina.",
        "¿Cómo se despide un químico? ¡Ácido un placer!"
    )

    //Esta es la posición del último chiste contado, para que no se repita 2 veces seguidas
    private var ultimo = -1

    //Creamos una funcion que devuelve un chiste al azar distinto del anterior
    fun aleatorio(): String {
        var posicion: Int
        do {
            //'indices' es el rango 0..9 y 'random()' elige una posición al azar
            posicion = chistes.indices.random()
        } while (posicion == ultimo)
        //Guardamos la posición para la próxima vez
        ultimo = posicion
        return chistes[posicion]
    }
}