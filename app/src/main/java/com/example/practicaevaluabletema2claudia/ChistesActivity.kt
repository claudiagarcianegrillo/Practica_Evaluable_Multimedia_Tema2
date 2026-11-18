package com.example.practicaevaluabletema2claudia

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaevaluabletema2claudia.databinding.ActivityChistesBinding
import kotlinx.coroutines.launch
import java.util.Locale

class ChistesActivity : AppCompatActivity() {

    //Nos da acceso a todas las vistas del layout activity_chistes.xml
    //y lateinit para que la variable se inicialice más tarde
    private lateinit var bindingMain: ActivityChistesBinding

    //Este es un objeto que convierte el texto en voz
    private lateinit var tts: TextToSpeech

    //Esto es una variable para saber si la voz está activada en la configuración
    private var voz = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inflamos las vistas para así poder accerder al activity de los chistes
        bindingMain = ActivityChistesBinding.inflate(layoutInflater)
        //Cargamos la interfaz en el Activity
        setContentView(bindingMain.root)

        //Leemos de SharedPreferences si la voz está activada o no
        voz = getSharedPreferences("config", MODE_PRIVATE).getBoolean("voz", true)

        //Mientras el motor de voz arranca el botón va a estar desactivado y se ve la rueda de progreso
        bindingMain.btnChiste.isEnabled = false

        //Inicializamos el motor de TextToSpeech, esto tarda unos segundos
        tts = TextToSpeech(this) { status ->

            //Ocultamos la rueda de progreso porque ya ha terminado de cargar
            bindingMain.progressChistes.visibility = View.GONE

            //Comprobamos que el motor se haya inciado correctamente
            if (status == TextToSpeech.SUCCESS) {

                //Configuramos el idioma a Español
                tts.language = Locale("es", "ES")

                //Ya se puede pulsar el botón
                bindingMain.btnChiste.isEnabled = true
            } else {

                //Si falla el motor avisamos con un Toast
                Toast.makeText(this, getString(R.string.error_voz), Toast.LENGTH_LONG).show()
            }
        }

        //Cuando pulsemos el botón se elige un chiste al azar
        bindingMain.btnChiste.setOnClickListener {
            //Desactivamos el botón mientras "calcula" el chiste para que se luzca la rueda
            bindingMain.btnChiste.isEnabled = false
            bindingMain.progressChistes.visibility = View.VISIBLE
            bindingMain.txtChiste.text = ""

            /*He puesto esto para que se vea el progressbar
            *Esto lo que hace es abrir una corrutina en el hilo
            * principal para modificar la interfaz gráfica*/
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                //Esto para la ejecución 1.5 segundos para que se vea algo la progressbar en el video
                kotlinx.coroutines.delay(1500)
                //Pedimos al repositorio un chiste aleatorio
                val chiste = ChistesRepositorio.aleatorio()
                //Ocultamos el progressbar una vez que haya acabado el tiempo
                bindingMain.progressChistes.visibility = View.GONE
                //También vamos a mostrar el chiste en texto
                bindingMain.txtChiste.text = chiste
                //Volvemos a activar el botón para poder pulsar de nuevo
                bindingMain.btnChiste.isEnabled = true

                //Si el usuario tiene la opción de la voz activada se lee mediante eso
                if (voz) {
                    //QUEUE_FLUSH corta el chiste anterior si se pulsa demasiado rápido y empieza otro nuevo
                    tts.speak(chiste, TextToSpeech.QUEUE_FLUSH, null, "chiste")
                }
            }
        }

        //Botón para volver a la pantalla principal
        bindingMain.btnVolver.setOnClickListener {
            finish()
        }
    }

    //Este metodo se ejecuta cuando la pantalla se destruye
    override fun onDestroy() {
        //Comprobamos que el motor de voz se llegó a inicializar para evitar errores
        if (::tts.isInitialized) {

            //Detenemos cualquier audio que se esté reproduciendo
            tts.stop()
            //Y liberamos los recursos del motor de voz
            tts.shutdown()
        }
        super.onDestroy()
    }
}