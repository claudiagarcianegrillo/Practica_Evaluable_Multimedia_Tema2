package com.example.practicaevaluabletema2claudia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaevaluabletema2claudia.databinding.ActivityDadosBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DadosActivity : AppCompatActivity() {

    //Nos da acceso a todas las vistas
    //lateinit para que la variable se inicialice más tarde (en el onCreate)
    private lateinit var bindingMain: ActivityDadosBinding

    //Aquí guardamos la suma de la última tirada de los 3 dados
    private var sum: Int = 0

    //Número entre 3 y 18 que ha escrito el usuario
    private var numeroUsuario: Int = 0

    //scope de las corrutinas, puesto como atributo de la clase para poder cancelarlo en onDestroy
    //dispatchers.Main es que las corrutinas se ejecutan en el hilo principal, es decir, el de la interfaz
    private val scope = CoroutineScope(Dispatchers.Main)

    //Se ejecuta al crear la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        //Llamamos al onCreate de la clase padre
        super.onCreate(savedInstanceState)
        //Convierte el XML en objetos en memoria y accedemos a las vistas
        bindingMain = ActivityDadosBinding.inflate(layoutInflater)
        //Carga la interfaz en el Activity
        setContentView(bindingMain.root)
        //Configuramos lo que va a pasar al pulsar el cubilete
        initEvent()
    }

    //Se ejecuta cuando la pantalla se destruye
    override fun onDestroy() {
        //Llamamos al onDestroy de la clase padre que es obligatorio sobreescribirlo
        super.onDestroy()
        //Cancelamos el scope por lo que se paran las corrutinas que sigan en marcha
        scope.cancel()
    }

    //Configuramos el evento del cubilete
    private fun initEvent() {
        //Al empezar el número del resultado está oculto
        bindingMain.txtResultado.visibility = View.INVISIBLE

        //Configuramos el botón para volver a la pantalla principal
        bindingMain.btnVolver.setOnClickListener {
            // Creamos el Intent para regresar a PrincipalActivity
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            // Cerramos esta actividad
            finish()
        }

        //Este código se ejecuta cuando el usuario pulsa el cubilete
        bindingMain.imageButton.setOnClickListener {
            //Leemos el texto escrito y lo pasamos a entero
            //Si está vacío o no es un número, toIntOrNull() que devuelva null
            val numero = bindingMain.edtNumero.text.toString().toIntOrNull()

            //Comprobamos que el número sea válido entre 3 y 18
            //Con 3 dados el mínimo posible 3 y el máximo 18
            if (numero == null || numero < 3 || numero > 18) {
                //Si el número es no válido avisamos con un Toast y no empieza la partida
                Toast.makeText(this, getString(R.string.error_rango_dados), Toast.LENGTH_SHORT).show()
            } else {
                //Si el número es válido lo guardamos para compararlo después con la tirada
                numeroUsuario = numero
                //Mostramos el resultado que antes estaba oculto
                bindingMain.txtResultado.visibility = View.VISIBLE
                //Desactivamos el cubilete y el campo de texto mientras dura la partida,
                // para asegurarnos que el usuario no puede pulsar otra vez hasta que termine
                bindingMain.imageButton.isEnabled = false
                bindingMain.edtNumero.isEnabled = false
                //Empieza el juego
                game()
            }
        }
    }

    //Arranca el juego
    private fun game() {
        //Programamos las tiradas
        scheduleRun()
    }

    //Programa las 5 tiradas y el resultado final usando una corrutina
    private fun scheduleRun() {
        //Launch lo que hace es iniciar una corrutina dentro del scope
        scope.launch {
            //Ejecutamos el bloque 5 veces
            repeat(5) {
                //Esperamos 1 segundo sin bloquear el hilo
                delay(1000)
                //Lanzamos los tres dados
                lanzarDados()
            }
            //Esperamos 2 segundos más ya que la última tirada fue a los 5 segundos y el resultado sale a los 7
            delay(2000)
            //Mostramos el resultado y comprobamos si ha acertado
            mostrarResultado()
        }
    }

    //Lanzamos los tres dados con tres números aleatorios
    private fun lanzarDados() {
        //Creamos 3 posiciones y rellenamos cada una con un número aleatorio
        //nextInt(1, 7) da un valor de 1 a 6 (el límite superior, el 7, no entra)
        val numDados = Array(3) { (1..6).random() }

        //Creamos un Array con los 3 ImageView de los dados en el mismo orden que el numDados
        val imagViews: Array<ImageView> = arrayOf(
            bindingMain.imagviewDado1,
            bindingMain.imagviewDado2,
            bindingMain.imagviewDado3
        )

        //Sumamos los tres valores y nos quedamos con el total de esta tirada
        sum = numDados.sum()

        //Recorremos las posiciones 0, 1 y 2 y cambiamos la imagen de cada dado según el número que le haya tocado
        for (i in 0..2)
            ponerImagenDado(imagViews[i], numDados[i])
    }

    //Ponemos en el ImageView la imagen del dado que corresponde al valor v
    private fun ponerImagenDado(imgV: ImageView, v: Int) {
        //Según el valor de v, elige qué imagen poner
        //En java sería con switch, y en kotlin es con when
        when (v) {
            1 -> imgV.setImageResource(R.drawable.dado1)
            2 -> imgV.setImageResource(R.drawable.dado2)
            3 -> imgV.setImageResource(R.drawable.dado3)
            4 -> imgV.setImageResource(R.drawable.dado4)
            5 -> imgV.setImageResource(R.drawable.dado5)
            6 -> imgV.setImageResource(R.drawable.dado6)
        }
    }

    //Mostramos la suma de la última tirada y comprobamos si el programa ha acertado
    private fun mostrarResultado() {
        //Escribimos la suma en el TextView
        bindingMain.txtResultado.text = sum.toString()

        //Si la suma coincide con el número que eligió el usuario, el programa ha acertado
        if (sum == numeroUsuario) {
            //Mostramos un diálogo de acierto
            Toast.makeText(this, getString(R.string.toast_acierto, numeroUsuario), Toast.LENGTH_LONG).show()
        } else {
            //Si no coincide vamos a avisar con un Toast
            Toast.makeText(this, getString(R.string.toast_fallo, sum), Toast.LENGTH_SHORT).show()
        }

        //La partida ha terminado y volvemos a activar el cubilete y el campo de texto
        bindingMain.imageButton.isEnabled = true
        bindingMain.edtNumero.isEnabled = true
        bindingMain.btnVolver.isEnabled = true
    }
}